package vn.viettel.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.report.service.ChangePriceReportService;
import vn.viettel.report.service.dto.ChangePriceDTO;
import vn.viettel.report.service.dto.ChangePricePrintDTO;
import vn.viettel.report.service.dto.ChangePriceSubTotalDTO;
import vn.viettel.report.service.dto.ChangePriceTotalDTO;
import vn.viettel.report.service.feign.ShopClient;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.time.LocalDate;
import java.util.*;

@Service
public class ChangePriceReportServiceImpl implements ChangePriceReportService {
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ShopClient shopClient;

    @Override
    public Object index(String searchKey, LocalDate fromTransDate, LocalDate toTransDate, LocalDate fromOrderDate, LocalDate toOrderDate, String ids, Pageable pageable, Boolean isPaging) {
        StoredProcedureQuery storedProcedure =
                entityManager.createStoredProcedureQuery("P_CHANGE_PRICE", ChangePriceDTO.class);
        storedProcedure.registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR);
        storedProcedure.registerStoredProcedureParameter(2, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(3, LocalDate.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(4, LocalDate.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(5, LocalDate.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(6, LocalDate.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(7, String.class, ParameterMode.IN);
        String ra = "";
        if(ids!=null){
            String rs[] = ids.split(",");
            for(int i = 0;i< rs.length;i++){
                if(i == rs.length-1)
                    ra +=rs[i].trim();
                if(i< rs.length-1)
                    ra +=rs[i].trim()+",";
            }
        }
        storedProcedure.setParameter(2, searchKey);
        storedProcedure.setParameter(3, fromTransDate);
        storedProcedure.setParameter(4, toTransDate);
        storedProcedure.setParameter(5, fromOrderDate);
        storedProcedure.setParameter(6, toOrderDate);
        storedProcedure.setParameter(7, ra == "" ? null : ra);

        List<ChangePriceDTO> result = storedProcedure.getResultList();
        entityManager.close();
        if (result.isEmpty())
            return new Response<CoverResponse<Page<ChangePriceDTO>, ChangePriceTotalDTO>>()
                    .withData(new CoverResponse<>(new PageImpl<>(new ArrayList<>()), null));

        ChangePriceDTO changePriceTotal = result.get(result.size() - 1);
        List<ChangePriceDTO> response = result.subList(0, result.size() - 2);
        Collections.sort(response, Comparator.comparing(ChangePriceDTO::getOrderDate, Comparator.reverseOrder()));
        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), response.size());
        List<ChangePriceDTO> subList = response.subList(start, end);

        if (isPaging)
            return new Response<CoverResponse<Page<ChangePriceDTO>, ChangePriceTotalDTO>>().withData(new CoverResponse<>(new PageImpl<>(subList,pageable,response.size()),
                    new ChangePriceTotalDTO(changePriceTotal.getRedInvoiceNo(), changePriceTotal.getId(),changePriceTotal.getOrderDate(),changePriceTotal.getPoNumber(),changePriceTotal.getInternalNumber(),changePriceTotal.getTransCode(), changePriceTotal.getQuantity(), changePriceTotal.getTotalInput(), changePriceTotal.getTotalOutput())));
        else
            return new Response<CoverResponse<List<ChangePriceDTO>, ChangePriceTotalDTO>>().withData(new CoverResponse<>(response,
                    new ChangePriceTotalDTO(changePriceTotal.getRedInvoiceNo(), changePriceTotal.getId(),changePriceTotal.getOrderDate(),changePriceTotal.getPoNumber(),changePriceTotal.getInternalNumber(),changePriceTotal.getTransCode(), changePriceTotal.getQuantity(), changePriceTotal.getTotalInput(), changePriceTotal.getTotalOutput())));
    }

    @Override
    public ChangePricePrintDTO getAll(String searchKey, Long shopId, LocalDate fromTransDate, LocalDate toTransDate, LocalDate fromOrderDate, LocalDate toOrderDate, String ids, Pageable pageable) {

        Response<CoverResponse<List<ChangePriceDTO>, ChangePriceTotalDTO>> data =
                (Response<CoverResponse<List<ChangePriceDTO>, ChangePriceTotalDTO>>) index(searchKey, fromTransDate, toTransDate, fromOrderDate, toOrderDate, ids, pageable, false);
        ChangePricePrintDTO response = new ChangePricePrintDTO();
        List<ChangePriceDTO> listPriceChange = data.getData().getResponse();
        ChangePriceTotalDTO changePriceTotal = data.getData().getInfo();

        List<CoverResponse<ChangePriceSubTotalDTO, List<ChangePriceDTO>>> coverResponse = new ArrayList<>();
        List<ChangePriceSubTotalDTO> subTotals = new ArrayList<>();

        for (ChangePriceDTO changePrice : listPriceChange) {
            if (!subTotals.stream().anyMatch(e -> e.getPoNumber().equals(changePrice.getPoNumber()))) {
                ChangePriceSubTotalDTO total = new ChangePriceSubTotalDTO();
                total.setPoNumber(changePrice.getPoNumber());
                total.setTransCode(changePrice.getTransCode());
                total.setInternalNumber(changePrice.getInternalNumber());
                total.setRedInvoiceNo(changePrice.getRedInvoiceNo());
                total.setOrderDate(changePrice.getOrderDate());
                subTotals.add(total);
            }
        }
        for (ChangePriceSubTotalDTO poNum : subTotals) {
            CoverResponse<ChangePriceSubTotalDTO, List<ChangePriceDTO>> subResponse = new CoverResponse<>();

            long totalQuantity = 0;
            double totalPriceInput = 0;
            double totalPriceOutput = 0;

            List<ChangePriceDTO> subParent = new ArrayList<>();
            for (ChangePriceDTO changePrice : listPriceChange) {
                if (changePrice.getPoNumber().equals(poNum.getPoNumber())) {
                    subParent.add(changePrice);
                    if(changePrice.getQuantity()!=null)
                    totalQuantity += changePrice.getQuantity();
                    if(changePrice.getTotalInput()!=null)
                    totalPriceInput += changePrice.getTotalInput();
                    if(changePrice.getTotalOutput()!=null)
                    totalPriceOutput += changePrice.getTotalOutput();
                }
            }
            poNum.setTotalQuantity(totalQuantity);
            poNum.setTotalPriceInput(totalPriceInput);
            poNum.setTotalPriceOutput(totalPriceOutput);

            subResponse.setResponse(poNum);
            subResponse.setInfo(subParent);

            coverResponse.add(subResponse);
        }

        ShopDTO shopDTO = shopClient.getShopByIdV1(shopId).getData();
        response.setShop(shopDTO);
        response.setFromDate(DateUtils.convertFromDate(fromTransDate));
        response.setToDate(DateUtils.convertFromDate(toTransDate));
        response.setTotalQuantity(changePriceTotal.getTotalQuantity());
        response.setTotalPriceInput(changePriceTotal.getTotalPriceInput());
        response.setTotalPriceOutput(changePriceTotal.getTotalPriceOutput());
        response.setDetails(coverResponse);
        return response;
    }
}

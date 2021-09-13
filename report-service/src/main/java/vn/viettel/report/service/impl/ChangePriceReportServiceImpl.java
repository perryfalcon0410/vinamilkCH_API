package vn.viettel.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseReportServiceImpl;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.report.messaging.ChangePriceFilter;
import vn.viettel.report.service.ChangePriceReportService;
import vn.viettel.report.service.dto.ChangePriceDTO;
import vn.viettel.report.service.dto.ChangePricePrintDTO;
import vn.viettel.report.service.dto.ChangePriceSubTotalDTO;
import vn.viettel.report.service.dto.ChangePriceTotalDTO;
import vn.viettel.report.service.feign.ShopClient;

import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class ChangePriceReportServiceImpl extends BaseReportServiceImpl implements ChangePriceReportService {

    @Autowired
    ShopClient shopClient;

    @Override
    public Object index(ChangePriceFilter filter, Pageable pageable, Boolean isPaging) {
        StoredProcedureQuery storedProcedure =
                entityManager.createStoredProcedureQuery("P_CHANGE_PRICE", ChangePriceDTO.class);
        storedProcedure.registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR);
        storedProcedure.registerStoredProcedureParameter(2, Long.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(3, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(4, LocalDateTime.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(5, LocalDateTime.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(6, LocalDateTime.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(7, LocalDateTime.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(8, String.class, ParameterMode.IN);
        String ra = "";
        if(filter.getIds()!=null){
            String rs[] = filter.getIds().split(",");
            for(int i = 0;i< rs.length;i++){
                if(i == rs.length-1)
                    ra +=rs[i].trim();
                if(i< rs.length-1)
                    ra +=rs[i].trim()+",";
            }
        }
        storedProcedure.setParameter(2, filter.getShopId());
        storedProcedure.setParameter(3, filter.getSearchKey());
        storedProcedure.setParameter(4, filter.getFromTransDate());
        storedProcedure.setParameter(5, filter.getToTransDate());
        storedProcedure.setParameter(6, filter.getFromOrderDate());
        storedProcedure.setParameter(7, filter.getToOrderDate());
        storedProcedure.setParameter(8, ra == "" ? null : ra);

        this.executeQuery(storedProcedure, "P_CHANGE_PRICE", filter.toString());

        List<ChangePriceDTO> result = storedProcedure.getResultList();


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
    public ChangePricePrintDTO getAll(ChangePriceFilter filter, Pageable pageable) {

        Response<CoverResponse<List<ChangePriceDTO>, ChangePriceTotalDTO>> data =
                (Response<CoverResponse<List<ChangePriceDTO>, ChangePriceTotalDTO>>) index( filter, pageable, false);
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

        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
        if(shopDTO == null) throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
        response.setShop(shopDTO);
        response.setFromDate(DateUtils.convertFromDate(filter.getFromTransDate()));
        response.setToDate(DateUtils.convertFromDate(filter.getToTransDate()));
        response.setTotalQuantity(changePriceTotal.getTotalQuantity());
        response.setTotalPriceInput(changePriceTotal.getTotalPriceInput());
        response.setTotalPriceOutput(changePriceTotal.getTotalPriceOutput());
        response.setDetails(coverResponse);
        return response;
    }
}

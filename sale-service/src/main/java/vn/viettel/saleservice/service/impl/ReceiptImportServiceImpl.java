package vn.viettel.saleservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.repository.*;
import vn.viettel.saleservice.service.ReceiptImportService;
import vn.viettel.saleservice.service.dto.ReceiptCreateRequest;
import vn.viettel.saleservice.service.dto.ReceiptImportDTO;
import vn.viettel.saleservice.service.dto.ReceiptSearch;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ReceiptImportServiceImpl implements ReceiptImportService {
    @Autowired
    ReceiptImportRepository receiptImportRepository;
    @Autowired
    WareHouseRepository wareHouseRepository;
    @Autowired
    ShopRepository shopRepository;
    @Autowired
    POConfirmRepository poConfirmRepository;
    @Autowired
    POBorrowRepository poBorrowRepository;
    @Autowired
    POAdjustedRepository poAdjustedRepository;
    @Autowired
    StockTotalRepository stockTotalRepository;

    @Override
    public Response<List<ReceiptImportDTO>> getAll(ReceiptSearch receiptSearch) {
        List<ReceiptImport> reci = receiptImportRepository.getReceiptImportByVariable(receiptSearch.getFromDate(), receiptSearch.getToDate(), receiptSearch.getInvoiceNumber(), receiptSearch.getReceiptType());
        List<ReceiptImportDTO> reciLst = new ArrayList<>();
        for (ReceiptImport r : reci) {
            ReceiptImportDTO reciDTO = new ReceiptImportDTO();
            reciDTO.setId(r.getId());
            reciDTO.setReceiptCode(r.getReceiptCode());
            reciDTO.setInvoiceDate(r.getInvoiceDate());
            reciDTO.setReceiptTotal(r.getReceiptTotal());
            reciDTO.setNote(r.getNote());
            reciDTO.setInternalNumber(r.getInternalNumber());
            reciDTO.setInvoiceNumber(r.getInvoiceNumber());
            reciDTO.setReceiptQuantity(r.getReceiptQuantity());
            reciLst.add(reciDTO);
        }
        Response<List<ReceiptImportDTO>> response = new Response<>();
        response.setData(reciLst);
        return response;
    }

    @Override
    public Response<ReceiptImport> createReceiptImport(ReceiptCreateRequest reccr, long userId, long idShop) {
        Response<ReceiptImport> response = new Response<>();
        if (reccr == null) {
            response.setFailure(ResponseMessage.NO_CONTENT);
            return response;
        }
        if (checkUserExist(userId) == null) {
            response.setFailure(ResponseMessage.NO_CONTENT);
            //return response;
        }
        String str = reccr.getInvoiceDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime time = LocalDateTime.parse(str, formatter);
        ReceiptImport reci = new ReceiptImport();
        WareHouse wareHouse = wareHouseRepository.findById(reccr.getWareHouseId()).get();

        Date date = new Date();
        LocalDateTime dateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        reci.setCreatedAt(dateTime);
        reci.setCreatedBy(userId);
        reci.setInvoiceDate(time);
        reci.setReceiptDate(dateTime);
        reci.setWareHouse(wareHouse);
        reci.setReceiptType(reccr.getReceiptType());
        reci.setReceiptCode(createReceiptImportCode(idShop));
        StockTotal stockTotal = stockTotalRepository.findById(wareHouse.getId()).get();
        if(stockTotal == null)
            response.setFailure(ResponseMessage.NO_CONTENT);
        if(stockTotal.getQuantity() == null){
            stockTotal.setQuantity(0);
        }
        if (reccr.getReceiptType() == 1) {
            POConfirm poConfirm = poConfirmRepository.findById(reccr.getPoId()).get();
            reci.setPoNumber(poConfirm.getPoNo());
        }
        if (reccr.getReceiptType() == 2) {
            reci.setPoNumber(poBorrowRepository.findById(reccr.getPoId()).get().getPoBorrowNumber());
        }
        if (reccr.getReceiptType() == 3) {
            reci.setPoNumber(poAdjustedRepository.findById(reccr.getPoId()).get().getPoLicenseNumber());
        }
        reci.setNote(reccr.getNote());

        receiptImportRepository.save(reci);
        response.setData(reci);
        return response;
    }

    @Override
    public Response<ReceiptImport> updateReceiptImport(ReceiptCreateRequest reccr, long userId) {
        Response<ReceiptImport> response = new Response<>();
        ReceiptImport recei = receiptImportRepository.findById(reccr.getId()).get();
        if (recei != null) {
            Date date = new Date();
            LocalDateTime dateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            recei.setUpdatedBy(userId);
            recei.setUpdatedAt(dateTime);
            recei.setInvoiceNumber(reccr.getInvoiceNumber());
            if(recei.getReceiptType()!=1){
                recei.setInternalNumber(reccr.getInternalNumber());
            }
            if(recei.getReceiptType()== 2){
                recei.setPoNumber(poBorrowRepository.findById(reccr.getPoId()).get().getPoBorrowNumber());
            }
            if(recei.getReceiptType()== 3){
                recei.setPoNumber(poAdjustedRepository.findById(reccr.getPoId()).get().getPoLicenseNumber());
            }
            recei.setNote(reccr.getNote());
            receiptImportRepository.save(recei);
            response.setData(recei);
        } else {
            response.setFailure(ResponseMessage.NO_CONTENT);
        }
        return response;
    }

    @Override
    public void remove(long[] ids) {
        for(long id: ids) {
            receiptImportRepository.deleteById(id);
        }
    }


    @Override
    public User checkUserExist(long userId) {
        //       User user = userClient.getUserById(userId);
//        return user == null ? null : user;
        return null;
    }

    @Override
    public String createReceiptImportCode(Long idShop) {
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String yy = df.format(Calendar.getInstance().getTime());
        int reciNum = receiptImportRepository.getReceiptImportNumber();
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("IMP.");
        reciCode.append(shopRepository.getShopById(idShop).getShopCode().toString());
        reciCode.append(".");
        reciCode.append(yy);
        reciCode.append(".");
        reciCode.append(formatReceINumber(reciNum));

        return reciCode.toString();
    }

    @Override
    public Response<ReceiptImportDTO> getReceiptImportById(Long receiID){
        Response response = new Response();
        ReceiptImportDTO receiDTO = new ReceiptImportDTO();
        try {
            ReceiptImport reci = receiptImportRepository.findById(receiID).get();
            receiDTO.setReceiptCode(reci.getReceiptCode());
            receiDTO.setReceiptType(reci.getReceiptType());
            receiDTO.setWareHouseId(reci.getWareHouse().getId());
            receiDTO.setInvoiceNumber(reci.getInvoiceNumber());
            receiDTO.setInvoiceDate(reci.getInvoiceDate());
            receiDTO.setInternalNumber(reci.getInternalNumber());
            receiDTO.setNote(reci.getNote());
            response.setData(receiDTO);
            return response;
        }catch (Exception e){
            response.setFailure(ResponseMessage.NO_CONTENT);
            return response;
        }
    }

    public String formatReceINumber(int number) {
        StringBuilder recei_num = new StringBuilder();
        int num = number + 1;

        if (num < 10) {
            recei_num.append("0000");
        }
        if (num < 100 && num >= 10) {
            recei_num.append("000");
        }
        if (num < 1000 && num >= 100) {
            recei_num.append("00");
        }
        if (num < 10000 && num >= 1000) {
            recei_num.append("0");
        }
        recei_num.append(num);

        return recei_num.toString();
    }
}

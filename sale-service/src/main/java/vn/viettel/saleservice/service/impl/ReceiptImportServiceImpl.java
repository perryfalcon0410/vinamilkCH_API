package vn.viettel.saleservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.repository.*;
import vn.viettel.saleservice.service.ReceiptImportService;
import vn.viettel.saleservice.service.dto.*;

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
    @Autowired
    SOConfirmRepository soConfirmRepository;
    @Autowired
    POBorrowDetailRepository poBorrowDetailRepository;
    @Autowired
    POAdjustedDetailRepository poAdjustedDetailRepository;
    @Autowired
    PoPromotionalDetailRepository poPromotionalDetailRepository;
    @Autowired
    PoPromotionalRepository poPromotionalRepository;

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
        final int DANHAPHANG = 0;
        final int CHUANHAPHANG = 1;
        Date date = new Date();
        LocalDateTime dateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        ReceiptImport reci = new ReceiptImport();
        WareHouse wareHouse = wareHouseRepository.findById(reccr.getWareHouseId()).get();
        /*String str = reccr.getInvoiceDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime time = LocalDateTime.parse(str, formatter);*/
        reci.setReceiptDate(dateTime);
        reci.setReceiptCode(createReceiptImportCode(idShop));
        reci.setWareHouse(wareHouse);
        reci.setReceiptType(reccr.getReceiptType());
        if (reccr.getReceiptType() == 0) {
            POConfirm poConfirm = poConfirmRepository.findById(reccr.getPoId()).get();
            List<SOConfirm> soConfirms = soConfirmRepository.getListSoConfirm(poConfirm.getPoNo());
            reci.setInvoiceDate(poConfirm.getPoDate());
            reci.setInternalNumber(poConfirm.getInternalNumber());
            reci.setPoNumber(poConfirm.getPoNo());
            for(SOConfirm soc : soConfirms){
                StockTotal stockTotal = stockTotalRepository.findStockTotalConfirmByProductId(soc.getProduct_Id());
                if(stockTotal == null)
                    response.setFailure(ResponseMessage.NO_CONTENT);
                if(stockTotal.getQuantity() == null){
                    stockTotal.setQuantity(0);
                }
                stockTotal.setQuantity(stockTotal.getQuantity()+ soc.getQuantity());
                stockTotalRepository.save(stockTotal);
            }
            poConfirm.setStatus(DANHAPHANG);
            poConfirmRepository.save(poConfirm);
        }
        if (reccr.getReceiptType() == 2) {
            POBorrow poBorrow = poBorrowRepository.findById(reccr.getPoId()).get();
            List<POBorrowDetail> poBorrowDetails = poBorrowDetailRepository.getListPoBorrowDetail(poBorrow.getPoBorrowNumber());
            reci.setInvoiceDate(poBorrow.getPoDate());
            reci.setPoNumber(poBorrow.getPoBorrowNumber());
            for(POBorrowDetail pbd : poBorrowDetails){
                StockTotal stockTotal = stockTotalRepository.findStockTotalBorrowByProductId(pbd.getProductId());
                if(stockTotal == null)
                    response.setFailure(ResponseMessage.NO_CONTENT);
                if(stockTotal.getQuantity() == null){
                    stockTotal.setQuantity(0);
                }
                stockTotal.setQuantity(stockTotal.getQuantity()+ pbd.getQuantity());

                stockTotalRepository.save(stockTotal);

            }
            poBorrow.setStatus(DANHAPHANG);
            poBorrowRepository.save(poBorrow);
        }
        if (reccr.getReceiptType() == 1) {
            POAdjusted poAdjusted = poAdjustedRepository.findById(reccr.getPoId()).get();
            List<POAdjustedDetail> poAdjustedDetails = poAdjustedDetailRepository.getListPOAdjustedDetail(poAdjusted.getPoLicenseNumber());
            reci.setInvoiceDate(poAdjusted.getPoDate());
            reci.setPoNumber(poAdjusted.getPoLicenseNumber());
            for(POAdjustedDetail pad : poAdjustedDetails){
                StockTotal stockTotal = stockTotalRepository.findStockTotalAdjustedByProductId(pad.getProductId());
                if(stockTotal == null)
                    response.setFailure(ResponseMessage.NO_CONTENT);
                if(stockTotal.getQuantity() == null){
                    stockTotal.setQuantity(0);
                }
                stockTotal.setQuantity(stockTotal.getQuantity()+ pad.getQuantity());
                stockTotalRepository.save(stockTotal);
            }
            poAdjusted.setStatus(DANHAPHANG);
            poAdjustedRepository.save(poAdjusted);
        }
        reci.setNote(reccr.getNote());
        reci.setCreatedAt(dateTime);
        reci.setCreatedBy(userId);


        receiptImportRepository.save(reci);
        response.setData(reci);
        return response;
    }

    @Override
    public Response<ReceiptImport> createReceiptImportPromotional(ReceiptCreateRequest reccr, PoPromotionalDTO ppd, List<PoPromotionalDetailDTO> ppdds, long userId, long idShop) {
        Response<ReceiptImport> response = new Response<>();
       /* if (reccr == null) {
            response.setFailure(ResponseMessage.NO_CONTENT);
            return response;
        }
        if (checkUserExist(userId) == null) {
            response.setFailure(ResponseMessage.NO_CONTENT);
            //return response;
        }
        final int DANHAPHANG = 0;
        final int CHUANHAPHANG = 1;
        Date date = new Date();
        LocalDateTime dateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        ReceiptImport reci = new ReceiptImport();
        WareHouse wareHouse = wareHouseRepository.findById(reccr.getWareHouseId()).get();
        String str = reccr.getInvoiceDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime time = LocalDateTime.parse(str, formatter);
        reci.setReceiptDate(dateTime);
        reci.setInvoiceDate(time);
        reci.setReceiptCode(createReceiptImportCode(idShop));
        reci.setWareHouse(wareHouse);
        reci.setReceiptType(reccr.getReceiptType());
        PoPromotional poPromotional = new PoPromotional();
        poPromotional.setPo_PromotionalNumber(ppd.getPo_PromotionalNumber());
        poPromotional.setPoDate(ppd.getPoDate());
        poPromotional.setPoNote(ppd.getPoNote());
        poPromotional.setStatus(0);
        if (reccr.getReceiptType() == 4) {
            for(PoPromotionalDetailDTO ppdd:ppdds){
                PoPromotionalDetail poPromotionalDetail = new PoPromotionalDetail();
                poPromotionalDetail.
                ppdd.setProductCode(ppdd.getProductCode());
                ppdd.setProductName(ppdd.getProductName());
                ppdd.setProductPrice(ppdd.getProductPrice());
                ppdd.setQuantity(ppdd.getQuantity());
                ppdd.setUnit(ppdd.getUnit());
            }







            for(SOConfirm soc : soConfirms){
                StockTotal stockTotal = stockTotalRepository.findStockTotalConfirmByProductId(soc.getProduct_Id());
                if(stockTotal == null)
                    response.setFailure(ResponseMessage.NO_CONTENT);
                if(stockTotal.getQuantity() == null){
                    stockTotal.setQuantity(0);
                }
                stockTotal.setQuantity(stockTotal.getQuantity()+ soc.getQuantity());
                stockTotalRepository.save(stockTotal);
            }
            poConfirm.setStatus(DANHAPHANG);
            poConfirmRepository.save(poConfirm);
        }
        reci.setNote(reccr.getNote());
        reci.setCreatedAt(dateTime);
        reci.setCreatedBy(userId);


        receiptImportRepository.save(reci);
        response.setData(reci);*/
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
        final int DANHAPHANG = 0;
        final int CHUANHAPHANG = 1;
        for(long id: ids) {
            receiptImportRepository.deleteById(id);
            ReceiptImport receiptImport = receiptImportRepository.findById(id).get();
            if(receiptImport.getReceiptType() == 0)
            {
               List<SOConfirm> soConfirms = soConfirmRepository.getSOConfirmByPoNumber(receiptImport.getPoNumber());
               for(SOConfirm so : soConfirms){
                    StockTotal stockTotal = stockTotalRepository.findStockTotalConfirmByProductId(so.getProduct_Id());
                    stockTotal.setQuantity(stockTotal.getQuantity() - so.getQuantity());
                    stockTotalRepository.save(stockTotal);
               }
               POConfirm poConfirm = poConfirmRepository.findPOConfirmByPoNo(receiptImport.getPoNumber());
               poConfirm.setStatus(CHUANHAPHANG);
               poConfirmRepository.save(poConfirm);
            }
            if(receiptImport.getReceiptType() == 1)
            {
                List<POAdjustedDetail> poAdjustedDetails = poAdjustedDetailRepository.getPOAdjustedDetailByPoNumber(receiptImport.getPoNumber());
                for(POAdjustedDetail pad : poAdjustedDetails){
                    StockTotal stockTotal = stockTotalRepository.findStockTotalAdjustedByProductId(pad.getProductId());
                    stockTotal.setQuantity(stockTotal.getQuantity() - pad.getQuantity());
                    stockTotalRepository.save(stockTotal);
                }
                POAdjusted poAdjusted = poAdjustedRepository.findPOAdjustedByPoLicenseNumber(receiptImport.getPoNumber());
                poAdjusted.setStatus(CHUANHAPHANG);
                poAdjustedRepository.save(poAdjusted);
            }
            if(receiptImport.getReceiptType() == 2)
            {
                List<POBorrowDetail> poBorrowDetails = poBorrowDetailRepository.getPOBorrowDetailByPoNumber(receiptImport.getPoNumber());
                for(POBorrowDetail pbd : poBorrowDetails){
                    StockTotal stockTotal = stockTotalRepository.findStockTotalBorrowByProductId(pbd.getProductId());
                    stockTotal.setQuantity(stockTotal.getQuantity() - pbd.getQuantity());
                    stockTotalRepository.save(stockTotal);
                }
                POBorrow poBorrow = poBorrowRepository.findPOBorrowByPoBorrowNumber(receiptImport.getPoNumber());
                poBorrow.setStatus(CHUANHAPHANG);
                poBorrowRepository.save(poBorrow);
            }
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

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
    @Autowired
    ProductRepository productRepository;

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
    public Response<ReceiptImport> createReceiptImport(POPromotionalRequest pro, long userId, long idShop) {
        Response<ReceiptImport> response = new Response<>();
        if (pro.getReccr() == null) {
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
        WareHouse wareHouse = wareHouseRepository.findById(pro.getReccr().getWareHouseId()).get();
        reci.setReceiptDate(dateTime);
        reci.setReceiptCode(createReceiptImportCode(idShop));
        reci.setWareHouse(wareHouse);
        reci.setReceiptType(pro.getReccr().getReceiptType());
        if (pro.getReccr().getReceiptType() == 0) {
            POConfirm poConfirm = poConfirmRepository.findById(pro.getReccr().getPoId()).get();
            List<SOConfirm> soConfirms = soConfirmRepository.getListSoConfirm(poConfirm.getPoNo());
            reci.setInvoiceDate(poConfirm.getPoDate());
            reci.setInternalNumber(poConfirm.getInternalNumber());
            reci.setPoNumber(poConfirm.getPoNo());
            for(SOConfirm soc : soConfirms){
                StockTotal stockTotal = stockTotalRepository.findStockTotalByProductIdAndWareHouseId(soc.getProduct_Id(),pro.getReccr().getWareHouseId());
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
        if (pro.getReccr().getReceiptType() == 2) {
            POBorrow poBorrow = poBorrowRepository.findById(pro.getReccr().getPoId()).get();
            List<POBorrowDetail> poBorrowDetails = poBorrowDetailRepository.getListPoBorrowDetail(poBorrow.getPoBorrowNumber());
            reci.setInvoiceDate(poBorrow.getPoDate());
            reci.setPoNumber(poBorrow.getPoBorrowNumber());
            for(POBorrowDetail pbd : poBorrowDetails){
                StockTotal stockTotal = stockTotalRepository.findStockTotalByProductIdAndWareHouseId(pbd.getProductId(),pro.getReccr().getWareHouseId());
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
        if (pro.getReccr().getReceiptType() == 1) {
            POAdjusted poAdjusted = poAdjustedRepository.findById(pro.getReccr().getPoId()).get();
            List<POAdjustedDetail> poAdjustedDetails = poAdjustedDetailRepository.getListPOAdjustedDetail(poAdjusted.getPoLicenseNumber());
            reci.setInvoiceDate(poAdjusted.getPoDate());
            reci.setPoNumber(poAdjusted.getPoLicenseNumber());
            for(POAdjustedDetail pad : poAdjustedDetails){
                StockTotal stockTotal = stockTotalRepository.findStockTotalByProductIdAndWareHouseId(pad.getProductId(),pro.getReccr().getWareHouseId());
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
        if (pro.getReccr().getReceiptType() == 3) {
            String str = pro.getReccr().getInvoiceDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime time = LocalDateTime.parse(str, formatter);
            reci.setInvoiceDate(time);
            reci.setPoNumber(pro.getReccr().getPoNumber());
            PoPromotional poPromotional = createPoPromotional(pro.getPpd(),userId,pro.getReccr().getPoNumber());
            List<PoPromotionalDetail> poPromotionalDetailList = createPoPromotionalDetail(pro.getPpdds(),userId,poPromotional.getId());
            for(PoPromotionalDetail po : poPromotionalDetailList){
                Product product = productRepository.findByProductCode(po.getProductCode());
                StockTotal stockTotal = stockTotalRepository.findStockTotalByProductIdAndWareHouseId(product.getId(),pro.getReccr().getWareHouseId());
                if(stockTotal == null)
                    response.setFailure(ResponseMessage.NO_CONTENT);
                if(stockTotal.getQuantity() == null){
                    stockTotal.setQuantity(0);
                }
                stockTotal.setQuantity(stockTotal.getQuantity()+ po.getQuantity());
                stockTotalRepository.save(stockTotal);
            }
        }
        reci.setNote(pro.getReccr().getNote());
        reci.setCreatedAt(dateTime);
        reci.setCreatedBy(userId);
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
            recei.setInvoiceNumber(reccr.getInvoiceNumber());
            if(recei.getReceiptType()==3){
                recei.setInternalNumber(reccr.getInternalNumber());

                if(!reccr.getLstPoPromotionDetail().isEmpty()){
                    PoPromotional pop = poPromotionalRepository.findPoPromotionalByPoPromotionalNumber(recei.getPoNumber());
                    for(PoPromotionalDetail po :reccr.getLstPoPromotionDetail() ){
                        PoPromotionalDetail p = new PoPromotionalDetail();
                        p.setProductCode(po.getProductCode());
                        p.setProductName(po.getProductName());
                        p.setProductPrice(po.getProductPrice());
                        p.setQuantity(po.getQuantity());
                        p.setTotalPrice(po.getTotalPrice());
                        p.setUnit(po.getUnit());
                        p.setPoPromotionalId(pop.getId());
                        StockTotal stockTotal = stockTotalRepository.findStockTotalByProductIdAndWareHouseId(p.getProductId(),recei.getWareHouse().getId());
                        stockTotal.setQuantity(stockTotal.getQuantity()+p.getQuantity());
                        poPromotionalDetailRepository.save(p);
                        stockTotalRepository.save(stockTotal);
                    }
                }
                if(!reccr.getLstIdRemove().isEmpty()){
                    for(Long id : reccr.getLstIdRemove()){
                        PoPromotionalDetail po = poPromotionalDetailRepository.findById(id).get();
                        Product product = productRepository.findByProductCode(po.getProductCode());
                        StockTotal stockTotal = stockTotalRepository.findStockTotalByProductIdAndWareHouseId(product.getId(),recei.getWareHouse().getId());
                        stockTotal.setQuantity(stockTotal.getQuantity()- po.getQuantity());
                        poPromotionalDetailRepository.deleteById(id);
                        stockTotalRepository.save(stockTotal);
                    }
                }
            }
            recei.setPoNumber(reccr.getPoNumber());
            recei.setNote(reccr.getNote());
            recei.setUpdatedBy(userId);
            recei.setUpdatedAt(dateTime);
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
                    StockTotal stockTotal = stockTotalRepository.findStockTotalByProductIdAndWareHouseId(so.getProduct_Id(),receiptImport.getWareHouse().getId());
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
                    StockTotal stockTotal = stockTotalRepository.findStockTotalByProductIdAndWareHouseId(pad.getProductId(),receiptImport.getWareHouse().getId());
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
                    StockTotal stockTotal = stockTotalRepository.findStockTotalByProductIdAndWareHouseId(pbd.getProductId(),receiptImport.getWareHouse().getId());
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

    @Override
    public PoPromotional createPoPromotional(PoPromotionalDTO poPro, long userId,String poNumer) {
        if (poPro != null) {
            Date date = new Date();
            LocalDateTime dateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            PoPromotional poPromotional = new PoPromotional();
            poPromotional.setPoPromotionalNumber(poNumer);
            poPromotional.setStatus(0);
            poPromotional.setPoDate(poPro.getPoDate());
            poPromotional.setPoNote(poPro.getPoNote());
            poPromotional.setCreatedAt(dateTime);
            return poPromotionalRepository.save(poPromotional);
        }
        return null;
    }

    @Override
    public List<PoPromotionalDetail> createPoPromotionalDetail(List<PoPromotionalDetailDTO> ppdds, long userId,long poId) {
        if (ppdds != null) {
            Date date = new Date();
            LocalDateTime dateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            PoPromotional poPromotional = poPromotionalRepository.findById(poId).get();
            List<PoPromotionalDetail>poPromotionalDetailList = new ArrayList<>();
            for (PoPromotionalDetailDTO pod : ppdds){
                PoPromotionalDetail poPromotionalDetail = new PoPromotionalDetail();
                poPromotionalDetail.setPoPromotionalId(poPromotional.getId());
                poPromotionalDetail.setProductCode(pod.getProductCode());
                poPromotionalDetail.setProductName(pod.getProductName());
                poPromotionalDetail.setQuantity(pod.getQuantity());
                poPromotionalDetail.setUnit(pod.getUnit());
                poPromotionalDetail.setTotalPrice(pod.getTotalPrice());
                poPromotionalDetail.setProductPrice(pod.getProductPrice());
                poPromotionalDetail.setCreatedAt(dateTime);
                poPromotionalDetailList.add(poPromotionalDetail);
            }
            for (PoPromotionalDetail p :poPromotionalDetailList){
                poPromotionalDetailRepository.save(p);
            }
            return poPromotionalDetailList;
        }
        return null;
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

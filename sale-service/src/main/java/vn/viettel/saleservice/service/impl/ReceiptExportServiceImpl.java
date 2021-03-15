package vn.viettel.saleservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.repository.ReceiptExportRepository;
import vn.viettel.saleservice.repository.ReceiptImportRepository;
import vn.viettel.saleservice.repository.ShopRepository;
import vn.viettel.saleservice.repository.WareHouseRepository;
import vn.viettel.saleservice.service.ReceiptExportService;
import vn.viettel.saleservice.service.dto.*;
import vn.viettel.saleservice.service.feign.UserClient;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ReceiptExportServiceImpl implements ReceiptExportService {
    @Autowired
    ReceiptExportRepository receiptExportRepository;
    @Autowired
    WareHouseRepository wareHouseRepository;
    @Autowired
    ShopRepository shopRepository;
    @Autowired
    UserClient userClient;
    @Autowired
    ReceiptImportRepository receiptImportRepository;

    private Date date = new Date();
    private Timestamp dateTime = new Timestamp(date.getTime());

    @Override
    public Response<Page<ReceiptExportDTO>> getReceiptExportBySearch(ReceiptSearch receiptSearch, Pageable pageable) {
        Page<ReceiptExport> reci = receiptExportRepository.getReceiptExportByVariable(receiptSearch.getFromDate(),
                receiptSearch.getToDate(), receiptSearch.getInvoiceNumber(),
                receiptSearch.getReceiptType(),pageable);
        List<ReceiptExportDTO> reciLst = new ArrayList<>();
        for (ReceiptExport r : reci) {
            ReceiptExportDTO reciDTO = new ReceiptExportDTO();
            reciDTO.setId(r.getId());
            reciDTO.setReceiptExportCode(r.getReceiptExportCode());
            reciDTO.setInvoiceDate(r.getInvoiceDate());
            reciDTO.setReceiptExportTotal(r.getReceiptExportTotal());
            reciDTO.setNote(r.getNote());
            reciDTO.setInternalNumber(r.getInternalNumber());
            reciDTO.setInvoiceNumber(r.getInvoiceNumber());
            reciDTO.setReceiptExportQuantity(r.getReceiptExportQuantity());
            reciLst.add(reciDTO);
        }
        Response<Page<ReceiptExportDTO>> response = new Response<>();
        Page<ReceiptExportDTO> receiptResponses = new PageImpl<>(reciLst);
        response.setData(receiptResponses);
        return response;
    }

    @Override
    public Response<ReceiptExport> createReceiptExport(ReceiptExportRequest rexr, long userId, long idShop) {
       /* Response<ReceiptExport> response = new Response<>();
        User user = userClient.getUserById(userId);
        if(user == null){
            response.setFailure(ResponseMessage.USER_DOES_NOT_EXISTS);
            return response;
        }
        if (rexr == null) {
            response.setFailure(ResponseMessage.NO_CONTENT);
            return response;
        }


        final int DANHAPHANG = 0;
        final int CHUANHAPHANG = 1;
        ReceiptExport recx = new ReceiptExport();
        WareHouse wareHouse = wareHouseRepository.findById(rexr.getWareHouseId()).get();
        recx.setReceiptExportDate(dateTime);
        recx.setReceiptExportCode(createReceiptExportCode(idShop));
        recx.setWareHouse(wareHouse);
        recx.setReceiptExportType(rexr.getReceiptExportType());;
        if (rexr.getReceiptExportType() == 0) {
            ReceiptImport receiptImport = receiptImportRepository.findById(rexr.getReceiptImportId()).get();
            List<SOConfirm> soConfirms = soConfirmRepository.findAllByPoConfirmId(poConfirm.getId());
            reci.setInvoiceDate(poConfirm.getPoDate());
            reci.setInternalNumber(poConfirm.getInternalNumber());
            reci.setPoNumber(poConfirm.getPoNo());
            for(SOConfirm soc : soConfirms){
                Product products = productRepository.findByProductCode(soc.getProductCode());
                StockTotal stockTotal = stockTotalRepository.findStockTotalByProductIdAndWareHouseId(products.getId(),pro.getReccr().getWareHouseId());
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
                Product products = productRepository.findByProductCode(pbd.getProductCode());
                StockTotal stockTotal = stockTotalRepository.findStockTotalByProductIdAndWareHouseId(products.getId(),pro.getReccr().getWareHouseId());
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
            List<POAdjustedDetail> poAdjustedDetails = poAdjustedDetailRepository.getListPOAdjustedDetail(poAdjusted.getPoAdjustedNumber());
            reci.setInvoiceDate(poAdjusted.getPoDate());
            reci.setPoNumber(poAdjusted.getPoAdjustedNumber());
            for(POAdjustedDetail pad : poAdjustedDetails){
                Product products = productRepository.findByProductCode(pad.getProductCode());
                StockTotal stockTotal = stockTotalRepository.findStockTotalByProductIdAndWareHouseId(products.getId(),pro.getReccr().getWareHouseId());
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
            Timestamp time = Timestamp.valueOf( str ) ;
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
        response.setData(reci);*/
        return null;

    }
    @Override
    public String createReceiptExportCode(Long idShop) {
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String yy = df.format(Calendar.getInstance().getTime());
        int recxNum = receiptExportRepository.getReceiptExportNumber();
        String shopCode = shopRepository.getShopById(idShop).getShopCode().toString();
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("EXS.");
        reciCode.append(shopCode);
        reciCode.append(".");
        reciCode.append(yy);
        reciCode.append(".");
        reciCode.append(formatReceiptNumber(recxNum));

        return reciCode.toString();
    }
    public String formatReceiptNumber(int number) {
        StringBuilder recx_num = new StringBuilder();
        int num = number + 1;

        if (num < 10) {
            recx_num.append("0000");
        }
        if (num < 100 && num >= 10) {
            recx_num.append("000");
        }
        if (num < 1000 && num >= 100) {
            recx_num.append("00");
        }
        if (num < 10000 && num >= 1000) {
            recx_num.append("0");
        }
        recx_num.append(num);

        return recx_num.toString();
    }
}

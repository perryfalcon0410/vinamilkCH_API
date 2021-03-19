package vn.viettel.saleservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.repository.*;
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
    @Autowired
    ReceiptImportDetailRepository receiptImportDetailRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    StockTotalRepository stockTotalRepository;
    @Autowired
    POAdjustedRepository poAdjustedRepository;
    @Autowired
    POAdjustedDetailRepository poAdjustedDetailRepository;
    @Autowired
    ReceiptExportDetailRepository receiptExportDetailRepository;
    @Autowired
    POBorrowRepository poBorrowRepository;
    @Autowired
    POBorrowDetailRepository poBorrowDetailRepository;
    private Date date = new Date();
    private Timestamp dateTime = new Timestamp(date.getTime());
    final int DAXUATHANG= 0;
    final int CHUAXUATHANG = 1;

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
        Response<ReceiptExport> response = new Response<>();
        User user = userClient.getUserById(userId);
        if(user == null){
            response.setFailure(ResponseMessage.USER_DOES_NOT_EXISTS);
            return response;
        }
        if (rexr == null) {
            response.setFailure(ResponseMessage.NO_CONTENT);
            return response;
        }
        ReceiptExport recx = new ReceiptExport();
        WareHouse wareHouse = wareHouseRepository.findById(rexr.getWareHouseId()).get();
        recx.setReceiptExportDate(dateTime);
        recx.setWareHouse(wareHouse);
        recx.setReceiptExportType(rexr.getReceiptExportType());;
        if (rexr.getReceiptExportType() == 0) {
            recx.setReceiptExportCode(createReceiptExportCode(idShop));
            ReceiptImport receiptImport = receiptImportRepository.findById(rexr.getReceiptImportId()).get();
            recx.setInvoiceNumber(receiptImport.getInvoiceNumber());
            recx.setInvoiceDate(receiptImport.getInvoiceDate());
            recx.setInternalNumber(receiptImport.getInternalNumber());
            recx.setPoNumber(receiptImport.getPoNumber());

            List<ReceiptImportDetail> receiptImportDetails = receiptImportDetailRepository.findByReceiptImportId(receiptImport.getId());
            for(int i =0; i< receiptImportDetails.size();i++){
                ReceiptExportDetail recxd = new ReceiptExportDetail();
                if(rexr.getIsRemainAll() ==  true)
                {

                    Product products = productRepository.findByProductCode(receiptImportDetails.get(i).getProductCode());
                    StockTotal stockTotal = stockTotalRepository.findStockTotalByProductIdAndWareHouseId(products.getId(),wareHouse.getId());
                    if(stockTotal == null)
                        response.setFailure(ResponseMessage.NO_CONTENT);
                    if(stockTotal.getQuantity() == null){
                        stockTotal.setQuantity(0);
                    }
                    recxd.setReceiptExportId(recx.getId());
                    recxd.setProductCode(receiptImportDetails.get(i).getProductCode());
                    recxd.setProductName(receiptImportDetails.get(i).getProductName());
                    recxd.setProductPrice(receiptImportDetails.get(i).getProductPrice());
                    recxd.setUnit(receiptImportDetails.get(i).getUnit());
                    recxd.setQuantity(receiptImportDetails.get(i).getQuantity()-receiptImportDetails.get(i).getQuantityExport());
                    recxd.setPriceTotal(recxd.getQuantity() * recxd.getProductPrice());
                    stockTotal.setQuantity(stockTotal.getQuantity() - recxd.getQuantity());
                    receiptImportDetails.get(i).setQuantityExport(receiptImportDetails.get(i).getQuantity());
                    stockTotalRepository.save(stockTotal);
                    receiptImportDetailRepository.save(receiptImportDetails.get(i));
                    receiptExportDetailRepository.save(recxd);
                }else{
                    for (int j =0; j < rexr.getLitQuantityRemain().size();j++){
                        Product products = productRepository.findByProductCode(receiptImportDetails.get(j).getProductCode());
                        StockTotal stockTotal = stockTotalRepository.findStockTotalByProductIdAndWareHouseId(products.getId(),wareHouse.getId());
                        if(stockTotal == null)
                            response.setFailure(ResponseMessage.NO_CONTENT);
                        if(stockTotal.getQuantity() == null){
                            stockTotal.setQuantity(0);
                        }
                        recxd.setReceiptExportId(recx.getId());
                        recxd.setProductCode(receiptImportDetails.get(i).getProductCode());
                        recxd.setProductName(receiptImportDetails.get(i).getProductName());
                        recxd.setProductPrice(receiptImportDetails.get(i).getProductPrice());
                        recxd.setUnit(receiptImportDetails.get(i).getUnit());
                        recxd.setQuantity(rexr.getLitQuantityRemain().get(j));
                        recxd.setPriceTotal(recxd.getQuantity() * recxd.getProductPrice());
                        stockTotal.setQuantity(stockTotal.getQuantity() - recxd.getQuantity());
                        receiptImportDetails.get(j).setQuantityExport(receiptImportDetails.get(j).getQuantityExport()+rexr.getLitQuantityRemain().get(j));
                        stockTotalRepository.save(stockTotal);
                        receiptImportDetailRepository.save(receiptImportDetails.get(j));
                        receiptExportDetailRepository.save(recxd);
                    }

                }

            }
        }
        if (rexr.getReceiptExportType() == 1) {
            ReceiptImport receiptImport = receiptImportRepository.findById(rexr.getReceiptImportId()).get();
            recx.setReceiptExportCode(receiptImport.getPoNumber());
            recx.setInvoiceNumber(createInvoiceAdjustedCode(idShop));
            recx.setInvoiceDate(receiptImport.getInvoiceDate());
            recx.setInternalNumber(createReceiptExportCode(idShop));
            POAdjusted rea = poAdjustedRepository.findAdjustedExport(rexr.getReId());
            List<POAdjustedDetail> reads = poAdjustedDetailRepository.findAllByPoAdjustedId(rea.getId());
            for(POAdjustedDetail read : reads){
                ReceiptExportDetail recxd = new ReceiptExportDetail();
                recxd.setReceiptExportId(recx.getId());
                recxd.setProductCode(read.getProductCode());
                recxd.setProductName(read.getProductName());
                recxd.setProductPrice(read.getProductPrice());
                recxd.setUnit(read.getUnit());
                recxd.setQuantity(read.getQuantity());
                recxd.setPriceTotal(recxd.getQuantity() * recxd.getProductPrice());
                Product products = productRepository.findByProductCode(read.getProductCode());
                StockTotal stockTotal = stockTotalRepository.findStockTotalByProductIdAndWareHouseId(products.getId(),wareHouse.getId());
                if(stockTotal == null)
                    response.setFailure(ResponseMessage.NO_CONTENT);
                if(stockTotal.getQuantity() == null){
                    stockTotal.setQuantity(0);
                }
                stockTotal.setQuantity(stockTotal.getQuantity()- read.getQuantity());

                stockTotalRepository.save(stockTotal);
                receiptExportDetailRepository.save(recxd);

            }
            rea.setStatus(DAXUATHANG);
            poAdjustedRepository.save(rea);
        }
        if (rexr.getReceiptExportType() == 2) {
            ReceiptImport receiptImport = receiptImportRepository.findById(rexr.getReceiptImportId()).get();
            recx.setReceiptExportCode(createReceiptExportCode(idShop));
            recx.setInvoiceNumber(createInvoiceBorrowCode(idShop));
            recx.setInvoiceDate(receiptImport.getInvoiceDate());//dang sai
            //recx.setInternalNumber(createReceiptExportCode(idShop)); re trong
            POBorrow reb = poBorrowRepository.findBorrowExport(rexr.getReId());
            List<POBorrowDetail> rebds = poBorrowDetailRepository.findAllByPoBorrowId(reb.getId());
            for(POBorrowDetail rebd : rebds){
                ReceiptExportDetail recxd = new ReceiptExportDetail();
                recxd.setReceiptExportId(recx.getId());
                recxd.setProductCode(rebd.getProductCode());
                recxd.setProductName(rebd.getProductName());
                recxd.setProductPrice(rebd.getProductPrice());
                recxd.setUnit(rebd.getUnit());
                recxd.setQuantity(rebd.getQuantity());
                recxd.setPriceTotal(recxd.getQuantity() * recxd.getProductPrice());
                Product products = productRepository.findByProductCode(rebd.getProductCode());
                StockTotal stockTotal = stockTotalRepository.findStockTotalByProductIdAndWareHouseId(products.getId(),wareHouse.getId());
                if(stockTotal == null)
                    response.setFailure(ResponseMessage.NO_CONTENT);
                if(stockTotal.getQuantity() == null){
                    stockTotal.setQuantity(0);
                }
                stockTotal.setQuantity(stockTotal.getQuantity()- rebd.getQuantity());

                stockTotalRepository.save(stockTotal);
                receiptExportDetailRepository.save(recxd);

            }
            reb.setStatus(DAXUATHANG);
            poBorrowRepository.save(reb);
        }
        Integer s = receiptExportDetailRepository.sumAllReceiptExport(recx.getId());
        recx.setReceiptExportQuantity(s);
        recx.setNote(rexr.getNote());
        recx.setCreatedAt(dateTime);
        recx.setCreatedBy(userId);
        receiptExportRepository.save(recx);
        response.setData(recx);
        return response;

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

    @Override
    public String createInvoiceAdjustedCode(Long idShop) {
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String yy = df.format(Calendar.getInstance().getTime());
        DateFormat dm = new SimpleDateFormat("mm"); // Just the year, with 2 digits
        String mm = dm.format(Calendar.getInstance().getTime());
        DateFormat dfd = new SimpleDateFormat("dd"); // Just the year, with 2 digits
        String dd = dfd.format(Calendar.getInstance().getTime());
        int recxNum = receiptExportRepository.getReceiptExportNumber();
        String shopCode = shopRepository.getShopById(idShop).getShopCode().toString();
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("SAL.");
        reciCode.append(shopCode);
        reciCode.append(".");
        reciCode.append(yy);
        reciCode.append(mm);
        reciCode.append(dd);
        reciCode.append(".");
        reciCode.append(formatReceiptNumber(recxNum));

        return reciCode.toString();
    }
    public String createInvoiceBorrowCode(Long idShop) {
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String yy = df.format(Calendar.getInstance().getTime());
        DateFormat dm = new SimpleDateFormat("mm"); // Just the year, with 2 digits
        String mm = dm.format(Calendar.getInstance().getTime());
        DateFormat dfd = new SimpleDateFormat("dd"); // Just the year, with 2 digits
        String dd = dfd.format(Calendar.getInstance().getTime());
        int recxNum = receiptExportRepository.getReceiptExportNumber();
        String shopCode = shopRepository.getShopById(idShop).getShopCode().toString();
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("EXP_");
        reciCode.append(shopCode);
        reciCode.append("_");
        reciCode.append(yy);
        reciCode.append(mm);
        reciCode.append(dd);
        reciCode.append(".");
        reciCode.append(formatReceiptNumberVer2(recxNum));

        return reciCode.toString();
    }



    @Override
    public Response<ReceiptExportDTO> getReceiptExportById(Long recxId) {
        Response response = new Response();
        ReceiptExportDTO re = new ReceiptExportDTO();
        ReceiptExport recx = receiptExportRepository.findById(recxId).get();
        re.setReceiptExportCode(recx.getReceiptExportCode());
        re.setReceiptExportType(recx.getReceiptExportType());
        re.setWareHouseId(recx.getWareHouse().getId());
        re.setInvoiceNumber(recx.getInvoiceNumber());
        re.setInvoiceDate(recx.getInvoiceDate());
        re.setPoNumber(recx.getPoNumber());
        re.setInternalNumber(recx.getInternalNumber());
        re.setNote(recx.getNote());
        response.setData(recx);
        return response;
    }

    @Override
    public Response<Page<POAdjustedDTO>> getAllReceiptExportAdjusted(Pageable pageable) {
        Page<POAdjusted> receiptExportAdjusters = poAdjustedRepository.getAdjustedExport(pageable);
        List<POAdjustedDTO> reciLst = new ArrayList<>();
        for (POAdjusted rea : receiptExportAdjusters) {
            POAdjustedDTO reaDTO = new POAdjustedDTO();
            reaDTO.setId(rea.getId());
            reaDTO.setPoAdjustedNumber(rea.getPoAdjustedNumber());
            reaDTO.setPoAdjustedNumber(rea.getPoAdjustedNumber());
            reaDTO.setPoNote(rea.getPoNote());
            reaDTO.setStatus(rea.getStatus());

            reciLst.add(reaDTO);
        }
        Response<Page<POAdjustedDTO>> response = new Response<>();
        Page<POAdjustedDTO> receiptResponses = new PageImpl<>(reciLst);
        response.setData(receiptResponses);
        return response;
    }

    @Override
    public Response<List<PoAdjustedDetailDTO>> getExportAdjustedDetailById(Long Id) {
        List<POAdjustedDetail> reads = poAdjustedDetailRepository.findAllByPoAdjustedId(Id);
        List<PoAdjustedDetailDTO> readList = new ArrayList<>();
        for (POAdjustedDetail read : reads) {
            PoAdjustedDetailDTO readDTO = new PoAdjustedDetailDTO();
            readDTO.setId(read.getId());
            readDTO.setPoAdjustedId(read.getPoAdjustedId());
            readDTO.setProductCode(read.getProductCode());
            readDTO.setProductName(read.getProductName());
            readDTO.setProductPrice(read.getProductPrice());
            readDTO.setUnit(read.getUnit());
            readDTO.setQuantity(read.getQuantity());
            readDTO.setPriceTotal(read.getPriceTotal());
            readList.add(readDTO);
        }
        Response<List<PoAdjustedDetailDTO>> response = new Response<>();
        response.setData(readList);
        return response;
    }

    @Override
    public Response<Page<POBorrowDTO>> getAllReceiptExportBorrow(Pageable pageable) {
        Page<POBorrow> rebs = poBorrowRepository.getBorrowExport(pageable);
        List<POBorrowDTO> reciLst = new ArrayList<>();
        for (POBorrow reb : rebs) {
            POBorrowDTO rebDTO = new POBorrowDTO();
            rebDTO.setId(reb.getId());
            rebDTO.setPoBorrowNumber(reb.getPoBorrowNumber());
            rebDTO.setPoDate(reb.getPoDate());
            rebDTO.setPoNote(reb.getPoNote());
            rebDTO.setStatus(reb.getStatus());

            reciLst.add(rebDTO);
        }
        Response<Page<POBorrowDTO>> response = new Response<>();
        Page<POBorrowDTO> receiptResponses = new PageImpl<>(reciLst);
        response.setData(receiptResponses);
        return response;
    }

    @Override
    public Response<List<PoBorrowDetailDTO>> getExportBorrowDetailById(Long Id) {
        List<POBorrowDetail> rebds = poBorrowDetailRepository.findAllByPoBorrowId(Id);
        List<PoBorrowDetailDTO> readList = new ArrayList<>();
        for (POBorrowDetail rebd : rebds) {
            PoBorrowDetailDTO rebdDTO = new PoBorrowDetailDTO();
            rebdDTO.setId(rebd.getId());
            rebdDTO.setPoBorrowId(rebd.getPoBorrowId());
            rebdDTO.setProductCode(rebd.getProductCode());
            rebdDTO.setProductName(rebd.getProductName());
            rebdDTO.setProductPrice(rebd.getProductPrice());
            rebdDTO.setUnit(rebd.getUnit());
            rebdDTO.setQuantity(rebd.getQuantity());
            rebdDTO.setPriceTotal(rebd.getPriceTotal());
            readList.add(rebdDTO);
        }
        Response<List<PoBorrowDetailDTO>> response = new Response<>();
        response.setData(readList);
        return response;
    }

    @Override
    public Response<ReceiptExport> updateReceiptExport(ReceiptExportRequest rexr, long userId) {
        Response<ReceiptExport> response = new Response<>();
        ReceiptExport recx = receiptExportRepository.findById(rexr.getId()).get();
        if (recx != null) {
            if(recx.getReceiptExportType()==0){
                ReceiptImport reci = receiptImportRepository.findByPoNumber(recx.getPoNumber());
                List<ReceiptImportDetail> recids = receiptImportDetailRepository.findByReceiptImportId(reci.getId());
                for(int i =0; i< recids.size();i++){
                    ReceiptExportDetail recxd = new ReceiptExportDetail();
                    recxd.setPriceTotal(recxd.getQuantity() * recxd.getProductPrice());
                    if(rexr.getIsRemainAll() ==  true){
                        Product products = productRepository.findByProductCode(recids.get(i).getProductCode());
                        StockTotal stockTotal = stockTotalRepository.findStockTotalByProductIdAndWareHouseId(products.getId(),recx.getWareHouse().getId());
                        if(stockTotal == null)
                            response.setFailure(ResponseMessage.NO_CONTENT);
                        if(stockTotal.getQuantity() == null){
                            stockTotal.setQuantity(0);
                        }
                        recxd.setReceiptExportId(recids.get(i).getId());
                        recxd.setProductCode(recids.get(i).getProductCode());
                        recxd.setProductName(recids.get(i).getProductName());
                        recxd.setProductPrice(recids.get(i).getProductPrice());
                        recxd.setUnit(recids.get(i).getUnit());
                        recxd.setQuantity(recids.get(i).getQuantity()-recids.get(i).getQuantityExport());
                        recxd.setPriceTotal(recxd.getQuantity() * recxd.getProductPrice());
                        stockTotal.setQuantity(stockTotal.getQuantity() - recxd.getQuantity());
                        recids.get(i).setQuantityExport(recids.get(i).getQuantity());
                        stockTotalRepository.save(stockTotal);
                        receiptImportDetailRepository.save(recids.get(i));
                        receiptExportDetailRepository.save(recxd);
                    }else{
                        for (int j =0; j < rexr.getLitQuantityRemain().size();j++){
                            Product products = productRepository.findByProductCode(recids.get(j).getProductCode());
                            StockTotal stockTotal = stockTotalRepository.findStockTotalByProductIdAndWareHouseId(products.getId(),recx.getWareHouse().getId());
                            if(stockTotal == null)
                                response.setFailure(ResponseMessage.NO_CONTENT);
                            if(stockTotal.getQuantity() == null){
                                stockTotal.setQuantity(0);
                            }
                            recxd.setReceiptExportId(recids.get(i).getId());
                            recxd.setProductCode(recids.get(i).getProductCode());
                            recxd.setProductName(recids.get(i).getProductName());
                            recxd.setProductPrice(recids.get(i).getProductPrice());
                            recxd.setUnit(recids.get(i).getUnit());
                            recxd.setQuantity(rexr.getLitQuantityRemain().get(j));
                            recxd.setPriceTotal(recxd.getQuantity() * recxd.getProductPrice());
                            stockTotal.setQuantity(stockTotal.getQuantity() + (recids.get(j).getQuantityExport()));
                            recids.get(j).setQuantityExport(0);
                            stockTotal.setQuantity(stockTotal.getQuantity() - (rexr.getLitQuantityRemain().get(j)));
                            recids.get(j).setQuantityExport(recids.get(j).getQuantityExport()+rexr.getLitQuantityRemain().get(j));
                            stockTotalRepository.save(stockTotal);
                            receiptImportDetailRepository.save(recids.get(j));
                            receiptExportDetailRepository.save(recxd);
                        }
                    }

                }
            }
            Integer s = receiptExportDetailRepository.sumAllReceiptExport(recx.getId());
            recx.setReceiptExportQuantity(s);
            recx.setNote(rexr.getNote());
            recx.setUpdatedAt(dateTime);
            recx.setUpdatedBy(userId);
            receiptExportRepository.save(recx);

        } else {
            response.setFailure(ResponseMessage.NO_CONTENT);
        }

        return response;
    }

    @Override
    public void remove(long[] ids) {
        for(long id: ids) {
            ReceiptExport recx = receiptExportRepository.findById(id).get();
            ReceiptImport reci = receiptImportRepository.findByPoNumber(recx.getPoNumber());
            if(recx.getReceiptExportType() == 0)
            {
                List<ReceiptExportDetail> recxds = receiptExportDetailRepository.findByReceiptExportId(id);
                for(ReceiptExportDetail recxd : recxds){
                    Product products = productRepository.findByProductCode(recxd.getProductCode());
                    StockTotal stockTotal = stockTotalRepository.findStockTotalByProductIdAndWareHouseId(products.getId(),recx.getWareHouse().getId());
                    stockTotal.setQuantity(stockTotal.getQuantity() + recxd.getQuantity());
                    stockTotalRepository.save(stockTotal);
                }
                if(recx.getReceiptExportQuantity() <reci.getReceiptImportQuantity())
                    reci.setStatus(2);
                else
                    reci.setStatus(0);
            }
            if(recx.getReceiptExportType() == 1)
            {
                List<ReceiptExportDetail> recxds = receiptExportDetailRepository.findByReceiptExportId(id);
                for(ReceiptExportDetail recxd : recxds){
                    Product products = productRepository.findByProductCode(recxd.getProductCode());
                    StockTotal stockTotal = stockTotalRepository.findStockTotalByProductIdAndWareHouseId(products.getId(),recx.getWareHouse().getId());
                    stockTotal.setQuantity(stockTotal.getQuantity() + recxd.getQuantity());
                    stockTotalRepository.save(stockTotal);
                }
            }
            if(recx.getReceiptExportType() == 2)
            {
                List<ReceiptExportDetail> recxds = receiptExportDetailRepository.findByReceiptExportId(id);
                for(ReceiptExportDetail recxd : recxds){
                    Product products = productRepository.findByProductCode(recxd.getProductCode());
                    StockTotal stockTotal = stockTotalRepository.findStockTotalByProductIdAndWareHouseId(products.getId(),recx.getWareHouse().getId());
                    stockTotal.setQuantity(stockTotal.getQuantity() + recxd.getQuantity());
                    stockTotalRepository.save(stockTotal);
                }
            }
            receiptExportRepository.deleteById(id);
        }

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
    public String formatReceiptNumberVer2(int number) {
        StringBuilder recx_num = new StringBuilder();
        int num = number + 1;

        if (num < 10) {
            recx_num.append("00");
        }
        if (num < 100 && num >= 10) {
            recx_num.append("0");
        }
        recx_num.append(num);

        return recx_num.toString();
    }
}

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
    ReceiptExportAdjustedRepository receiptExportAdjustedRepository;
    @Autowired
    ReceiptExportAdjustedDetailRepository receiptExportAdjustedDetailRepository;
    @Autowired
    ReceiptExportBorrowRepository receiptExportBorrowRepository;
    @Autowired
    ReceiptExportBorrowDetailRepository receiptExportBorrowDetailRepository;
    @Autowired
    ReceiptExportDetailRepository receiptExportDetailRepository;

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
            ReceiptExportAdjusted rea = receiptExportAdjustedRepository.findById(rexr.getReId()).get();
            List<ReceiptExportAdjustedDetail> reads = receiptExportAdjustedDetailRepository.findByReceiptExportAdjustedId(rea.getId());
            for(ReceiptExportAdjustedDetail read : reads){
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
            receiptExportAdjustedRepository.save(rea);
        }
        if (rexr.getReceiptExportType() == 2) {
            ReceiptImport receiptImport = receiptImportRepository.findById(rexr.getReceiptImportId()).get();
            recx.setReceiptExportCode(createReceiptExportCode(idShop));
            recx.setInvoiceNumber(createInvoiceBorrowCode(idShop));
            recx.setInvoiceDate(receiptImport.getInvoiceDate());//dang sai
            //recx.setInternalNumber(createReceiptExportCode(idShop)); re trong
            ReceiptExportBorrow reb = receiptExportBorrowRepository.findById(rexr.getReId()).get();
            List<ReceiptExportBorrowDetail> rebds = receiptExportBorrowDetailRepository.findByReceiptExportBorrowId(reb.getId());
            for(ReceiptExportBorrowDetail rebd : rebds){
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
            receiptExportBorrowRepository.save(reb);
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
    public Response<Page<ReceiptExportAdjustedDTO>> getAllReceiptExportAdjusted(Pageable pageable) {
        Page<ReceiptExportAdjusted> receiptExportAdjusteds = receiptExportAdjustedRepository.getAll(pageable);
        List<ReceiptExportAdjustedDTO> reciLst = new ArrayList<>();
        for (ReceiptExportAdjusted rea : receiptExportAdjusteds) {
            ReceiptExportAdjustedDTO reaDTO = new ReceiptExportAdjustedDTO();
            reaDTO.setId(rea.getId());
            reaDTO.setLicenseNumber(rea.getLicenseNumber());
            reaDTO.setReceiptExportAdjustedDate(rea.getReceiptExportAdjustedDate());
            reaDTO.setNote(rea.getNote());
            reaDTO.setStatus(rea.getStatus());

            reciLst.add(reaDTO);
        }
        Response<Page<ReceiptExportAdjustedDTO>> response = new Response<>();
        Page<ReceiptExportAdjustedDTO> receiptResponses = new PageImpl<>(reciLst);
        response.setData(receiptResponses);
        return response;
    }

    @Override
    public Response<List<ReceiptExportAdjustedDetailDTO>> getExportAdjustedDetailById(Long Id) {
        List<ReceiptExportAdjustedDetail> reads = receiptExportAdjustedDetailRepository.findByReceiptExportAdjustedId(Id);
        List<ReceiptExportAdjustedDetailDTO> readList = new ArrayList<>();
        for (ReceiptExportAdjustedDetail read : reads) {
            ReceiptExportAdjustedDetailDTO readDTO = new ReceiptExportAdjustedDetailDTO();
            readDTO.setId(read.getId());
            readDTO.setReceiptExportAdjustedId(read.getReceiptExportAdjustedId());
            readDTO.setProductCode(read.getProductCode());
            readDTO.setProductName(read.getProductName());
            readDTO.setProductPrice(read.getProductPrice());
            readDTO.setUnit(read.getUnit());
            readDTO.setQuantity(read.getQuantity());
            readDTO.setPriceTotal(read.getPriceTotal());
            readList.add(readDTO);
        }
        Response<List<ReceiptExportAdjustedDetailDTO>> response = new Response<>();
        response.setData(readList);
        return response;
    }

    @Override
    public Response<Page<ReceiptExportBorrowDTO>> getAllReceiptExportBorrow(Pageable pageable) {
        Page<ReceiptExportBorrow> rebs = receiptExportBorrowRepository.getAll(pageable);
        List<ReceiptExportBorrowDTO> reciLst = new ArrayList<>();
        for (ReceiptExportBorrow reb : rebs) {
            ReceiptExportBorrowDTO rebDTO = new ReceiptExportBorrowDTO();
            rebDTO.setId(reb.getId());
            rebDTO.setLicenseNumber(reb.getLicenseNumber());
            rebDTO.setReceiptExportBorrowDate(reb.getReceiptExportAdjustedDate());
            rebDTO.setNote(reb.getNote());
            rebDTO.setStatus(reb.getStatus());

            reciLst.add(rebDTO);
        }
        Response<Page<ReceiptExportBorrowDTO>> response = new Response<>();
        Page<ReceiptExportBorrowDTO> receiptResponses = new PageImpl<>(reciLst);
        response.setData(receiptResponses);
        return response;
    }

    @Override
    public Response<List<ReceiptExportBorrowDetailDTO>> getExportBorrowDetailById(Long Id) {
        List<ReceiptExportBorrowDetail> rebds = receiptExportBorrowDetailRepository.findByReceiptExportBorrowId(Id);
        List<ReceiptExportBorrowDetailDTO> readList = new ArrayList<>();
        for (ReceiptExportBorrowDetail rebd : rebds) {
            ReceiptExportBorrowDetailDTO rebdDTO = new ReceiptExportBorrowDetailDTO();
            rebdDTO.setId(rebd.getId());
            rebdDTO.setReceiptExportBorrowId(rebd.getReceiptExportBorrowId());
            rebdDTO.setProductCode(rebd.getProductCode());
            rebdDTO.setProductName(rebd.getProductName());
            rebdDTO.setProductPrice(rebd.getProductPrice());
            rebdDTO.setUnit(rebd.getUnit());
            rebdDTO.setQuantity(rebd.getQuantity());
            rebdDTO.setPriceTotal(rebd.getPriceTotal());
            readList.add(rebdDTO);
        }
        Response<List<ReceiptExportBorrowDetailDTO>> response = new Response<>();
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

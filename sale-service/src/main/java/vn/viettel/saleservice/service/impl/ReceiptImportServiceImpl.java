package vn.viettel.saleservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.repository.ReceiptImportRepository;
import vn.viettel.saleservice.repository.ShopRepository;
import vn.viettel.saleservice.repository.WareHouseRepository;
import vn.viettel.saleservice.service.ReceiptImportService;
import vn.viettel.saleservice.service.dto.ReceiptCreateRequest;
import vn.viettel.saleservice.service.dto.ReceiptImportDTO;
import vn.viettel.saleservice.service.dto.WareHouseDTO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ReceiptImportServiceImpl implements ReceiptImportService {
    @Autowired
    ReceiptImportRepository receiptImportRepository;
    @Autowired
    WareHouseRepository wareHouseRepository;
    @Autowired
    ShopRepository shopRepository;

    @Override
    public Response<List<ReceiptImportDTO>> getAll(LocalDateTime fromDate, LocalDateTime toDate, String invoiceNumber, Integer type) {
        List<ReceiptImport> reci = receiptImportRepository.getReceiptImportByVariable(fromDate,toDate,invoiceNumber,type);
        List<ReceiptImportDTO> reciLst = new ArrayList<>();
        for (ReceiptImport r : reci) {
            ReceiptImportDTO reciDTO = new ReceiptImportDTO();
            reciDTO.setId(r.getId());
            reciDTO.setReceipt_code(r.getReceipt_code());
            reciDTO.setInvoice_date(r.getInvoice_date());
            reciDTO.setReceipt_total(r.getReceipt_total() );
            reciDTO.setNote(r.getNote());
            reciDTO.setInternal_number(r.getInternal_number());
            reciDTO.setInvoice_number(r.getInvoice_number());
            reciDTO.setReceipt_quantity(r.getReceipt_quantity());
            reciLst.add(reciDTO);
        }
        Response<List<ReceiptImportDTO>> response = new Response<>();
        response.setData(reciLst);
        return response;
    }

    @Override
    public Response<ReceiptImport> createReceiptImport(ReceiptCreateRequest reccr, long userId,long idShop) {
        Response<ReceiptImport> response = new Response<>();
        if (reccr == null)
            response.setFailure(ResponseMessage.NO_CONTENT);
        if (checkUserExist(userId) == null)
            response.setFailure(ResponseMessage.USER_DOES_NOT_EXISTS);
        WareHouse wareHouse = createReceiptImportWareHouse(reccr.getWarehouseDTO());
        ReceiptImport reci = new ReceiptImport();
        setReceiptImportValue(reci, reccr, userId,idShop);
        Date date = new Date();
        LocalDateTime dateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        reci.setCreatedAt(dateTime);
        reci.setCreatedBy(userId);
        setReceiptImportValue(reci,reccr,userId,idShop);

        return response;
    }

    @Override
    public Response<ReceiptImport> updateReceiptImport(ReceiptCreateRequest reccr, long userId) {
        Response<ReceiptImport> response = new Response<>();
        if (reccr == null)
            response.setFailure(ResponseMessage.NO_CONTENT);
        ReceiptImport recei = receiptImportRepository.findById(reccr.getId()).get();
        Date date = new Date();
        LocalDateTime dateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        if (recei != null) {

        }
        setReceiptImportValue2(recei, reccr, userId);
        recei.setUpdatedBy(userId);
        recei.setUpdatedAt(dateTime);
        receiptImportRepository.save(recei);

        response.setData(recei);
        return response;
    }


    @Override
    public void remove(Long reciId) {

    }

    @Override
    public User checkUserExist(long userId) {
        //        User user = userClient.getUserById(userId);
//        return user == null ? null : user;
        return null;
    }

    @Override
    public WareHouse createReceiptImportWareHouse(WareHouseDTO wareHouseDTO) {
        if(wareHouseDTO != null){
            if (wareHouseRepository.findByAddressAndWarehouse_name(wareHouseDTO.getWarehouse_name(), wareHouseDTO.getAddress()) != null){
                return wareHouseRepository.findByAddressAndWarehouse_name(wareHouseDTO.getWarehouse_name(),wareHouseDTO.getAddress());
            }else
            {
                WareHouse wareHouse = new WareHouse(wareHouseDTO.getWarehouse_name(), wareHouseDTO.getAddress());
                return wareHouseRepository.save(wareHouse);
            }
        }
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

    public String formatReceINumber(int number) {
        StringBuilder recei_num = new StringBuilder();
        int num = number + 1;

        if(num < 10) {
            recei_num.append("0000");
        }
        if(num < 100 && num >= 10) {
            recei_num.append("000");
        }
        if(num < 1000 && num >= 100) {
            recei_num.append("00");
        }
        if(num < 10000 && num >= 1000) {
            recei_num.append("0");
        }
        recei_num.append(num);

        return recei_num.toString();
    }

    private ReceiptImport setReceiptImportValue(ReceiptImport reci, ReceiptCreateRequest reccr, long userId,long idShop) {

        reci.setReceipt_code(createReceiptImportCode(idShop));
        reci.setInvoice_date(reccr.getInvoice_date());
        reci.setInvoice_number(reccr.getInvoice_number());
        reci.setInvoice_number(reccr.getInternal_number());
        reci.setNote(reccr.getNote());
        reci.setReceipt_type(reccr.getReceipt_type());
        return reci;
    }
    private ReceiptImport setReceiptImportValue2(ReceiptImport reci, ReceiptCreateRequest reccr, long userId) {
        reci.setInvoice_date(reccr.getInvoice_date());
        reci.setInvoice_number(reccr.getInvoice_number());
        reci.setInvoice_number(reccr.getInternal_number());
        reci.setNote(reccr.getNote());
        reci.setReceipt_type(reccr.getReceipt_type());
        return reci;
    }


}

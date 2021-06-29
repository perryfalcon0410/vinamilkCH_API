package vn.viettel.sale.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.convert.XStreamTranslator;
import vn.viettel.sale.entities.PoConfirm;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.entities.PoDetail;
import vn.viettel.sale.entities.Product;
import vn.viettel.sale.repository.PoConfirmRepository;
import vn.viettel.sale.repository.PoDetailRepository;
import vn.viettel.sale.repository.ProductRepository;
import vn.viettel.sale.service.PoConfirmService;
import vn.viettel.sale.service.dto.PoConfirmXmlDTO;
import vn.viettel.sale.service.feign.ApparamClient;
import vn.viettel.sale.service.feign.ShopClient;
import vn.viettel.sale.util.ConnectFTP;
import vn.viettel.sale.xml.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PoConfirmServiceImpl extends BaseServiceImpl<PoConfirm, PoConfirmRepository> implements PoConfirmService {
    @Autowired
    ShopClient shopClient;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    PoDetailRepository poDetailRepository;

    @Autowired
    ApparamClient apparamClient;

    XStreamTranslator xstream = XStreamTranslator.getInstance();

    @Override
    public Response<PoConfirm> getPoConfirmById(Long id) {
        Optional<PoConfirm> poConfirm = repository.findById(id);
        if(!poConfirm.isPresent())
        {
            throw new ValidateException(ResponseMessage.PO_CONFIRM_NOT_EXISTS);
        }
        return new Response<PoConfirm>().withData(poConfirm.get());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncXmlPo(InputStream input) throws IOException {
        Class<?>[] classes = new Class[] { Line.class, PODetail.class, POHeader.class, NewData.class, NewDataSet.class};
        xstream.processAnnotations(classes);
        xstream.allowTypes(classes);
        NewDataSet newDataSet = (NewDataSet) xstream.fromXML(input);
        List<NewData> lstNewData = newDataSet.getLstNewData();
        lstNewData.stream().forEach(data -> {
            POHeader poHeader = data.getPoHeader();
            if(poHeader != null)
            {
                //po confirm
                PoConfirm poConfirm = new PoConfirm();
                ShopDTO shopDTO = shopClient.getByShopCode(poHeader.getDistCode()).getData();
                if(shopDTO != null)
                    poConfirm.setShopId(shopDTO.getId());
                else
                    throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
                poConfirm.setPoNumber(poHeader.getPoNumber());
                int i = poHeader.getPoCoNumber().lastIndexOf('_');
                String poCoNum = poHeader.getPoCoNumber().substring(0,i);
                String internalNum = poHeader.getPoCoNumber().substring(i+1);

                poConfirm.setOrderDate(poHeader.getOrderDate());
                poConfirm.setStatus(0);
                poConfirm.setInternalNumber(internalNum);
                poConfirm.setPoCoNumber(poCoNum);
                Long id = repository.save(poConfirm).getId();

                //po detail
                PODetail poDetail = data.getPoDetail();
                Double totalAm = 0D;
                Integer totalQuan = 0;
                if(poDetail != null)
                {
                    List<Line> lines = poDetail.getLstLine();
                    for(Line line: lines) {
                        if (poHeader.getPoNumber() == line.getPONumber() && poHeader.getPoCoNumber() == line.getPoCoNumber()) {
                            PoDetail detail = new PoDetail();
                            Product product = productRepository.findByProductCode(line.getItemCode());
                            if (product != null)
                                detail.setProductId(product.getId());
                            else
                                throw new ValidateException(ResponseMessage.PRODUCT_NOT_FOUND);
                            detail.setPoId(id);
                            detail.setShopId(shopDTO.getId());
                            detail.setQuantity(line.getQuantity());
                            detail.setPriceNotVat(line.getPrice());
                            detail.setVat(line.getVat());
                            detail.setAmountNotVat(line.getLineTotal());
                            detail.setPrice((line.getVat() > 0) ? line.getPrice() + (line.getPrice()*line.getVat()/100) : line.getPrice());
                            totalAm += line.getLineTotal();
                            totalQuan += line.getQuantity();
                            poDetailRepository.save(detail);
                        }
                    }
                }

                //update po confirm
                poConfirm.setId(id);
                poConfirm.setTotalAmount(totalAm);
                poConfirm.setTotalQuantity(totalQuan);
                String poCode = poHeader.getDistCode()+"_"+id;
                poConfirm.setPoCode(poCode);
                repository.save(poConfirm);
            }
        });
    }

    @Override
    public PoConfirmXmlDTO updatePoCofirm() {
        int stt = 0;
        List<ApParamDTO> apParamDTOList = apparamClient.getApParamByTypeV1("FTP").getData();
        String readPath = "/POCHGTSP/Outbox", backupPath = "/POCHGTSP/Backup", newPo = "_IMP_PO_";
        if(apParamDTOList != null){
            for(ApParamDTO app : apParamDTOList){
                if(app.getApParamCode() == null || "FTP_PO".equalsIgnoreCase(app.getApParamCode().trim())) readPath = app.getValue().trim();
                if(app.getApParamCode() == null || "FTP_PO_BACKUP".equalsIgnoreCase(app.getApParamCode().trim())) backupPath = app.getValue().trim();
                if(app.getApParamCode() == null || "FTP_FILE_NEW_PO".equalsIgnoreCase(app.getApParamCode().trim())) newPo = app.getValue().trim();
            }
        }
        ConnectFTP connectFTP = getConnectFTP(apParamDTOList);
        //read new po
        HashMap<String, InputStream> newPos = connectFTP.getFiles(readPath, newPo);
        if(newPos != null){
            for (Map.Entry<String, InputStream> entry : newPos.entrySet()){
                try {
                    this.syncXmlPo(entry.getValue());
                    connectFTP.moveFile(readPath, backupPath, entry.getKey());
                    stt++;
                }catch (Exception ex) {
                    LogFile.logToFile("", "", LogLevel.ERROR, null, "Error while read file " + entry.getKey() + " - " + ex.getMessage());
                }
            }
        }
        if(stt > 0){
            return new PoConfirmXmlDTO(true, "Đồng bộ thành công "+stt+" file");
        }else{
            return new PoConfirmXmlDTO(false, "Đồng bộ không thành công");
        }

    }

    public static ConnectFTP getConnectFTP(List<ApParamDTO> apParamDTOList) {
        String server = null, portStr = null, userName = null, password = null;
        if(apParamDTOList != null){
            for(ApParamDTO app : apParamDTOList){
                if(app.getApParamCode() == null || "FTP_SERVER".equalsIgnoreCase(app.getApParamCode().trim())) server = app.getValue().trim();
                if(app.getApParamCode() == null || "FTP_USER".equalsIgnoreCase(app.getApParamCode().trim())) userName = app.getValue().trim();
                if(app.getApParamCode() == null || "FTP_PASS".equalsIgnoreCase(app.getApParamCode().trim())) password = app.getValue().trim();
                if(app.getApParamCode() == null || "FTP_PORT".equalsIgnoreCase(app.getApParamCode().trim())) portStr = app.getValue().trim();
            }
        }
        return new ConnectFTP(server, portStr, userName, password);
    }
}

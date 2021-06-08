package vn.viettel.sale.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.viettel.core.dto.ShopDTO;
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
import vn.viettel.sale.service.feign.ShopClient;
import vn.viettel.sale.xml.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class PoConfirmServiceImpl extends BaseServiceImpl<PoConfirm, PoConfirmRepository> implements PoConfirmService {
    @Autowired
    ShopClient shopClient;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    PoDetailRepository poDetailRepository;

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
    public NewDataSet syncXmlPo(MultipartFile file) throws IOException {
        Class<?>[] classes = new Class[] { Line.class, PODetail.class, POHeader.class, NewData.class, NewDataSet.class};
        xstream.processAnnotations(classes);
        xstream.allowTypes(classes);
        NewDataSet newDataSet = (NewDataSet) xstream.fromXML(file.getInputStream());
        List<NewData> lstNewData = newDataSet.getLstNewData();
        lstNewData.stream().forEach(data -> {
            POHeader poHeader = data.getPoHeader();
            if(poHeader != null)
            {
                //po confirm
                PoConfirm poConfirm = new PoConfirm();
                ShopDTO shopDTO = shopClient.getByShopCode(poHeader.getDistCode()).getData();
                if(shopDTO != null)
                {
                    poConfirm.setShopId(shopDTO.getId());
                }
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
                            if (product != null) {
                                detail.setProductId(product.getId());
                            }
                            detail.setPoId(id);
                            detail.setShopId(shopDTO.getId());
                            detail.setOrderDate(line.getExpireDate());
                            detail.setQuantity(line.getQuantity());
                            detail.setPriceNotVat(line.getPrice());
                            detail.setVat(line.getVat());
                            detail.setAmountNotVat(line.getLineTotal());
                            detail.setPrice((line.getVat() > 0) ? line.getPrice()*line.getVat()/100 : line.getPrice());
                            totalAm += line.getLineTotal();
                            totalQuan += line.getQuantity();
                            poDetailRepository.save(detail);
                        }
                    }
                }
                poConfirm.setTotalAmount(totalAm);
                poConfirm.setTotalQuantity(totalQuan);
                String poCode = poHeader.getDistCode()+"_"+id;
                poConfirm.setPoCode(poCode);
                repository.save(poConfirm);
            }
        });
        return newDataSet;
    }
}

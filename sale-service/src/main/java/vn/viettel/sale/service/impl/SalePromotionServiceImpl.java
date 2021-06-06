package vn.viettel.sale.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.dto.customer.MemberCardDTO;
import vn.viettel.core.dto.promotion.PromotionProgramDTO;
import vn.viettel.core.dto.promotion.PromotionProgramDetailDTO;
import vn.viettel.core.dto.promotion.PromotionShopMapDTO;
import vn.viettel.core.enums.PromotionCustObjectType;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.entities.ComboProductDetail;
import vn.viettel.sale.entities.Price;
import vn.viettel.sale.entities.Product;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.sale.messaging.ProductOrderRequest;
import vn.viettel.sale.messaging.OrderPromotionRequest;
import vn.viettel.sale.messaging.SalePromotionCalItemRequest;
import vn.viettel.sale.messaging.SalePromotionCalculationRequest;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.ProductService;
import vn.viettel.sale.service.SalePromotionService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.enums.PriceType;
import vn.viettel.sale.service.enums.PromotionProgramType;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.CustomerTypeClient;
import vn.viettel.sale.service.feign.MemberCardClient;
import vn.viettel.sale.service.feign.PromotionClient;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SalePromotionServiceImpl extends BaseServiceImpl<SaleOrder, SaleOrderRepository> implements SalePromotionService {

    @Autowired
    PromotionClient promotionClient;

    @Autowired
    CustomerClient customerClient;

    @Autowired
    MemberCardClient memberCardClient;

    @Autowired
    SaleOrderDiscountRepository saleOrderDiscountRepo;

    @Autowired
    ProductPriceRepository productPriceRepo;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CustomerTypeClient customerTypeClient;

    @Autowired
    ComboProductDetailRepository comboProductDetailRepo;

    private final int P_ZV01TOZV21 = 1;
    private final int P_ZM = 2;
    private final int P_ZV23 = 3;
    private final String PC_ZV = "zv";
    private final String PC_ZM = "zm";

    /*
    Lấy danh sách các khuyến mãi cho 1 đơn hàng
     */
    @Override
    public List<SalePromotionDTO> getSaleItemPromotions(OrderPromotionRequest request, Long shopId) {
        List<SalePromotionDTO> results = new ArrayList<>();

        /////////////
        SalePromotionDTO itemPromotion1 = new SalePromotionDTO();
        itemPromotion1.setIsUse(true);
        itemPromotion1.setPromotionType(0);
        itemPromotion1.setProgramId(1L);
        itemPromotion1.setPromotionProgramName("Khuyến mãi tự động 1");
        itemPromotion1.setIsEditable(false);
        itemPromotion1.setContraintType(0);

        FreeProductDTO freeProductDTO1 = new FreeProductDTO();
        freeProductDTO1.setProductId(1L);
        freeProductDTO1.setProductCode("SP0001");
        freeProductDTO1.setProductName("Sản phẩm SP0001");
        freeProductDTO1.setQuantity(1);
        freeProductDTO1.setStockQuantity(10);

        FreeProductDTO freeProductDTO2 = new FreeProductDTO();
        freeProductDTO2.setProductId(2L);
        freeProductDTO2.setProductCode("SP0002");
        freeProductDTO2.setProductName("Sản phẩm SP0002");
        freeProductDTO2.setQuantity(2);
        freeProductDTO2.setStockQuantity(1);

        List<FreeProductDTO> lstProduct1 = new ArrayList<>();
        lstProduct1.add(freeProductDTO1);
        lstProduct1.add(freeProductDTO2);

        itemPromotion1.setProducts(lstProduct1);
        results.add(itemPromotion1);

        /////////////

        SalePromotionDTO itemPromotion2 = new SalePromotionDTO();
        itemPromotion2.setIsUse(true);
        itemPromotion2.setPromotionType(0);
        itemPromotion2.setProgramId(2L);
        itemPromotion2.setPromotionProgramName("Khuyến mãi tự động 2");
        itemPromotion2.setIsEditable(true);
        itemPromotion2.setContraintType(0);

        FreeProductDTO freeProductDTO3 = new FreeProductDTO();
        freeProductDTO3.setProductId(3L);
        freeProductDTO3.setProductCode("SP0003");
        freeProductDTO3.setProductName("Sản phẩm SP0003");
        freeProductDTO3.setQuantity(0);
        freeProductDTO3.setStockQuantity(2);
        freeProductDTO3.setQuantityMax(2);

        FreeProductDTO freeProductDTO4 = new FreeProductDTO();
        freeProductDTO4.setProductId(4L);
        freeProductDTO4.setProductCode("SP0004");
        freeProductDTO4.setProductName("Sản phẩm SP0004");
        freeProductDTO4.setQuantity(0);
        freeProductDTO4.setStockQuantity(4);
        freeProductDTO4.setQuantityMax(2);

        List<FreeProductDTO> lstProduct2 = new ArrayList<>();
        lstProduct2.add(freeProductDTO3);
        lstProduct2.add(freeProductDTO4);

        itemPromotion2.setProducts(lstProduct2);
        results.add(itemPromotion2);
        ///////////////////
        SalePromotionDTO itemPromotion5 = new SalePromotionDTO();
        itemPromotion5.setIsUse(true);
        itemPromotion5.setPromotionType(0);
        itemPromotion5.setProgramId(5L);
        itemPromotion5.setPromotionProgramName("Khuyến mãi tự dộng 3");
        itemPromotion5.setIsEditable(false);
        itemPromotion5.setContraintType(0);

        SalePromotionDiscountDTO salePromotionDiscountDTO1 = new SalePromotionDiscountDTO();
        salePromotionDiscountDTO1.setAmount(20000.0);

        itemPromotion5.setAmount(salePromotionDiscountDTO1);
        results.add(itemPromotion5);

        /////////////////////
        SalePromotionDTO itemPromotion3 = new SalePromotionDTO();
        itemPromotion3.setIsUse(true);
        itemPromotion3.setPromotionType(1);
        itemPromotion3.setProgramId(3L);
        itemPromotion3.setPromotionProgramName("Khuyến mãi tay 1");
        itemPromotion3.setIsEditable(true);
        itemPromotion3.setContraintType(0);

        results.add(itemPromotion3);
        /////////////////////

        SalePromotionDTO itemPromotion4 = new SalePromotionDTO();
        itemPromotion4.setIsUse(true);
        itemPromotion4.setPromotionType(1);
        itemPromotion4.setProgramId(4L);
        itemPromotion4.setPromotionProgramName("Khuyến mãi tay 2");
        itemPromotion4.setIsEditable(false);
        itemPromotion4.setContraintType(0);

        FreeProductDTO freeProductDTO5 = new FreeProductDTO();
        freeProductDTO5.setProductId(3L);
        freeProductDTO5.setProductCode("SP0003");
        freeProductDTO5.setProductName("Sản phẩm SP0003");
        freeProductDTO5.setQuantity(0);
        freeProductDTO5.setStockQuantity(2);
        freeProductDTO5.setQuantityMax(2);

        FreeProductDTO freeProductDTO6 = new FreeProductDTO();
        freeProductDTO6.setProductId(4L);
        freeProductDTO6.setProductCode("SP0004");
        freeProductDTO6.setProductName("Sản phẩm SP0004");
        freeProductDTO6.setQuantity(0);
        freeProductDTO6.setStockQuantity(4);
        freeProductDTO6.setQuantityMax(2);

        List<FreeProductDTO> lstProduct3 = new ArrayList<>();
        lstProduct3.add(freeProductDTO5);
        lstProduct3.add(freeProductDTO6);

        itemPromotion4.setProducts(lstProduct3);
        results.add(itemPromotion4);
        /////////////////////

        SalePromotionDTO itemPromotion6 = new SalePromotionDTO();
        itemPromotion6.setIsUse(true);
        itemPromotion6.setPromotionType(1);
        itemPromotion6.setProgramId(6L);
        itemPromotion6.setPromotionProgramName("Khuyến mãi tay 3");
        itemPromotion6.setIsEditable(true);
        itemPromotion6.setContraintType(0);

        SalePromotionDiscountDTO salePromotionDiscountDTO2 = new SalePromotionDiscountDTO();
        salePromotionDiscountDTO2.setAmount(0.0);
        salePromotionDiscountDTO2.setMaxAmount(30000.0);

        itemPromotion6.setAmount(salePromotionDiscountDTO2);
        results.add(itemPromotion6);

        List<SalePromotionDTO> zv01zv21 = getAutoItemPromotionZV01ToZV21(request, shopId);
        if (zv01zv21 != null && zv01zv21.size() > 0){
            zv01zv21.stream().forEachOrdered(results::add);
        }

        List<SalePromotionDTO> autoAmount = getAutoItemPromotionAmount(request, shopId);
        if (autoAmount != null && autoAmount.size() > 0){
            results.stream().forEachOrdered(autoAmount::add);
        }

        List<SalePromotionDTO> autoDiscount = getAutoItemPromotionDiscount(request, shopId);
        if (autoDiscount != null && autoDiscount.size() > 0){
            results.stream()
                    .forEachOrdered(autoDiscount::add);
        }

        List<SalePromotionDTO> zm = getItemPromotionZM(request, shopId);
        if (zm != null && zm.size() > 0){
            results.stream().forEachOrdered(zm::add);
        }

        List<SalePromotionDTO> zv23 = getItemPromotionZV23(request, shopId);
        if (zv23 != null && zv23.size() > 0){
            results.stream()
                    .forEachOrdered(zv23::add);
        }

        List<SalePromotionDTO> amount = getItemPromotionAmount(request, shopId);
        if (amount != null && amount.size() > 0){
            results.stream().forEachOrdered(amount::add);
        }

        List<SalePromotionDTO> discount = getItemPromotionDiscount(request, shopId);
        if (discount != null && discount.size() > 0){
            results.stream().forEachOrdered(discount::add);
        }

        List<SalePromotionDTO> newYear = getItemPromotionNewYear(request, shopId);
        if (newYear != null && newYear.size() > 0){
            results.stream().forEachOrdered(newYear::add);
        }

        return results;
    }

    /*
    Lấy danh sách khuyến mãi ZV01 đến ZV21
     */
    private List<SalePromotionDTO> getAutoItemPromotionZV01ToZV21(OrderPromotionRequest request, Long shopId){
        List<SalePromotionDTO> results = new ArrayList<>();
        CustomerDTO customer = customerClient.getCustomerByIdV1(request.getCustomerId()).getData();
        if(customer == null) throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
        // Danh sách chương trình khuyến mãi thỏa các điều kiện cửa hàng, khách hàng
        List<PromotionProgramDTO> programs = this.validPromotionProgram(request, shopId, customer);
        if(programs.isEmpty()) return null;

        // get default warehouse
        CustomerTypeDTO customerType = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        Long warehouseId = 0L;
        if (customerType != null)
            warehouseId = customerType.getWareHouseTypeId();

        ProductOrderDataDTO orderData = this.getProductOrderData(request, customer);
        for (PromotionProgramDTO program: programs) {
            SalePromotionDTO auto = null;
            switch (PromotionProgramType.valueOf(program.getType())) {
                case ZV01:
                case ZV02:
                case ZV03:
                case ZV04:
                case ZV05:
                case ZV06:
                    auto = this.getZV01ToZV06(program, orderData, shopId, warehouseId);
                    break;
                case ZV07:
                    auto = this.zV07(program, orderData);
                    break;
                case ZV08:
                    auto = this.zV08(program, orderData);
                    break;
                case ZV10:
                    auto = this.zV10(program, orderData);
                    break;
                case ZV19:
                    // Todo
                    break;
                default:
                    // Todo
            }

            if(auto != null){
                auto.setPromotionType(0);
                auto.setProgramId(program.getId());
                auto.setPromotionProgramName(program.getPromotionProgramName());
                if (program.getIsEdited() != null && program.getIsEdited() == 0)
                    auto.setIsEditable(false);
                else
                    auto.setIsEditable(true);
                if (program.getRelation() == null || program.getRelation() == 0)
                    auto.setContraintType(0);
                else
                    auto.setContraintType(program.getRelation());

                PromotionShopMapDTO promotionShopMap = promotionClient.getPromotionShopMapV1(program.getId(), shopId).getData();
                if(promotionShopMap.getQuantityMax() == null) auto.setIsUse(true);
                if(auto.getAmount() != null && promotionShopMap.getQuantityMax() != null) {
                    double quantityReceive = promotionShopMap.getQuantityReceived()!=null?promotionShopMap.getQuantityReceived():0;
                    if(promotionShopMap.getQuantityMax() >= (quantityReceive + auto.getAmount().getAmount())) auto.setIsUse(true);
                    else auto.setIsUse(false);
                }

                results.add(auto);
            }
        }


        return results;
    }

    /*
    Lấy danh sách khuyến mãi tay ZM
     */
    private List<SalePromotionDTO> getItemPromotionZM(OrderPromotionRequest request, Long shopId){
        List<SalePromotionDTO> results = new ArrayList<>();

        return results;
    }

    /*
    Lấy danh sách khuyến mãi ZV23
     */
    private List<SalePromotionDTO> getItemPromotionZV23(OrderPromotionRequest request, Long shopId){
        List<SalePromotionDTO> results = new ArrayList<>();

        return results;
    }

    /*
    Lấy danh sách khuyến mãi tay tặng tiền
     */
    private List<SalePromotionDTO> getItemPromotionAmount(OrderPromotionRequest request, Long shopId){
        List<SalePromotionDTO> results = new ArrayList<>();

        return results;
    }

    /*
    Lấy danh sách khuyến mãi tay giảm giá
     */
    private List<SalePromotionDTO> getItemPromotionDiscount(OrderPromotionRequest request, Long shopId){
        List<SalePromotionDTO> results = new ArrayList<>();

        return results;
    }

    /*
    Lấy danh sách khuyến mãi tự động tặng tiền
     */
    private List<SalePromotionDTO> getAutoItemPromotionAmount(OrderPromotionRequest request, Long shopId){
        List<SalePromotionDTO> results = new ArrayList<>();

        return results;
    }

    /*
    Lấy danh sách khuyến mãi tự động giảm giá
     */
    private List<SalePromotionDTO> getAutoItemPromotionDiscount(OrderPromotionRequest request, Long shopId){
        List<SalePromotionDTO> results = new ArrayList<>();

        return results;
    }

    /*
    Lấy danh sách khuyến mãi tích lỹ quà tết
     */
    private List<SalePromotionDTO> getItemPromotionNewYear(OrderPromotionRequest request, Long shopId){
        List<SalePromotionDTO> results = new ArrayList<>();

        return results;
    }

    /*
    Tính tiền khuyến mãi và tiền cần thanh toán cho 1 đơn hàng
     */
    @Override
    public SalePromotionCalculationDTO promotionCalculation(SalePromotionCalculationRequest calculationRequest, Long shopId){
        SalePromotionCalculationDTO result = new SalePromotionCalculationDTO();
        double promotionAmount = 0;
        double paymentAmount = 0;

        if (calculationRequest.getTotalAmount() != null && calculationRequest.getTotalAmount() != 0){
            paymentAmount = calculationRequest.getTotalAmount();

            //tính tiền khuyến mãi
            //o	TT tính KM = Tổng tiền đơn hàng – Tổng tiền chiết khấu từ các CTKM (ZV01 – ZV21 – ZM) – Tổng tiền chiết khấu từ CTKM ZV23
            if (calculationRequest.getPromotionInfo() != null && calculationRequest.getPromotionInfo().size() > 0){
                double amount1 = 0;
                double amount2 = 0;
                double amount3 = 0;
                double percent1 = 0;
                double percent2 = 0;
                double percent3 = 0;
                for (SalePromotionCalItemRequest item : calculationRequest.getPromotionInfo()){
                    if (item.getProgramId() != null){
                        PromotionProgramDTO programDTO = promotionClient.getByIdV1(item.getProgramId()).getData();
                        if (programDTO != null){
                            if (item.getAmount() != null){
                                if (programDTO.getAmountOrderType() != null && programDTO.getDiscountType() != null && programDTO.getDiscountPriceType() != null){
                                    // chiết khấu tiền
                                    if (programDTO.getDiscountType() == 0 && item.getAmount().getAmount() != null && item.getAmount().getAmount() > 0){
                                        // giảm giá đã gồm vat
                                        if (programDTO.getDiscountPriceType() == 1){
                                            // giảm giá trên tổng tiền
                                            if (programDTO.getAmountOrderType() == 0)
                                                amount1 += item.getAmount().getAmount();

                                                // giảm giá sau khi chiết khấu đợt 1
                                            else if (programDTO.getAmountOrderType() == 1)
                                                amount2 += item.getAmount().getAmount();

                                                // giảm giá sau khi chiết khấu đợt 2
                                            else if (programDTO.getAmountOrderType() == 2)
                                                amount3 += item.getAmount().getAmount();
                                        }
                                        // giảm giá chưa gồm vat
                                        else{
                                            // giảm giá trên tổng tiền
                                            if (programDTO.getAmountOrderType() == 0)
                                                amount1 += item.getAmount().getAmount() * 10/100;

                                                // giảm giá sau khi chiết khấu đợt 1
                                            else if (programDTO.getAmountOrderType() == 1)
                                                amount2 += item.getAmount().getAmount() * 10/100;

                                                // giảm giá sau khi chiết khấu đợt 2
                                            else if (programDTO.getAmountOrderType() == 2)
                                                amount3 += item.getAmount().getAmount() * 10/100;
                                        }
                                    }
                                    // chiết khấu %
                                    else if (programDTO.getDiscountType() == 1 &&  item.getAmount().getPercentage() != null && item.getAmount().getPercentage() > 0){
                                        // giảm giá trên tổng tiền
                                        if (programDTO.getAmountOrderType() == 0)
                                            percent1 += item.getAmount().getPercentage();

                                            // giảm giá sau khi chiết khấu đợt 1
                                        else if (programDTO.getAmountOrderType() == 1)
                                            percent2 += item.getAmount().getPercentage();

                                            // giảm giá sau khi chiết khấu đợt 2
                                        else if (programDTO.getAmountOrderType() == 2)
                                            percent3 += item.getAmount().getPercentage();
                                    }
                                }
                            }
                        }
                    }
                }

                if (percent1 > 0){
                    amount1 += paymentAmount * percent1 / 100;
                }
                promotionAmount += amount1;

                if (percent2 > 0){
                    amount2 += (paymentAmount - amount1) * percent2 / 100;
                }
                promotionAmount += amount2;

                if (percent3 > 0){
                    amount3 += (paymentAmount - (amount1 + amount2)) * percent3 / 100;
                }
                promotionAmount += amount3;
            }

            // trừ tiền khuyến mãi
            if (promotionAmount > paymentAmount){
                paymentAmount = 0;
            }else{
                paymentAmount = paymentAmount - promotionAmount;
            }

            // trừ tiền giảm giá
            if (calculationRequest.getSaleOffAmount() != null){
                if (calculationRequest.getSaleOffAmount() > paymentAmount){
                    paymentAmount = 0;
                }else{
                    paymentAmount = paymentAmount - calculationRequest.getSaleOffAmount();
                }
            }

            // trừ tiền voucher
            if (calculationRequest.getVoucherAmount() != null){
                if (calculationRequest.getVoucherAmount() > paymentAmount){
                    paymentAmount = 0;
                }else{
                    paymentAmount = paymentAmount - calculationRequest.getVoucherAmount();
                }
            }

            // trừ tiền tích lũy
            if (calculationRequest.getSaveAmount() != null){
                if (calculationRequest.getSaveAmount() > paymentAmount){
                    paymentAmount = 0;
                }else{
                    paymentAmount = paymentAmount - calculationRequest.getSaveAmount();
                }
            }
        }

        result.setPromotionAmount(promotionAmount);
        result.setPaymentAmount(paymentAmount);

        return result;
    }

    /*
    kiểm tra KM là ZM hay ZV01-ZV21 hay ZV23
     */
    private Integer checkPromotionType(String type){
        if (type != null && !type.trim().equals("")){
            type = type.trim().toLowerCase();
            if (type.startsWith(PC_ZM))
                return P_ZM;
            if (type.startsWith(PC_ZV)){
                String strNo = type.replaceAll("[a-zA-Z]","");
                if(!strNo.trim().equals("")){
                    Integer number = Integer.parseInt(strNo);
                    if (number != null && number > 0 && number < 22){
                        return P_ZV01TOZV21;
                    }
                    if (number != null && number == 23){
                        return P_ZV23;
                    }
                }
            }
        }

        return 0;
    }

    /*
     *ZV02
     *Mua 1 sản phẩm, với số lượng xác định, giảm số tiền).Vd: Mua 5 hộp A, giảm 5000 đồng
     */
    public SalePromotionDTO getZV01ToZV06(PromotionProgramDTO program, ProductOrderDataDTO orderData, Long shopId, Long warehouseId) {

        if (program == null || orderData == null || orderData.getProducts() == null || orderData.getProducts().isEmpty())
            return null;

        SalePromotionDTO salePromotion = new SalePromotionDTO();

        HashMap<Long, ProductOrderDetailDataDTO> mapProductOrder = new HashMap<>();
        // gộp sản phẩm nếu có mua sản phẩm trùng
        List<ProductOrderDetailDataDTO> orderTmp = orderData.getProducts().stream().map(detail -> {
            if(detail.getQuantity() == null) detail.setQuantity(0);
            if(detail.getTotalPrice() == null) detail.setTotalPrice(0.0);
            return detail;
        }).collect(Collectors.toList());

        for (ProductOrderDetailDataDTO dto : orderTmp){
            if (mapProductOrder.containsKey(dto.getProductId())){
                ProductOrderDetailDataDTO exited = mapProductOrder.get(dto.getProductId());
                exited.setQuantity(exited.getQuantity() + dto.getQuantity());
                mapProductOrder.put(dto.getProductId(), exited);
            }else{
                mapProductOrder.put(dto.getProductId(), dto);
            }
        }

        List<ProductOrderDetailDataDTO> productOrders = new ArrayList<>(mapProductOrder.values());

        List<PromotionProgramDetailDTO> details = promotionClient.findPromotionProgramDetailV1(program.getId()).getData();
        if(details.isEmpty()) return null;

        List<PromotionProgramDetailDTO> programDetails = details.stream().map(detail -> {
            if(detail.getSaleQty() == null) detail.setSaleQty(0);
            if(detail.getDisPer() == null) detail.setDisPer(0.0);
            return detail;
        }).collect(Collectors.toList());

        if (program.getGivenType() != null){
            // zv01 , zv02, zv04, zv05
            if (program.getGivenType() == 0){ // KM tien
                double amountPromotion = 0;
                double percentPromotion = 0;
                for (ProductOrderDetailDataDTO productOrder : productOrders){
                    for (PromotionProgramDetailDTO productPromotion : programDetails){
                        if(productPromotion.getRequired()!=null && productPromotion.getRequired() == 1 && productOrder.getProductId().equals(productPromotion.getProductId())){ //bắt buộc phải mua SP
                            if ((productOrder.getQuantity() >= productPromotion.getSaleQty()) // Mua 1 sản phẩm, với số lượng xác định
                                || (productOrder.getTotalPrice() >= productPromotion.getSaleAmt()) //Mua 1 sản phẩm, với số tiền đạt mức
                                ){
                                if (productPromotion.getDiscAmt() != null && productPromotion.getDiscAmt() != 0){
                                    amountPromotion += productPromotion.getDiscAmt();
                                }else if (productPromotion.getDisPer() != null && productPromotion.getDisPer() != 0){
                                    percentPromotion += productPromotion.getDisPer();
                                }
                            }
                        }
                    }
                }

                if (amountPromotion == 0 && percentPromotion == 0)
                    return null;
                SalePromotionDiscountDTO discountDTO = new SalePromotionDiscountDTO();
                if (amountPromotion > 0){
                    discountDTO.setMaxAmount(amountPromotion);
                    discountDTO.setAmount(amountPromotion);
                }else{
                    discountDTO.setPercentage(percentPromotion);
                }

                salePromotion.setAmount(discountDTO);
            } // end KM tien
            // zv03 , zv06
            else if (program.getGivenType() == 1) { // KM san pham
                List<FreeProductDTO> lstProductPromotion = new ArrayList<>();
                for (ProductOrderDetailDataDTO productOrder : productOrders){
                    for (PromotionProgramDetailDTO productPromotion : programDetails){
                        if(productPromotion.getRequired()!=null && productPromotion.getRequired() == 1 && productOrder.getProductId().equals(productPromotion.getProductId())){ //bắt buộc phải mua SP
                            if ((productOrder.getQuantity() >= productPromotion.getSaleQty()) // Mua 1 sản phẩm, với số lượng xác định
                                    || (productOrder.getTotalPrice() >= productPromotion.getSaleAmt()) //Mua 1 sản phẩm, với số tiền đạt mức
                            ){
                                if (productPromotion.getFreeProductId() != null){
                                    FreeProductDTO freeProductDTO = productRepository.getFreeProductDTONoOrder(shopId, warehouseId, productPromotion.getFreeProductId());
                                    if (freeProductDTO != null){
                                        freeProductDTO.setQuantityMax(productPromotion.getFreeQty());
                                        if (program.getIsEdited() == null || program.getIsEdited() == 0){
                                            if ( productPromotion.getFreeQty() > freeProductDTO.getStockQuantity()) {
                                                freeProductDTO.setQuantity(freeProductDTO.getStockQuantity());
                                            }
                                            else{
                                                freeProductDTO.setQuantity(productPromotion.getFreeQty());
                                            }
                                        }else{
                                            freeProductDTO.setQuantity(0);
                                        }
                                        lstProductPromotion.add(freeProductDTO);
                                    }
                                }
                            }
                        }
                    }
                }

                if (lstProductPromotion.isEmpty())
                    return null;

                salePromotion.setProducts(lstProductPromotion);
            }
        }

        return salePromotion;
    }


    /*
     * ZV07
     * Mua 1 nhóm sản phẩm nào đó - với số lượng xác định (tổng), thì được giảm % tổng tiền.
     * Vd: Mua nhóm sản phẩm ( A , B, C) với số lượng 50 hộp, được giảm 5% tổng tiền cho nhóm sản phẩm này.
     */
    public SalePromotionDTO zV07(PromotionProgramDTO program, ProductOrderDataDTO orderData) {
        SalePromotionDTO auto = new SalePromotionDTO();
        List<ProductOrderDetailDataDTO> productOrders = orderData.getProducts();
        List<PromotionProgramDetailDTO> details = promotionClient.findPromotionProgramDetailV1(program.getId()).getData();
        if(details.isEmpty()) return null;

        List<PromotionProgramDetailDTO> programDetails = details.stream().map(detail -> {
            if(detail.getSaleQty() == null) detail.setSaleQty(0);
            if(detail.getDisPer() == null) detail.setDisPer(0.0);
            return detail;
        }).collect(Collectors.toList());

        //Kt đk sản phẩm bắt buộc
        Map<Long, Integer> orderRequies = programDetails.stream()
                .filter(detail -> detail.getRequired()!=null && detail.getRequired() == 1)
                .collect(Collectors.toMap(PromotionProgramDetailDTO::getProductId, PromotionProgramDetailDTO::getSaleQty));

        List<ProductOrderDetailDataDTO> productEquals = productOrders.stream().filter(product ->
                orderRequies.keySet().contains(product.getProductId()) && product.getQuantity() >= orderRequies.get(product.getProductId()))
                .collect(Collectors.toList());
        if(productEquals.size() != orderRequies.size()) return null;

        // Kt dk mua đủ số lượng trong bộ sp
        Map<Long, PromotionProgramDetailDTO> productsOrderMaps = programDetails.stream()
                .collect(Collectors.toMap(PromotionProgramDetailDTO::getProductId, Function.identity()));

        Integer quantityRequire = programDetails.stream().map(PromotionProgramDetailDTO::getSaleQty).reduce(0,Integer::sum);
        Integer quantityOrder = productOrders.stream().filter(p -> productsOrderMaps.keySet().contains(p.getProductId()))
                                    .map(ProductOrderDetailDataDTO::getQuantity).reduce(0,Integer::sum);
        if(quantityOrder < quantityRequire) return null;

        //Tính khuyến mãi
        SalePromotionDiscountDTO discountDTO = new SalePromotionDiscountDTO();
        for(ProductOrderDetailDataDTO product: productOrders) {
            PromotionProgramDetailDTO promotion = productsOrderMaps.get(product.getProductId());
            if(promotion != null) {
                if((program.getDiscountPriceType() == PriceType.VAT.getValue()))
                    discountDTO.addAmount(product.getTotalPrice()*(promotion.getDisPer()/100));
                if((program.getDiscountPriceType() == PriceType.NOT_VAT.getValue()))
                    discountDTO.addAmount(product.getTotalPriceNotVAT()*(promotion.getDisPer()/100));
            }
        }

        auto.setAmount(discountDTO);
        return auto;
    }

    /*
     * ZV08
     *  Mua 1 nhóm sản phẩm nào đó – với số lượng xác định (tổng), thì được giảm trừ tiền.
     * Vd: Mua nhóm sản phẩm (A, B, C) với số lượng 50 hộp, được giảm trừ 5000 đ.
     */
    public SalePromotionDTO zV08(PromotionProgramDTO program, ProductOrderDataDTO orderData) {
        SalePromotionDTO auto = new SalePromotionDTO();
        List<ProductOrderDetailDataDTO> productOrders = orderData.getProducts();
        List<PromotionProgramDetailDTO> details = promotionClient.findPromotionProgramDetailV1(program.getId()).getData();
        if(details.isEmpty()) return null;

        List<PromotionProgramDetailDTO> programDetails = details.stream().map(detail -> {
            if(detail.getSaleQty() == null) detail.setSaleQty(0);
            if(detail.getDiscAmt() == null) detail.setDiscAmt(0.0);
            return detail;
        }).collect(Collectors.toList());

        //Kt điều kiện sản phẩm bắt buộc
        Map<Long, Integer> orderRequies = programDetails.stream()
                .filter(detail -> detail.getRequired()!=null && detail.getRequired() == 1)
                .collect(Collectors.toMap(PromotionProgramDetailDTO::getProductId, PromotionProgramDetailDTO::getSaleQty));

        List<ProductOrderDetailDataDTO> productEquals = productOrders.stream().filter(product ->
                orderRequies.keySet().contains(product.getProductId()) && product.getQuantity() >= orderRequies.get(product.getProductId()))
                .collect(Collectors.toList());
        if(productEquals.size() != orderRequies.size()) return null;

        // Kt dk mua đủ số lượng trong bộ sp
        Map<Long, PromotionProgramDetailDTO> productsOrderMaps = programDetails.stream()
                .collect(Collectors.toMap(PromotionProgramDetailDTO::getProductId, Function.identity()));

        Integer quantityRequire = programDetails.stream().map(PromotionProgramDetailDTO::getSaleQty).reduce(0,Integer::sum);
        Integer quantityOrder = productOrders.stream().filter(p -> productsOrderMaps.keySet().contains(p.getProductId()))
                .map(ProductOrderDetailDataDTO::getQuantity).reduce(0,Integer::sum);
        if(quantityOrder < quantityRequire) return null;

        //Tính khuyến mãi tặng tiền
        SalePromotionDiscountDTO discountDTO = new SalePromotionDiscountDTO();
        for(ProductOrderDetailDataDTO product: productOrders) {
            PromotionProgramDetailDTO promotion = productsOrderMaps.get(product.getProductId());
            if(promotion != null) discountDTO.addAmount(promotion.getDiscAmt());
        }

        auto.setAmount(discountDTO);
        return auto;
    }

    /*
     *ZV10
     *Mua 1 nhóm sản phẩm nào đó – với số tiền xác định (tổng), thì được giảm % tổng tiền của nhóm này.
     *Vd: Mua nhóm sản phẩm (A, B, C) với tổng tiền là 500.000 đ, thì được giảm 10%.
     */
    public SalePromotionDTO zV10(PromotionProgramDTO program, ProductOrderDataDTO orderData) {
        SalePromotionDTO auto = new SalePromotionDTO();
        List<ProductOrderDetailDataDTO> productOrders = orderData.getProducts();
        List<PromotionProgramDetailDTO> details = promotionClient.findPromotionProgramDetailV1(program.getId()).getData();
        if(details.isEmpty()) return null;

        List<PromotionProgramDetailDTO> programDetails = details.stream().map(detail -> {
            if(detail.getSaleAmt() == null) detail.setSaleAmt(0.0);
            if(detail.getDisPer() == null) detail.setDisPer(0.0);
            return detail;
        }).collect(Collectors.toList());

        //Kt đk sản phẩm bắt buộc
        Map<Long, Double> orderRequies = programDetails.stream()
                .filter(detail -> detail.getRequired()!=null && detail.getRequired() == 1)
                .collect(Collectors.toMap(PromotionProgramDetailDTO::getProductId, PromotionProgramDetailDTO::getSaleAmt));

        // chuong trinh tinh KM tren gia truoc thue + dk tong gia mua cua sp
        SalePromotionDiscountDTO discountDTO = new SalePromotionDiscountDTO();
        Map<Long, PromotionProgramDetailDTO> productsOrderMaps = programDetails.stream()
                .collect(Collectors.toMap(PromotionProgramDetailDTO::getProductId, Function.identity()));
        // Tổng tiền cần mua
        Double amountRequire = programDetails.stream().map(PromotionProgramDetailDTO::getSaleAmt).reduce(0.0,Double::sum);
        if(program.getDiscountPriceType() == PriceType.NOT_VAT.getValue()) {
            List<ProductOrderDetailDataDTO> productEquals = productOrders.stream().filter(product ->
                    orderRequies.keySet().contains(product.getProductId()) && product.getTotalPriceNotVAT() >= orderRequies.get(product.getProductId()))
                    .collect(Collectors.toList());
            if(productEquals.size() != orderRequies.size()) return null;

            // Kt dk mua đủ tổng tiền trong bộ sp
            Double amountOrder = productOrders.stream().filter(p -> productsOrderMaps.keySet().contains(p.getProductId()))
                    .map(ProductOrderDetailDataDTO::getTotalPriceNotVAT).reduce(0.0,Double::sum);
            if(amountOrder < amountRequire) return null;
            //Tính khuyến mãi
            for(ProductOrderDetailDataDTO product: productOrders) {
                PromotionProgramDetailDTO promotion = productsOrderMaps.get(product.getProductId());
                if(promotion != null)  discountDTO.addAmount(product.getTotalPriceNotVAT()*(promotion.getDisPer()/100));
            }
        }
        // chuong trinh tinh KM tren gia sau thue + dk tong gia mua cua sp
        if(program.getDiscountPriceType() == PriceType.VAT.getValue()) {
            List<ProductOrderDetailDataDTO> productEquals = productOrders.stream().filter(product ->
                    orderRequies.keySet().contains(product.getProductId()) && product.getTotalPrice() >= orderRequies.get(product.getProductId()))
                    .collect(Collectors.toList());
            if(productEquals.size() != orderRequies.size()) return null;

            // Kt dk mua đủ tổng tiền trong bộ sp
            Double amountOrder = productOrders.stream().filter(p -> productsOrderMaps.keySet().contains(p.getProductId()))
                    .map(ProductOrderDetailDataDTO::getTotalPrice).reduce(0.0,Double::sum);
            if(amountOrder < amountRequire) return null;
            //Tính khuyến mãi
            for(ProductOrderDetailDataDTO product: productOrders) {
                PromotionProgramDetailDTO promotion = productsOrderMaps.get(product.getProductId());
                if(promotion != null)  discountDTO.addAmount(product.getTotalPrice()*(promotion.getDisPer()/100));
            }
        }

        auto.setAmount(discountDTO);
        return auto;
    }



    //Kiểm tra các chwuong trình hợp lệ
    public List<PromotionProgramDTO> validPromotionProgram(OrderPromotionRequest request, Long shopId, CustomerDTO customer) {

        List<PromotionProgramDTO> programs = promotionClient.findPromotionPrograms(shopId).getData();
        // Kiểm tra loại đơn hàng tham gia & Kiểm tra thuộc tính khách hàng tham gia
        return programs.stream().filter(program -> {
            //Kiểm tra giới hạn số lần được KM của KH
            Integer numberDiscount = saleOrderDiscountRepo.countDiscount(shopId, customer.getId());
            if(program.getPromotionDateTime() != null && program.getPromotionDateTime() <= numberDiscount) return false;

            // Kiểm tra loại đơn hàng tham gia
            if(program.getObjectType() != null && program.getObjectType() != 0) {
                Set<Long> orderTypes = promotionClient.findCusCardPromotion(program.getId(), PromotionCustObjectType.ORDER_TYPE.getValue()).getData();
                if(!orderTypes.contains(Long.valueOf(request.getOrderType()))) return false;
            }
            // Kiểm tra thuộc tính khách hàng tham gia
            Set<Long> customerTypes = promotionClient.findCusCardPromotion(program.getId(), PromotionCustObjectType.CUSTOMER_TYPE.getValue()).getData();
            if(!customerTypes.isEmpty() && !customerTypes.contains(customer.getCustomerTypeId())) return false;

            Set<Long> memberCards = promotionClient.findCusCardPromotion(program.getId(), PromotionCustObjectType.MEMBER_CARD.getValue()).getData();
            MemberCardDTO memberCard = memberCardClient.getByCustomerId(customer.getId()).getData();
            if(!memberCards.isEmpty() && !memberCards.contains(memberCard!=null?memberCard.getId():null)) return false;

            Set<Long> loyalCustomers = promotionClient.findCusCardPromotion(program.getId(), PromotionCustObjectType.LOYAL_CUSTOMER.getValue()).getData();
            if(!loyalCustomers.isEmpty() && !loyalCustomers.contains(customer.getCloselyTypeId())) return false;

            Set<Long> customerCardTypes = promotionClient.findCusCardPromotion(program.getId(), PromotionCustObjectType.CUSTOMER_CARD_TYPE.getValue()).getData();
            if(!customerCardTypes.isEmpty() && !customerCardTypes.contains(customer.getCardTypeId())) return false;
            // Kiểm tra quy định tính chiết khấu của đơn hàng

            return true;
        }).collect(Collectors.toList());
    }

    public ProductOrderDataDTO getProductOrderData(OrderPromotionRequest request, CustomerDTO customer) {
        ProductOrderDataDTO orderDataDTO = new ProductOrderDataDTO(request.getOrderType());
        List<ProductOrderRequest> products = request.getProducts();

        //Gộp sản phẩm combo - nếu sản phẩm Combo có chứa các sản phẩm thỏa CTKM thì vẫn được hưởng KM
        Map<Long, ProductOrderRequest> productMaps = products.stream().collect(Collectors.toMap(ProductOrderRequest::getProductId, Function.identity()));
        List<ProductOrderRequest> productCombos = products.stream().filter(ProductOrderRequest::isCombo).collect(Collectors.toList());
        for (ProductOrderRequest combo: productCombos) {
            Product product = productRepository.findById(combo.getProductId())
                .orElseThrow(() -> new ValidateException(ResponseMessage.PRODUCT_DOES_NOT_EXISTS));
            List<ComboProductDetail> comboDetails = comboProductDetailRepo.findByComboProductIdAndStatus(product.getComboProductId(), 1);
            for(ComboProductDetail detail: comboDetails) {
                if(productMaps.containsKey(detail.getProductId())){
                    ProductOrderRequest productOrder = productMaps.get(detail.getProductId());
                    productOrder.setQuantity(productOrder.getQuantity() + (combo.getQuantity()*detail.getFactor()));
                }else{
                    Product productDb = productRepository.findById(detail.getProductId())
                        .orElseThrow(() -> new ValidateException(ResponseMessage.PRODUCT_DOES_NOT_EXISTS));
                    ProductOrderRequest productOrder = new ProductOrderRequest();
                    productOrder.setProductId(detail.getProductId());
                    productOrder.setProductCode(productDb.getProductCode());
                    productOrder.setQuantity(combo.getQuantity()*detail.getFactor());
                    productMaps.put(detail.getProductId(), productOrder);
                }
            }
        }

        List<ProductOrderRequest> productOrders = new ArrayList<>(productMaps.values());
        for (ProductOrderRequest product: productOrders) {
            Price price = productPriceRepo.getProductPrice(product.getProductId(), customer.getCustomerTypeId());
            if(price == null) throw new ValidateException(ResponseMessage.NO_PRICE_APPLIED);
            ProductOrderDetailDataDTO detail = new ProductOrderDetailDataDTO();
            detail.setProductId(product.getProductId());
            detail.setProductCode(product.getProductCode());
            detail.setQuantity(product.getQuantity());
            detail.setPrice(price.getPrice());
            detail.setPriceNotVAT(price.getPriceNotVat());

            orderDataDTO.addProduct(detail);
            orderDataDTO.addQuantity(detail.getQuantity());
            orderDataDTO.addTotalPrice(detail.getTotalPrice());
            orderDataDTO.addTotalPriceNotVAT(detail.getTotalPriceNotVAT());
        }
        return orderDataDTO;
    }

}









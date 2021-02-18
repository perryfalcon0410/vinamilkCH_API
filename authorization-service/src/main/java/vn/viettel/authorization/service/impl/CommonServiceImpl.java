package vn.viettel.authorization.service.impl;

import vn.viettel.core.messaging.Response;
import vn.viettel.authorization.repository.UserRepository;
import vn.viettel.authorization.service.CommonService;
import vn.viettel.authorization.service.DistributorService;
import vn.viettel.authorization.service.dto.DistributorDTO;
import vn.viettel.authorization.service.dto.group.GroupDTO;
import vn.viettel.authorization.service.dto.shop.ShopDTO;
import vn.viettel.authorization.service.feign.GroupClient;
import vn.viettel.authorization.service.feign.ShopClient;
import vn.viettel.core.db.entity.User;
import vn.viettel.core.service.BaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CommonServiceImpl extends BaseServiceImpl<User, UserRepository> implements CommonService {

    Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);

    private final ShopClient shopClient;
    private final GroupClient groupClient;
    private final DistributorService distributorService;

    public CommonServiceImpl(ShopClient shopClient, GroupClient groupClient, DistributorService distributorService) {
        this.shopClient = shopClient;
        this.groupClient = groupClient;
        this.distributorService = distributorService;
    }

    @Override
    public ShopDTO getShopBy(Long shopId) {
        logger.info("#getShopBy - start");
        ShopDTO shop = null;
        Long distributorId = null;
        Response<ShopDTO> response = null;
        Response<DistributorDTO> responseDis = null;
        try {
            if (isAdministrator()) {
                shop = shopClient.getShopById(shopId);
            } else if (isDistributor()) {
                responseDis = distributorService.getDistributorByUserId(getUserId());
                if (responseDis != null && responseDis.getSuccess()) {
                    distributorId = responseDis.getData().getId();
                } else {
                    return shop;
                }
                response = shopClient.getByDistributorId(shopId, distributorId);
                if (response != null && response.getSuccess()) {
                    shop = response.getData();
                }
            } else {
                response = shopClient.getShopByIdAndUserId(shopId, getUserId());
            }
            if (response != null && response.getSuccess()) {
                shop = response.getData();
            }
            return shop;
        } catch (Exception ex) {
            logger.error("ERROR: " + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public GroupDTO getGroupBy(Long groupId) {
        logger.info("#getGroupBy - start");
        Long distributorId = null;
        GroupDTO groupDTO = null;
        Response<DistributorDTO> responseDis = null;
        Response<GroupDTO> response = null;
        try {
            if (isAdministrator()) {
                response = groupClient.getGroupByGroupId(groupId);
            } else if (isDistributor()) {
                responseDis = distributorService.getDistributorByUserId(getUserId());
                if (responseDis != null && responseDis.getSuccess()) {
                    distributorId = responseDis.getData().getId();
                } else {
                    return groupDTO;
                }
                response = groupClient.getByDistributorId(groupId, distributorId);
            } else {
                response = groupClient.getGroupByIdAndUserId(groupId, this.getUserId());
            }
            if (response.getSuccess()) {
                groupDTO = response.getData();
            }
            return groupDTO;
        } catch (Exception ex) {
            logger.error("ERROR: " + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }
}

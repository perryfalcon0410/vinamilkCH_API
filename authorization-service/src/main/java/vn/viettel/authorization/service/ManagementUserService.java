package vn.viettel.authorization.service;

import vn.viettel.authorization.messaging.user.*;
import vn.viettel.authorization.service.dto.user.GroupManagementUserInfoDTO;
import vn.viettel.authorization.service.dto.user.ShopManagementUsersInfoDTO;
import vn.viettel.core.db.entity.ManagementUsers;
import vn.viettel.core.dto.salon.SalonConfirmationHairdresserDetailDTO;
import vn.viettel.core.dto.salon.SalonHairdresserResponseDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.authorization.messaging.user.*;

import java.util.List;

public interface ManagementUserService extends BaseService {

    Response<ShopManagementUsersInfoDTO> getShopManagementUserInfo(ShopManagementUserInfoRequest request);

    Response<GroupManagementUserInfoDTO> getGroupManagementUserInfo(GroupManagementUserInfoRequest request);

    Response<String> updateShopManagerUser(ShopManagerUserUpdateRequest request);

    Response<String> updateGroupManagerUser(GroupManagerUserUpdateRequest request);

    Response<String> updateShopEmployeeUser(ShopEmployeeUserUpdateRequest request);

    Response<String> updateGroupEmployeeUser(GroupEmployeeUserUpdateRequest request);

    Response<String> updatePasswordShopEmployeeUser(ShopEmployeeUserUpdatePasswordRequest request);

    Response<String> updatePasswordGroupEmployeeUser(GroupEmployeeUserUpdatePasswordRequest request);

    Response<Boolean> isExpiredPasswordShopEmployeeUser(ShopEmployeeExpiredPasswordRequest request);

    Response<Boolean> isExpiredPasswordGroupEmployeeUser(GroupEmployeeExpiredPasswordRequest request);

    ManagementUsers getByEmailAndCompanyId(String email);

    /**
     * Get all hairdressers in salon by salon Id
     *
     * @param salonId
     * @return
     */
    List<SalonHairdresserResponseDTO> getAllHairdressersInSalon(Long salonId);

    /**
     * Get hairdresser by id
     *
     * @param hairdresserId
     * @return
     */
    SalonHairdresserResponseDTO getHairdresserById(Long hairdresserId);

    /**
     * Get Salon hairdressers by user ids
     *
     * @param userIds id of user
     * @return List<SalonConfirmationHairdresserDetailDTO>
     */
    List<SalonConfirmationHairdresserDetailDTO> feignGetHairdressersByIds(List<Long> userIds);

    /**
     * Get hairdressers entity by userIds
     *
     * @param userIds id of user
     * @return List<ManagementUsers>
     */
    List<ManagementUsers> feignGetHairdressersEntityByIds(List<Long> userIds);

    /**
     * Get management user by id
     *
     * @param id
     * @return ManagementUsers
     */
    ManagementUsers getManagementUserById(Long id);
}

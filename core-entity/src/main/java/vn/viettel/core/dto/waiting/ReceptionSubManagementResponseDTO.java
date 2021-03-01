package vn.viettel.core.dto.waiting;

import vn.viettel.core.dto.salon.SalonConfirmationHairdresserDetailDTO;

import java.util.List;

public class ReceptionSubManagementResponseDTO {

    private Long receptionId;

    private Long receptionManagementTypeId;

    private String receptionManagementTypeName;

    private String managementUserIdsStr;

    private List<Long> managementUserIds;

    private List<SalonConfirmationHairdresserDetailDTO> hairdresserDetailList;

    public ReceptionSubManagementResponseDTO() {
    }

    public ReceptionSubManagementResponseDTO(Long receptionId, Long receptionManagementTypeId, String receptionManagementTypeName, String managementUserIdsStr, List<Long> managementUserIds, List<SalonConfirmationHairdresserDetailDTO> hairdresserDetailList) {
        this.receptionId = receptionId;
        this.receptionManagementTypeId = receptionManagementTypeId;
        this.receptionManagementTypeName = receptionManagementTypeName;
        this.managementUserIdsStr = managementUserIdsStr;
        this.managementUserIds = managementUserIds;
        this.hairdresserDetailList = hairdresserDetailList;
    }

    public Long getReceptionId() {
        return receptionId;
    }

    public void setReceptionId(Long receptionId) {
        this.receptionId = receptionId;
    }

    public Long getReceptionManagementTypeId() {
        return receptionManagementTypeId;
    }

    public void setReceptionManagementTypeId(Long receptionManagementTypeId) {
        this.receptionManagementTypeId = receptionManagementTypeId;
    }

    public String getReceptionManagementTypeName() {
        return receptionManagementTypeName;
    }

    public void setReceptionManagementTypeName(String receptionManagementTypeName) {
        this.receptionManagementTypeName = receptionManagementTypeName;
    }

    public String getManagementUserIdsStr() {
        return managementUserIdsStr;
    }

    public void setManagementUserIdsStr(String managementUserIdsStr) {
        this.managementUserIdsStr = managementUserIdsStr;
    }

    public List<Long> getManagementUserIds() {
        return managementUserIds;
    }

    public void setManagementUserIds(List<Long> managementUserIds) {
        this.managementUserIds = managementUserIds;
    }

    public List<SalonConfirmationHairdresserDetailDTO> getHairdresserDetailList() {
        return hairdresserDetailList;
    }

    public void setHairdresserDetailList(List<SalonConfirmationHairdresserDetailDTO> hairdresserDetailList) {
        this.hairdresserDetailList = hairdresserDetailList;
    }
}

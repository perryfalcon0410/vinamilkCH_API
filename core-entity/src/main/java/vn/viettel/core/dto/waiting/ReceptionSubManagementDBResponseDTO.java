package vn.viettel.core.dto.waiting;

public interface ReceptionSubManagementDBResponseDTO {
    Long getReceptionId();

    Long getReceptionManagementTypeId();

    String getReceptionManagementTypeName();

    String getManagementUserIdsStr();
}

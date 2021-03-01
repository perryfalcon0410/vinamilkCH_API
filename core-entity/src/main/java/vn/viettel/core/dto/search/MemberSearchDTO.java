package vn.viettel.core.dto.search;

import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.annotation.NotNull;


public class MemberSearchDTO {

    @NotNull(responseMessage = ResponseMessage.COMPANY_DOES_NOT_EXIST)
    private Long companyId;

    private SearchDTO search;

    public MemberSearchDTO() {

    }

    public MemberSearchDTO(Long companyId, SearchDTO search) {
        this.companyId = companyId;
        this.search = search;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public SearchDTO getSearch() {
        return search;
    }

    public void setSearch(SearchDTO search) {
        this.search = search;
    }
}

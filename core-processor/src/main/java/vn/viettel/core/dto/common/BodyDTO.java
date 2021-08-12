package vn.viettel.core.dto.common;

import java.util.List;

public class BodyDTO {
    private List<Long> ids;

    public BodyDTO() {
    }

    public BodyDTO(List<Long> ids) {
        this.ids = ids;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}

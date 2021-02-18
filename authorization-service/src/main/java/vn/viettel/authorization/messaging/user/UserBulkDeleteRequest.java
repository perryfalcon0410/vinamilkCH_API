package vn.viettel.authorization.messaging.user;

public class UserBulkDeleteRequest {
    private Long[] ids;

    public Long[] getIds() {
        return ids;
    }

    public void setIds(Long[] ids) {
        this.ids = ids;
    }
}

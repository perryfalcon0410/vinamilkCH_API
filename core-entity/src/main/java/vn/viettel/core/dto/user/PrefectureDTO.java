package vn.viettel.core.dto.user;

import vn.viettel.core.db.entity.Prefecture;

import java.util.List;

public class PrefectureDTO {
    List<Prefecture> Prefectures;

    public List<Prefecture> getPrefectures() {
        return Prefectures;
    }

    public void setPrefectures(List<Prefecture> prefectures) {
        Prefectures = prefectures;
    }
}

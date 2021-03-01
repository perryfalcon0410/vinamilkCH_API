package vn.viettel.core.dto.user;

import vn.viettel.core.db.entity.Prefecture;

import java.util.List;

public class MemberInformationResponseDTO {
    List<ChannelTypeResponseDTO> channels;

    List<Prefecture> prefectures;

    public List<ChannelTypeResponseDTO> getChannels() {
        return channels;
    }

    public void setChannels(List<ChannelTypeResponseDTO> channels) {
        this.channels = channels;
    }

    public List<Prefecture> getPrefectures() {
        return prefectures;
    }

    public void setPrefectures(List<Prefecture> prefectures) {
        this.prefectures = prefectures;
    }
}

package vn.viettel.core.dto.user;

import com.google.gson.annotations.Expose;

import java.util.List;

public class ChannelTypeResponseDTO {
    @Expose
    private Long id;
    @Expose
    private String name;
    private Long type;
    @Expose
    private List<ChannelTypeResponseDTO> typeIds;
    private Long parent;
    private Boolean isCheck;

    public ChannelTypeResponseDTO(Long id, String name, Long type, List<ChannelTypeResponseDTO> typeIds, Long parent, Boolean isCheck) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.typeIds = typeIds;
        this.parent = parent;
        this.isCheck = isCheck;
    }

    public ChannelTypeResponseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public List<ChannelTypeResponseDTO> getTypeIds() {
        return typeIds;
    }

    public void setTypeIds(List<ChannelTypeResponseDTO> typeIds) {
        this.typeIds = typeIds;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public Boolean getCheck() {
        return isCheck;
    }

    public void setCheck(Boolean check) {
        isCheck = check;
    }
}

package vn.viettel.core.dto;

import vn.viettel.core.dto.salon.SalonNameResponseDTO;

import java.util.List;

public class MemberSalonNameResponseDTO {
    private Long memberId;
    private List<SalonNameResponseDTO> salons;

    public MemberSalonNameResponseDTO() {
    }

    public MemberSalonNameResponseDTO(Long memberId, List<SalonNameResponseDTO> salons) {
        this.memberId = memberId;
        this.salons = salons;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public List<SalonNameResponseDTO> getSalons() {
        return salons;
    }

    public void setSalons(List<SalonNameResponseDTO> salons) {
        this.salons = salons;
    }
}

package vn.viettel.core.dto.company;

import vn.viettel.core.dto.salon.SalonNameResponseDTO;

import java.util.List;

public class CompanyListResponseDTO {
    private Long id;
    private String name;
    private List<SalonNameResponseDTO> salons;

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

    public List<SalonNameResponseDTO> getSalons() {
        return salons;
    }

    public void setSalons(List<SalonNameResponseDTO> salons) {
        this.salons = salons;
    }
}

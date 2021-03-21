package vn.viettel.customer.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTO {

    private String name;

    private String value;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LocationDTO) {
            if (this.name.equals(((LocationDTO) obj).name) && this.value.equals(((LocationDTO) obj).value))
                return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (this.name.hashCode() + this.value.hashCode());
    }

}

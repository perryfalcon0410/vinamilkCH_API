package vn.viettel.sale.xml;

import com.thoughtworks.xstream.annotations.XStreamImplicit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PODetail {
    @XStreamImplicit
    private List<Line> lstLine;
}

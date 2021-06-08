package vn.viettel.sale.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@XStreamAlias("PODetail")
public class PODetail {
    @XStreamImplicit(itemFieldName = "Line")
    private List<Line> lstLine;
}

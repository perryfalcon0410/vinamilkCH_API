package vn.viettel.sale.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@XStreamAlias("NewData")
public class NewData {
    @XStreamAlias("POHeader")
    private POHeader poHeader;
    @XStreamAlias("PODetail")
    private PODetail poDetail;
}

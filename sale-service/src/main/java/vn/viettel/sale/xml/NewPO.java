package vn.viettel.sale.xml;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@XStreamAlias("NewPO")
public class NewPO {
	
    @XStreamImplicit(itemFieldName = "POHeader")
    private List<POHeader> poHeader;
    
    @XStreamImplicit(itemFieldName = "PODetail")
    private List<PODetail> poDetailList;
}

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
@XStreamAlias("PO")
public class PO {
	
    @XStreamImplicit(itemFieldName = "NewPO")
    private List<NewPO> newPOList;
}

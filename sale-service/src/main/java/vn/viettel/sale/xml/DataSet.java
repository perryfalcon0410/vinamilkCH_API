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
@XStreamAlias("DataSet")
public class DataSet {
    @XStreamImplicit(itemFieldName = "NewDataSet")
    private List<NewDataSet> lstNewDataSet;
    private Integer stt;
}

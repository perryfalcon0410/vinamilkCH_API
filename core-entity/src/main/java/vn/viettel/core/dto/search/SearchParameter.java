package vn.viettel.core.dto.search;

import java.util.List;

public class SearchParameter {
    private String paramValueSearchColumn;

    private long paramValueOffset;

    private long paramValueLimit;

    StringBuilder paramValueSort;

    List<Param> nameParameters;

    public SearchParameter(String paramValueSearchColumn, long paramValueOffset, long paramValueLimit, StringBuilder paramValueSort, List<Param> nameParameters) {
        this.paramValueSearchColumn = paramValueSearchColumn;
        this.paramValueOffset = paramValueOffset;
        this.paramValueLimit = paramValueLimit;
        this.paramValueSort = paramValueSort;
        this.nameParameters = nameParameters;
    }

    public String getParamValueSearchColumn() {
        return paramValueSearchColumn;
    }

    public void setParamValueSearchColumn(String paramValueSearchColumn) {
        this.paramValueSearchColumn = paramValueSearchColumn;
    }

    public long getParamValueOffset() {
        return paramValueOffset;
    }

    public void setParamValueOffset(long paramValueOffset) {
        this.paramValueOffset = paramValueOffset;
    }

    public long getParamValueLimit() {
        return paramValueLimit;
    }

    public void setParamValueLimit(long paramValueLimit) {
        this.paramValueLimit = paramValueLimit;
    }

    public StringBuilder getParamValueSort() {
        return paramValueSort;
    }

    public void setParamValueSort(StringBuilder paramValueSort) {
        this.paramValueSort = paramValueSort;
    }

    public List<Param> getNameParameters() {
        return nameParameters;
    }

    public void setNameParameters(List<Param> nameParameters) {
        this.nameParameters = nameParameters;
    }
}

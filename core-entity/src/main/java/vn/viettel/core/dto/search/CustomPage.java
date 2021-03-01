package vn.viettel.core.dto.search;

import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class CustomPage<T> {
    private final List<T> content = new ArrayList<>();

    private final long trueTotal;

    private final Pageable pageable;

    public CustomPage() {
        this.pageable = null;
        this.trueTotal = 0;
    }

    public CustomPage(List<T> content, Pageable pageable) {
        this.content.addAll(content);
        this.pageable = pageable;
        this.trueTotal = !CollectionUtils.isEmpty(content) ? content.size() : 0;
    };

    public CustomPage(List<T> content, Pageable pageable, long total) {
        this.content.addAll(content);
        this.pageable = pageable;
        this.trueTotal = total;
    }

    public int getTotalPages() {
        return (int) Math.ceil((double) trueTotal / (double) pageable.getPageSize());
    }

    public long getTotalElements() {
        return trueTotal;
    }

    public boolean isLast() {
        return pageable.getPageNumber() + 1 >=  getTotalPages();
    }

    public boolean isFirst() {
        return pageable.getPageNumber()>=0;
    }

    public List<T> getContent() {
        return content;
    }

    public Pageable getPageable() {
        return pageable;
    }
}

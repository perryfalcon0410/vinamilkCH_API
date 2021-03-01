package vn.viettel.core.dto.search;

import vn.viettel.core.dto.PageDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SearchDTO {
    private String keyword;

    private Pageable pageable;

    private PageDTO page;

    private List<String> columns;

    public SearchDTO() {
    }

    public SearchDTO(String keyword, Pageable pageable, PageDTO page, List<String> columns) {
        this.keyword = keyword;
        this.pageable = pageable;
        this.columns = columns;
        this.setPage(page);
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public PageDTO getPage() {
        return page;
    }

    public void setPage(PageDTO page) {
        this.page = page;
        // parse into pageable
        // construct into sort object
        List<String> queries = null;
        if (!StringUtils.isEmpty(page.getSort())) {
            queries = Arrays.stream(page.getSort().split("&"))
                    .filter(item-> !StringUtils.isEmpty(item)).collect(Collectors.toList());
        }
        List<Sort.Order> orders = new ArrayList<>();
        if (!CollectionUtils.isEmpty(queries)) {
            queries.forEach(query -> {
                List<String> parts = Arrays.asList(query.split(","));
                Sort.Direction direction = parts.size() == 0 ?
                        null : Sort.Direction.fromOptionalString(parts.get(parts.size() - 1)).orElse(null);
                if (direction != null) {
                    orders.add(new Sort.Order(direction, parts.get(0)));
                }
            });
        }
        Sort sortAlgorithm =
                !CollectionUtils.isEmpty(orders) ? Sort.by(orders) : Sort.unsorted();

        // setting page request object
        this.pageable = PageRequest.of(page.getNumber(), page.getSize(), sortAlgorithm);
    }

    public Pageable getPageable() {
        return pageable;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }
}

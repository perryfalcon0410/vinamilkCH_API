package vn.viettel.promotion.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestParam;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VoucherFilter {
    private String keyWord;
    private Long customerTypeId;
    private Long shopId;

}

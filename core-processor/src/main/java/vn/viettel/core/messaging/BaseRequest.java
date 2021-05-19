package vn.viettel.core.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseRequest {
    private Long id;
//
//    private Timestamp createdAt;
//
//    private Timestamp updatedAt;
}

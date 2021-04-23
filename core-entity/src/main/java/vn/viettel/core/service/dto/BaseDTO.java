package vn.viettel.core.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseDTO {

    private Long id;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private Timestamp deletedAt;



    public Timestamp getCreatedAt() {
        ZoneOffset zoneOffset = ZoneId.systemDefault().getRules().getOffset(Instant.now());
        if(createdAt != null)
            return new Timestamp(createdAt.getTime() + (1000 * zoneOffset.getTotalSeconds()));
        return null;
    }

    public Timestamp getUpdatedAt() {
        ZoneOffset zoneOffset = ZoneId.systemDefault().getRules().getOffset(Instant.now());
        if(updatedAt != null)
            return new Timestamp(updatedAt.getTime() + (1000 * zoneOffset.getTotalSeconds()));
        return null;
    }

    public Timestamp getDeletedAt() {
        ZoneOffset zoneOffset = ZoneId.systemDefault().getRules().getOffset(Instant.now());
        if(deletedAt != null)
            return new Timestamp(deletedAt.getTime() + (1000 * zoneOffset.getTotalSeconds()));
        return null;
    }
}

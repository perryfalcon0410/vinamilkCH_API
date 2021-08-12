package vn.viettel.authorization.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "token")
@EntityListeners(AuditingEntityListener.class)
public class Token {
    @Id
    @Column(name = "token_id", nullable = false)
    private String tokenId;

    @Column(name = "token", nullable = false)
    private String token;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

package vn.viettel.sale.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "MEDIA_ITEM")
public class MediaItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "URL")
    private String url;
    @Column(name = "THUMB_URL")
    private String thumbUrl;
    @Column(name = "TITLE")
    private String title;
    @Column(name = "MEDIA_TYPE")
    private String mediaType;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "FILE_SIZE")
    private Integer fileSize;
    @Column(name = "WIDTH")
    private Integer width;
    @Column(name = "HEIGHT")
    private Integer height;
    @Column(name = "OBJECT_TYPE")
    private Integer objectType;
    @Column(name = "STATUS")
    private Integer status;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "OBJECT_ID")
    private Long objectId;

    @Column(name = "CREATE_USER")
    private String createUser;
    @Column(name = "UPDATE_USER")
    private String updateUser;

}

package com.craft.demo.url.operations.entity;

import com.craft.demo.commons.entity.base.BaseEntity;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "url_info_details",
        uniqueConstraints = @UniqueConstraint(columnNames = {"shortUrl", "longUrl"}),
        indexes = {
                @Index(name = "shortUrl_idx", columnList = "shortUrl")
        }
)
@Builder
public class UrlInfoDetails extends BaseEntity {

    @NotNull
    @Column(unique = true)
    String shortUrl;

    @NotNull
    String longUrl;

    @Builder.Default
    @Min(value = 1, message = "Expiration Time can not be less than 1 minute")
    Integer expirationTime = 5;

    @Builder.Default
    Integer visitCount = 0;

    @Builder.Default
    private LocalDateTime urlCreationTime = LocalDateTime.now();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UrlInfoDetails that = (UrlInfoDetails) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

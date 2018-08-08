package org.benetech.mathshare.model.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.sql.Timestamp;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class ProblemSet extends AbstractEntity {

    @Column(insertable = false)
    private Long editCode;

    @CreationTimestamp
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Timestamp dateCreated;

    @UpdateTimestamp
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Timestamp dateModified;
}

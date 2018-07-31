package org.benetech.mathshare.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class ProblemSet extends AbstractEntity {

    @NotNull
    @NonNull
    private String editCode;

    @CreationTimestamp
    private Timestamp dateCreated;

    @UpdateTimestamp
    private Timestamp dateModified;
}

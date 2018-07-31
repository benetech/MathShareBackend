package org.benetech.mathshare.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class ProblemSolution extends AbstractEntity {

    @NotNull
    @NonNull
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Problem problem;

    @Lob
    @NotNull
    @NonNull
    private String editCode;

    @CreationTimestamp
    private Timestamp dateCreated;

    @UpdateTimestamp
    private Timestamp dateModified;
}

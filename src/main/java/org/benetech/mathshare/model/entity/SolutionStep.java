package org.benetech.mathshare.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class SolutionStep extends AbstractEntity {

    @Lob
    private String explanation;

    @Lob
    @NotNull
    @NonNull
    private String stepValue;

    @NotNull
    @NonNull
    @ManyToOne(cascade = CascadeType.PERSIST)
    private ProblemSolution solution;

    @NotNull
    @NonNull
    private Boolean deleted;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "replaced_by")
    private SolutionStep replacedBy;

    @UpdateTimestamp
    private Timestamp dateModified;
}

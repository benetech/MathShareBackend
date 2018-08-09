package org.benetech.mathshare.model.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
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

    @OneToOne(mappedBy = "step", cascade = CascadeType.ALL)
    private Scratchpad scratchpad;

    @Lob
    @NotNull
    @NonNull
    private String stepValue;

    @NotNull
    @NonNull
    @ManyToOne
    private SolutionRevision solutionRevision;

    @NotNull
    @NonNull
    private Boolean deleted;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "replaced_by")
    private SolutionStep replacedBy;

    @UpdateTimestamp
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Timestamp dateModified;
}

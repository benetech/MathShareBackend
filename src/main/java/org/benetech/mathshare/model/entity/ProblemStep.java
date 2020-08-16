package org.benetech.mathshare.model.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
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
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProblemStep extends AbstractEntity {

    @Lob
    @Type(type = "text")
    private String explanation;

    @OneToOne(cascade = CascadeType.ALL)
    private Scratchpad scratchpad;

    @Lob
    @NotNull
    @NonNull
    @Type(type = "text")
    private String stepValue;

    @NotNull
    @NonNull
    @ManyToOne
    private Problem problem;

    @NotNull
    @NonNull
    private Boolean deleted;

    @NotNull
    @NonNull
    private Boolean inProgress;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "replaced_by")
    private ProblemStep replacedBy;

    @Lob
    @Type(type = "text")
    private String cleanup;

    @UpdateTimestamp
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Timestamp dateModified;
}

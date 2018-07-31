package org.benetech.mathshare.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class ProblemSetRevision extends AbstractEntity {

    @NotNull
    @NonNull
    @ManyToOne(cascade = CascadeType.PERSIST)
    private ProblemSet problemSet;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "replaced_by")
    private ProblemSetRevision replacedBy;

    @NotNull
    @NonNull
    private String shareCode;

    @CreationTimestamp
    private Timestamp dateCreated;
}

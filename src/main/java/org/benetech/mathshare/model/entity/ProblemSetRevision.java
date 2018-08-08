package org.benetech.mathshare.model.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class ProblemSetRevision extends AbstractEntity {

    @NotNull
    @NonNull
    @ManyToOne(cascade = CascadeType.PERSIST)
    private ProblemSet problemSet;

    @OneToMany(mappedBy = "problemSetRevision", cascade = CascadeType.PERSIST)
    private List<Problem> problems = new ArrayList<>();

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "replaced_by")
    private ProblemSetRevision replacedBy;

    @Column(insertable = false)
    private Long shareCode;

    @CreationTimestamp
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Timestamp dateCreated;

    public ProblemSetRevision() {

    }

    public ProblemSetRevision(@NotNull ProblemSet problemSet) {
        this.problemSet = problemSet;
    }
}

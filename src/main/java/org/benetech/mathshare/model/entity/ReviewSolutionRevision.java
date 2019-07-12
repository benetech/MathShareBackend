package org.benetech.mathshare.model.entity;

import lombok.NonNull;
import org.hibernate.annotations.CreationTimestamp;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "review_solution_revision")
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ReviewSolutionRevision extends AbstractEntity {

    @Column(insertable = true)
    private Long reviewCode;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "solution_revision_id")
    private SolutionRevision solutionRevision;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "problem_set_revision_solution_id")
    private ProblemSetRevisionSolution problemSetRevisionSolution;

    @CreationTimestamp
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Timestamp dateCreated;

    @NonNull
    private Boolean inactive = false;

    public ReviewSolutionRevision(@NotNull SolutionRevision solutionRevision, Long reviewCode,
                                  ProblemSetRevisionSolution problemSetRevisionSolution) {
        this.solutionRevision = solutionRevision;
        this.reviewCode = reviewCode;
        this.problemSetRevisionSolution = problemSetRevisionSolution;
    }
}

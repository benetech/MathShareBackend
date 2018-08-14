package org.benetech.mathshare.model.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SolutionRevision extends AbstractEntity {

    @Column(insertable = false)
    private Long shareCode;

    @OneToMany(mappedBy = "solutionRevision", cascade = CascadeType.ALL)
    private List<SolutionStep> steps = new ArrayList<>();

    @NotNull
    @NonNull
    @ManyToOne
    private ProblemSolution problemSolution;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "replaced_by")
    private SolutionRevision replacedBy;

    @CreationTimestamp
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Timestamp dateCreated;

    public SolutionRevision(@NotNull ProblemSolution problemSolution, List<SolutionStep> steps) {
        this.problemSolution = problemSolution;
        this.steps = steps;
    }
}

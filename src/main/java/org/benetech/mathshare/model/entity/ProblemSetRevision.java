package org.benetech.mathshare.model.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProblemSetRevision extends AbstractEntity {

    @NotNull
    @NonNull
    @ManyToOne
    private ProblemSet problemSet;

    @OneToMany(mappedBy = "problemSetRevision", cascade = CascadeType.ALL)
    private List<Problem> problems = new ArrayList<>();

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "replaced_by")
    private ProblemSetRevision replacedBy;

    @Column(insertable = false)
    private Long shareCode;

    @Column
    private boolean isExample;

    @Column
    private boolean optionalExplanations;

    @Column
    private boolean hideSteps;

    @Column
    private String title;

    @CreationTimestamp
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Timestamp dateCreated;

    public ProblemSetRevision(@NotNull ProblemSet problemSet, @NotNull String title) {
        this.problemSet = problemSet;
        this.title = title;
    }

    public ProblemSetRevision(@NotNull ProblemSet problemSet, @NotNull String title,
                              boolean optionalExplanations, boolean hideSteps) {
        this(problemSet, title);
        this.optionalExplanations = optionalExplanations;
        this.hideSteps = hideSteps;
    }
}

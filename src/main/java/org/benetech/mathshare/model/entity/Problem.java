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
import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Problem extends AbstractEntity {

    @NotNull
    @NonNull
    @ManyToOne
    private ProblemSetRevision problemSetRevision;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "replaced_by")
    private Problem replacedBy;

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL)
    @OrderBy("id ASC")
    private List<ProblemStep> steps = new ArrayList<>();

    @Lob
    @NotNull
    @NonNull
    @Type(type = "text")
    private String problemText;

    @NotNull
    @NonNull
    private String title;

    private Integer position;

    @OneToOne(cascade = CascadeType.ALL)
    private Scratchpad scratchpad;

    @CreationTimestamp
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Timestamp dateCreated;

    public Problem(String problemText, String title, ProblemSetRevision problemSetRevision) {
        this.problemSetRevision = problemSetRevision;
        this.title = title;
        this.problemText = problemText;
    }

    public Problem(String problemText, String title, ProblemSetRevision problemSetRevision, Integer position) {
        this.problemSetRevision = problemSetRevision;
        this.title = title;
        this.problemText = problemText;
        this.position = position;
    }

    public Problem(String problemText, String title, ProblemSetRevision problemSetRevision, List<ProblemStep> steps) {
        this(problemText, title, problemSetRevision);
        this.steps = steps;
    }
}

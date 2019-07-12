package org.benetech.mathshare.model.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "problem_set_revision_solution")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProblemSetRevisionSolution extends AbstractEntity {

    @ManyToOne
    private ProblemSetRevision problemSetRevision;

    @Column(insertable = true)
    private Long editCode;

    @CreationTimestamp
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Timestamp dateCreated;

    public ProblemSetRevisionSolution(ProblemSetRevision problemSetRevision, Long editCode) {
        this.problemSetRevision = problemSetRevision;
        this.editCode = editCode;
    }
}

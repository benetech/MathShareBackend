package org.benetech.mathshare.model.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @Column
    private String userId;

    @Getter(AccessLevel.PUBLIC)
    private String archiveMode;

    private String archivedBy;

    @Column
    private String source;

    @Column
    private String metadata;

    @Column(insertable = true)
    private Long editCode;

    @CreationTimestamp
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Timestamp dateCreated;

    @UpdateTimestamp
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Timestamp dateModified;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.PRIVATE)
    private Timestamp archivedAt;

    public ProblemSetRevisionSolution(ProblemSetRevision problemSetRevision, Long editCode, String userId) {
        this.problemSetRevision = problemSetRevision;
        this.editCode = editCode;
        this.userId = userId;
    }

    public ProblemSetRevisionSolution(ProblemSetRevision problemSetRevision, Long editCode, String userId, String metadata) {
        this(problemSetRevision, editCode, userId);
        this.metadata = metadata;
    }

    public void setArchiveMode(String archiveMode) {
        if (!"deleted".equals(archiveMode) && !"archived".equals(archiveMode) && archiveMode != null) {
            return;
        }
        this.archiveMode = archiveMode;
        if (archiveMode != null) {
            this.setArchivedAt(new Timestamp(System.currentTimeMillis()));
        }
    }
}

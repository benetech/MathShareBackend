package org.benetech.mathshare.model.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.benetech.mathshare.converters.StringListConverter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JoinFormula;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class ProblemSet extends AbstractEntity {

    @Column(insertable = false)
    private Long editCode;

    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinFormula("(SELECT r.id FROM problem_set_revision r WHERE r.problem_set_id = id ORDER BY r.id DESC LIMIT 1)")
    private ProblemSetRevision latestRevision;

    @CreationTimestamp
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Timestamp dateCreated;

    @UpdateTimestamp
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Timestamp dateModified;

    @Convert(converter = StringListConverter.class)
    private List<String> palettes = new ArrayList<>();
}

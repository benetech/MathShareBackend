package org.benetech.mathshare.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Scratchpad extends AbstractEntity {

    @NotNull
    @NonNull
    @OneToOne(cascade = CascadeType.PERSIST)
    private SolutionStep step;

    @Lob
    private String content;

    @UpdateTimestamp
    private Timestamp dateModified;
}

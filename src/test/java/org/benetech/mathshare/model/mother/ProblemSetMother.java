package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.model.entity.ProblemSet;

public abstract class ProblemSetMother {

    public static ProblemSet validInstance() {
        return new ProblemSet();
    }
}

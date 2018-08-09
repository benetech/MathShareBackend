package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.model.entity.ProblemSet;

public abstract class ProblemSetMother {

    public static final Long EDIT_CODE = 49L;

    public static ProblemSet validInstance() {
        return new ProblemSet();
    }

    public static ProblemSet withEditCode(long code) {
        ProblemSet result = validInstance();
        result.setEditCode(code);
        return result;
    }

    public static ProblemSet mockInstance() {
        return withEditCode(EDIT_CODE);
    }
}

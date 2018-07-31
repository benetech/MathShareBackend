package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.model.entity.ProblemSet;

public class ProblemSetMother {

    public static final String DEFAULT_EDIT_CODE = "BG54ad65gf";

    public static ProblemSet validInstance() {
        return new ProblemSet(DEFAULT_EDIT_CODE);
    }
}

package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.model.entity.ProblemSet;

public abstract class ProblemSetUtils {

    public static final String DEFAULT_EDIT_CODE = "BG54ad65gf";

    public static ProblemSet createValidInstance() {
        return new ProblemSet(DEFAULT_EDIT_CODE);
    }
}

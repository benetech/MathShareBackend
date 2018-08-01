package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.model.entity.Scratchpad;
import org.benetech.mathshare.model.entity.SolutionStep;

public abstract class ScratchpadUtils {

    public static Scratchpad createValidInstance() {
        SolutionStep step = SolutionStepUtils.createValidInstance();
        return new Scratchpad(step);
    }
}

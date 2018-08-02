package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.model.entity.Scratchpad;
import org.benetech.mathshare.model.entity.SolutionStep;

public abstract class ScratchpadMother {

    public static Scratchpad createValidInstance() {
        SolutionStep step = SolutionStepMother.createValidInstance();
        return new Scratchpad(step);
    }
}

package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.model.entity.Scratchpad;
import org.benetech.mathshare.model.entity.SolutionStep;

public abstract class ScratchpadMother {

    public static Scratchpad validInstance() {
        SolutionStep step = SolutionStepMother.validInstance();
        return new Scratchpad(step);
    }
}

package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.model.entity.Scratchpad;

public abstract class ScratchpadMother {

    public static final String CONTENT = "content";

    public static Scratchpad validInstance() {
        return new Scratchpad(CONTENT);
    }
}

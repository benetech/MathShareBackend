package org.benetech.mathshare.model.mother;

import org.benetech.mathshare.model.entity.SolutionRevision;
import org.benetech.mathshare.model.entity.SolutionStep;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class SolutionStepMother {

    public static final String DEFAULT_STEP_VALUE = "step value";
    public static final Boolean DEFAULT_DELETED_VALUE = false;

    public static SolutionStep validInstance(SolutionRevision revision) {
        return new SolutionStep(DEFAULT_STEP_VALUE, revision, DEFAULT_DELETED_VALUE);
    }

    public static List<SolutionStep> createValidStepsList(SolutionRevision revision, int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> validInstance(revision))
                .collect(Collectors.toList());
    }
}

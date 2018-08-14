package org.benetech.mathshare.mappers;

import org.benetech.mathshare.converters.UrlCodeConverter;
import org.benetech.mathshare.model.dto.SolutionDTO;
import org.benetech.mathshare.model.dto.SolutionStepDTO;
import org.benetech.mathshare.model.entity.SolutionRevision;
import org.benetech.mathshare.model.entity.SolutionStep;
import org.benetech.mathshare.model.mother.ProblemSolutionMother;
import org.benetech.mathshare.model.mother.SolutionRevisionMother;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class SolutionMapperTest {

    private static final Long SHARE_CODE = 57L;

    private static final Long EDIT_CODE = 42L;

    @Test
    public void shouldMapSolutionStepsToDTO() {
        int stepsSize = 3;
        SolutionRevision revision = SolutionRevisionMother.withSteps(ProblemSolutionMother.mockInstance(), stepsSize);

        SolutionDTO mapped = SolutionMapper.INSTANCE.toSolutionDTO(revision);

        Assert.assertEquals(stepsSize, mapped.getSteps().size());
    }

    @Test
    public void shouldMapSolutionStepFromDTO() {
        SolutionStepDTO solutionStepDTO = new SolutionStepDTO();
        solutionStepDTO.setExplanation("explanation");
        solutionStepDTO.setStepValue("Step value");

        SolutionStep mapped = SolutionMapper.INSTANCE.fromDto(solutionStepDTO);

        Assert.assertEquals(solutionStepDTO.getStepValue(), mapped.getStepValue());
        Assert.assertEquals(solutionStepDTO.getExplanation(), mapped.getExplanation());
        Assert.assertEquals(false, mapped.getDeleted());
    }

    @Test
    public void shouldMapSolutionCodes() {
        SolutionRevision revision = SolutionRevisionMother.withShareCode(ProblemSolutionMother.mockInstance(), SHARE_CODE);
        revision.getProblemSolution().setEditCode(EDIT_CODE);

        SolutionDTO mapped = SolutionMapper.INSTANCE.toSolutionDTO(revision);

        Assert.assertEquals(UrlCodeConverter.toUrlCode(EDIT_CODE), mapped.getEditCode());
        Assert.assertEquals(UrlCodeConverter.toUrlCode(SHARE_CODE), mapped.getShareCode());
    }

    @Test
    public void shouldMapSolutionProblem() {
        SolutionRevision revision = SolutionRevisionMother.validInstance(ProblemSolutionMother.mockInstance());

        SolutionDTO mapped = SolutionMapper.INSTANCE.toSolutionDTO(revision);

        Assert.assertNotNull(mapped.getProblem());
    }
}

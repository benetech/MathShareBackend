package org.benetech.mathshare.mappers;

import org.benetech.mathshare.converters.UrlCodeConverter;
import org.benetech.mathshare.model.dto.SolutionDTO;
import org.benetech.mathshare.model.entity.SolutionRevision;
import org.benetech.mathshare.model.mother.SolutionRevisionMother;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class SolutionMapperTest {

    @Test
    public void shouldMapSolutionSteps() {
        int stepsSize = 3;
        SolutionRevision revision = SolutionRevisionMother.withSteps(stepsSize);

        SolutionDTO mapped = SolutionMapper.INSTANCE.toSolutionDTO(revision);

        Assert.assertEquals(stepsSize, mapped.getSteps().size());
    }

    @Test
    public void shouldMapSolutionCodes() {
        long editCode = 76L;
        long shareCode = 32L;
        SolutionRevision revision = SolutionRevisionMother.withShareCode(shareCode);
        revision.getProblemSolution().setEditCode(editCode);

        SolutionDTO mapped = SolutionMapper.INSTANCE.toSolutionDTO(revision);

        Assert.assertEquals(UrlCodeConverter.toUrlCode(editCode), mapped.getEditCode());
        Assert.assertEquals(UrlCodeConverter.toUrlCode(shareCode), mapped.getShareCode());
    }

    @Test
    public void shouldMapSolutionProblem() {
        SolutionRevision revision = SolutionRevisionMother.validInstance();

        SolutionDTO mapped = SolutionMapper.INSTANCE.toSolutionDTO(revision);

        Assert.assertNotNull(mapped.getProblem());
    }
}

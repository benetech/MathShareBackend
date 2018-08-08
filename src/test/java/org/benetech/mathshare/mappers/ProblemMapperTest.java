package org.benetech.mathshare.mappers;

import org.benetech.mathshare.converters.UrlCodeConverter;
import org.benetech.mathshare.model.dto.ProblemSetDTO;
import org.benetech.mathshare.model.entity.ProblemSetRevision;
import org.benetech.mathshare.model.mother.ProblemSetRevisionMother;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class ProblemMapperTest {

    @Test
    public void shouldMapProblemSetCodes() {
        long editCode = 34L;
        long shareCode = 16L;
        ProblemSetRevision revision = ProblemSetRevisionMother.withShareCode(shareCode);
        revision.getProblemSet().setEditCode(editCode);

        ProblemSetDTO mapped = ProblemMapper.INSTANCE.toProblemSetDTO(revision);

        Assert.assertEquals(UrlCodeConverter.toUrlCode(editCode), mapped.getEditCode());
        Assert.assertEquals(UrlCodeConverter.toUrlCode(shareCode), mapped.getShareCode());
    }

    @Test
    public void shouldMapProblemSetRevisionProblems() {
        int problemsSize = 3;
        ProblemSetRevision revision = ProblemSetRevisionMother.withProblems(problemsSize);

        ProblemSetDTO mapped = ProblemMapper.INSTANCE.toProblemSetDTO(revision);

        Assert.assertEquals(problemsSize, mapped.getProblems().size());
    }
}

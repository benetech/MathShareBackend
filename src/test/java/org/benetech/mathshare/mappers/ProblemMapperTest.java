package org.benetech.mathshare.mappers;

import org.benetech.mathshare.converters.UrlCodeConverter;
import org.benetech.mathshare.model.dto.ProblemSetDTO;
import org.benetech.mathshare.model.entity.ProblemSet;
import org.benetech.mathshare.model.entity.ProblemSetRevision;
import org.benetech.mathshare.model.mother.ProblemSetMother;
import org.benetech.mathshare.model.mother.ProblemSetRevisionMother;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class ProblemMapperTest {

    private static final Long SHARE_CODE = 68L;

    private static final Long EDIT_CODE = 22L;

    @Test
    public void shouldMapProblemSetCodes() {
        ProblemSet problemSet = ProblemSetMother.mockInstance();
        ProblemSetRevision revision = ProblemSetRevisionMother.withShareCode(problemSet, SHARE_CODE);
        revision.getProblemSet().setEditCode(EDIT_CODE);

        ProblemSetDTO mapped = ProblemMapper.INSTANCE.toProblemSetDTO(revision);

        Assert.assertEquals(UrlCodeConverter.toUrlCode(EDIT_CODE), mapped.getEditCode());
        Assert.assertEquals(UrlCodeConverter.toUrlCode(SHARE_CODE), mapped.getShareCode());
    }

    @Test
    public void shouldMapProblemSetRevisionProblems() {
        int problemsSize = 3;
        ProblemSet problemSet = ProblemSetMother.mockInstance();
        ProblemSetRevision revision = ProblemSetRevisionMother.withProblems(problemSet, problemsSize);

        ProblemSetDTO mapped = ProblemMapper.INSTANCE.toProblemSetDTO(revision);

        Assert.assertEquals(problemsSize, mapped.getProblems().size());
    }
}

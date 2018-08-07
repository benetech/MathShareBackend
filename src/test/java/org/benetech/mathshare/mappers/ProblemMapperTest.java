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
    public void shouldMapProblemSetRevisionToProblemSetDTO() {
        long editCode = 0L;
        ProblemSetRevision revision = ProblemSetRevisionMother.withShareCode();
        revision.getProblemSet().setEditCode(editCode);
        ProblemSetDTO y = ProblemMapper.INSTANCE.toProblemSetDTO(revision);
        Assert.assertEquals(UrlCodeConverter.toUrlCode(editCode), y.getEditCode());
        Assert.assertEquals(ProblemSetRevisionMother.VALID_CODE, y.getShareCode());
    }
}

package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.entity.ProblemSet;
import org.benetech.mathshare.model.entity.ProblemSetRevision;
import org.benetech.mathshare.model.mother.ProblemSetRevisionUtils;
import org.benetech.mathshare.model.mother.ProblemSetUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.HSQL, replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProblemSetRevisionRepositoryTest {

    @Autowired
    private ProblemSetRevisionRepository problemSetRevisionRepository;
    @Autowired
    private ProblemSetRepository problemSetRepository;

    @Test
    public void shouldSaveProblemSetRevision() {
        problemSetRevisionRepository.save(ProblemSetRevisionUtils.createValidInstance());
        ProblemSetRevision problemSetRevisionFromDB = problemSetRevisionRepository.findAll().get(0);
        Assert.assertEquals(ProblemSetRevisionUtils.DEFAULT_SHARE_CODE, problemSetRevisionFromDB.getShareCode());
    }

    @Test
    public void shouldFindProblemSetRevisionByShareCode() {
        problemSetRevisionRepository.save(ProblemSetRevisionUtils.createValidInstance());
        ProblemSetRevision problemSetRevision = problemSetRevisionRepository.findByShareCode(ProblemSetRevisionUtils.DEFAULT_SHARE_CODE);
        Assert.assertEquals(ProblemSetRevisionUtils.DEFAULT_SHARE_CODE, problemSetRevision.getShareCode());
        Assert.assertEquals(ProblemSetUtils.DEFAULT_EDIT_CODE, problemSetRevision.getProblemSet().getEditCode());
    }

    @Test
    public void shouldFindByProblemSetAndReplacedBy() {
        problemSetRevisionRepository.save(ProblemSetRevisionUtils.createValidInstance());
        ProblemSet problemSet = problemSetRepository.findAll().get(0);
        ProblemSetRevision problemSetRevision = problemSetRevisionRepository.findByProblemSetAndReplacedBy(problemSet, null);
        Assert.assertEquals(ProblemSetRevisionUtils.DEFAULT_SHARE_CODE, problemSetRevision.getShareCode());

        ProblemSetRevision newRevision = problemSetRevisionRepository.save(ProblemSetRevisionUtils.createNewRevisionOfValidInstance(problemSet));
        Assert.assertEquals(ProblemSetRevisionUtils.DEFAULT_SHARE_CODE_NEW_REV, newRevision.getShareCode());
    }
}

package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.entity.ProblemSolution;
import org.benetech.mathshare.model.entity.SolutionRevision;
import org.benetech.mathshare.model.mother.ProblemSolutionUtils;
import org.benetech.mathshare.model.mother.SolutionRevisionUtils;
import org.junit.Assert;
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
public class SolutionRevisionRepositoryTest {

    @Autowired
    private SolutionRevisionRepository solutionRevisionRepository;

    @Autowired
    private ProblemSolutionRepository problemSolutionRepository;

    @Test
    public void shouldSaveSolutionRevision() {
        solutionRevisionRepository.save(SolutionRevisionUtils.createValidInstance());
        SolutionRevision solutionRevisionFromDB = solutionRevisionRepository.findAll().get(0);
        Assert.assertEquals(SolutionRevisionUtils.DEFAULT_SHARE_CODE, solutionRevisionFromDB.getShareCode());
    }

    @Test
    public void shouldFindSolutionRevisionByShareCode() {
        solutionRevisionRepository.save(SolutionRevisionUtils.createValidInstance());
        SolutionRevision solutionRevision = solutionRevisionRepository.findOneByShareCode(SolutionRevisionUtils.DEFAULT_SHARE_CODE);
        Assert.assertEquals(SolutionRevisionUtils.DEFAULT_SHARE_CODE, solutionRevision.getShareCode());
        Assert.assertEquals(ProblemSolutionUtils.DEFAULT_EDIT_CODE, solutionRevision.getProblemSolution().getEditCode());
    }

    @Test
    public void shouldFindByProblemSolutionAndReplacedBy() {
        solutionRevisionRepository.save(SolutionRevisionUtils.createValidInstance());
        ProblemSolution problemSolution = problemSolutionRepository.findAll().get(0);
        SolutionRevision solutionRevision = solutionRevisionRepository.findAllByProblemSolutionAndReplacedBy(problemSolution, null);
        Assert.assertEquals(SolutionRevisionUtils.DEFAULT_SHARE_CODE, solutionRevision.getShareCode());

        SolutionRevision newRevision = solutionRevisionRepository.save(SolutionRevisionUtils.createNewRevisionOfValidInstance(problemSolution));
        Assert.assertEquals(SolutionRevisionUtils.DEFAULT_SHARE_CODE_NEW_REV, newRevision.getShareCode());
    }
}

package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.entity.ProblemSolution;
import org.benetech.mathshare.model.entity.SolutionRevision;
import org.benetech.mathshare.model.mother.SolutionRevisionMother;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.HSQL, replace = AutoConfigureTestDatabase.Replace.NONE)
public class SolutionRevisionRepositoryTest {

    @Autowired
    private SolutionRevisionRepository solutionRevisionRepository;

    @Autowired
    private ProblemSolutionRepository problemSolutionRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void shouldSaveSolutionRevision() {
        int dbSizeBeforeSave = solutionRevisionRepository.findAll().size();
        solutionRevisionRepository.saveAndFlush(SolutionRevisionMother.createValidInstance());
        int dbSizeAfterSave = solutionRevisionRepository.findAll().size();
        Assert.assertEquals(dbSizeBeforeSave + 1, dbSizeAfterSave);
    }

    @Test
    public void shouldFindSolutionRevisionByShareCode() {
        SolutionRevision saved = solutionRevisionRepository.save(SolutionRevisionMother.createValidInstance());
        em.refresh(saved);
        SolutionRevision solutionRevision = solutionRevisionRepository.findOneByShareCode(saved.getShareCode());
        Assert.assertNotNull(solutionRevision.getShareCode());
        Assert.assertNotNull(solutionRevision.getProblemSolution());
    }

    @Test
    public void shouldFindByProblemSolutionAndReplacedBy() {
        SolutionRevision saved = solutionRevisionRepository.save(SolutionRevisionMother.createValidInstance());
        ProblemSolution problemSolution = problemSolutionRepository.findAll().get(0);
        SolutionRevision solutionRevision = solutionRevisionRepository.findAllByProblemSolutionAndReplacedBy(problemSolution, null);
        Assert.assertEquals(saved.getShareCode(), solutionRevision.getShareCode());
        SolutionRevision newRevision = solutionRevisionRepository.save(SolutionRevisionMother.createNewRevisionOfValidInstance(problemSolution));
        Assert.assertEquals(saved.getShareCode(), newRevision.getShareCode());
    }
}

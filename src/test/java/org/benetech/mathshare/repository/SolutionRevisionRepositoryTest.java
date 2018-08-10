package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.entity.Problem;
import org.benetech.mathshare.model.entity.ProblemSet;
import org.benetech.mathshare.model.entity.ProblemSetRevision;
import org.benetech.mathshare.model.entity.ProblemSolution;
import org.benetech.mathshare.model.entity.SolutionRevision;
import org.benetech.mathshare.model.mother.ProblemMother;
import org.benetech.mathshare.model.mother.ProblemSetMother;
import org.benetech.mathshare.model.mother.ProblemSetRevisionMother;
import org.benetech.mathshare.model.mother.ProblemSolutionMother;
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

    @Autowired
    private ProblemSetRepository problemSetRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private ProblemSetRevisionRepository problemSetRevisionRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void shouldSaveSolutionRevision() {
        int dbSizeBeforeSave = solutionRevisionRepository.findAll().size();
        ProblemSet problemSet = problemSetRepository.save(ProblemSetMother.validInstance());
        ProblemSetRevision revision = problemSetRevisionRepository.save(ProblemSetRevisionMother.validInstance(problemSet));
        Problem problem = problemRepository.save(ProblemMother.validInstance(revision));
        ProblemSolution problemSolution = problemSolutionRepository.save(ProblemSolutionMother.validInstance(problem));
        solutionRevisionRepository.saveAndFlush(SolutionRevisionMother.validInstance(problemSolution));
        int dbSizeAfterSave = solutionRevisionRepository.findAll().size();
        Assert.assertEquals(dbSizeBeforeSave + 1, dbSizeAfterSave);
    }

    @Test
    public void shouldFindSolutionRevisionByShareCode() {
        ProblemSet problemSet = problemSetRepository.save(ProblemSetMother.validInstance());
        ProblemSetRevision revision = problemSetRevisionRepository.save(ProblemSetRevisionMother.validInstance(problemSet));
        Problem problem = problemRepository.save(ProblemMother.validInstance(revision));
        ProblemSolution problemSolution = problemSolutionRepository.save(ProblemSolutionMother.validInstance(problem));
        SolutionRevision saved = solutionRevisionRepository.save(SolutionRevisionMother.validInstance(problemSolution));
        em.refresh(saved);
        SolutionRevision solutionRevision = solutionRevisionRepository.findOneByShareCode(saved.getShareCode());
        Assert.assertNotNull(solutionRevision.getShareCode());
        Assert.assertNotNull(solutionRevision.getProblemSolution());
    }

    @Test
    public void shouldFindByProblemSolutionAndReplacedBy() {
        ProblemSet problemSet = problemSetRepository.save(ProblemSetMother.validInstance());
        ProblemSetRevision revision = problemSetRevisionRepository.save(ProblemSetRevisionMother.validInstance(problemSet));
        Problem problem = problemRepository.save(ProblemMother.validInstance(revision));
        ProblemSolution problemSolution = problemSolutionRepository.save(ProblemSolutionMother.validInstance(problem));
        SolutionRevision saved = solutionRevisionRepository.save(SolutionRevisionMother.validInstance(problemSolution));
        ProblemSolution problemSolutionFromDB = problemSolutionRepository.findAll().get(0);
        SolutionRevision solutionRevision = solutionRevisionRepository.findOneByProblemSolutionAndReplacedBy(problemSolutionFromDB, null);
        Assert.assertEquals(saved.getShareCode(), solutionRevision.getShareCode());
        SolutionRevision newRevision = solutionRevisionRepository.save(SolutionRevisionMother.revisionOf(problemSolutionFromDB));
        Assert.assertEquals(saved.getShareCode(), newRevision.getShareCode());
    }
}

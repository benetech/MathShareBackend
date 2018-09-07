package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.entity.Problem;
import org.benetech.mathshare.model.entity.ProblemSet;
import org.benetech.mathshare.model.entity.ProblemSetRevision;
import org.benetech.mathshare.model.entity.ProblemSolution;
import org.benetech.mathshare.model.mother.ProblemMother;
import org.benetech.mathshare.model.mother.ProblemSetMother;
import org.benetech.mathshare.model.mother.ProblemSetRevisionMother;
import org.benetech.mathshare.model.mother.ProblemSolutionMother;
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
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2, replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProblemSolutionRepositoryTest {

    @Autowired
    private ProblemSolutionRepository problemSolutionRepository;

    @Autowired
    private ProblemSetRepository problemSetRepository;

    @Autowired
    private ProblemSetRevisionRepository problemSetRevisionRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void shouldSaveProblemSolution() {
        int dbSizeBeforeSave = problemSolutionRepository.findAll().size();
        ProblemSet problemSet = problemSetRepository.save(ProblemSetMother.validInstance());
        ProblemSetRevision revision = problemSetRevisionRepository.save(ProblemSetRevisionMother.validInstance(problemSet));
        Problem problem = problemRepository.save(ProblemMother.validInstance(revision));
        problemSolutionRepository.saveAndFlush(ProblemSolutionMother.validInstance(problem));
        int dbSizeAfterSave = problemSolutionRepository.findAll().size();
        Assert.assertEquals(dbSizeBeforeSave + 1, dbSizeAfterSave);
    }

    @Test
    public void shouldFindByEditCode() {
        ProblemSet problemSet = problemSetRepository.save(ProblemSetMother.validInstance());
        ProblemSetRevision revision = problemSetRevisionRepository.save(ProblemSetRevisionMother.validInstance(problemSet));
        Problem problem = problemRepository.save(ProblemMother.validInstance(revision));
        ProblemSolution saved = problemSolutionRepository.save(ProblemSolutionMother.validInstance(problem));
        em.refresh(saved);
        ProblemSolution problemSolutionFromDB = problemSolutionRepository.findOneByEditCode(saved.getEditCode());
        Assert.assertNotNull(problemSolutionFromDB);
    }
}

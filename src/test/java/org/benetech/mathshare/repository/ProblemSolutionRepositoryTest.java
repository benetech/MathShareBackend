package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.entity.ProblemSolution;
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
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.HSQL, replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProblemSolutionRepositoryTest {

    @Autowired
    private ProblemSolutionRepository problemSolutionRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void shouldSaveProblemSolution() {
        int dbSizeBeforeSave = problemSolutionRepository.findAll().size();
        problemSolutionRepository.saveAndFlush(ProblemSolutionMother.createValidInstance());
        int dbSizeAfterSave = problemSolutionRepository.findAll().size();
        Assert.assertEquals(dbSizeBeforeSave + 1, dbSizeAfterSave);
    }

    @Test
    public void shouldFindByEditCode() {
        ProblemSolution saved = problemSolutionRepository.save(ProblemSolutionMother.createValidInstance());
        em.refresh(saved);
        ProblemSolution problemSolutionFromDB = problemSolutionRepository.findOneByEditCode(saved.getEditCode());
        Assert.assertNotNull(problemSolutionFromDB);
    }
}

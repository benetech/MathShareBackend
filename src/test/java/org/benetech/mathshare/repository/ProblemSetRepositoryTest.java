package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.entity.ProblemSet;
import org.benetech.mathshare.model.mother.ProblemSetMother;
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
public class ProblemSetRepositoryTest {

    @Autowired
    private ProblemSetRepository problemSetRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void shouldSaveProblemSet() {
        int dbSizeBeforeSave = problemSetRepository.findAll().size();
        problemSetRepository.saveAndFlush(ProblemSetMother.validInstance());
        int dbSizeAfterSave = problemSetRepository.findAll().size();
        Assert.assertEquals(dbSizeBeforeSave + 1, dbSizeAfterSave);
    }

    @Test
    public void shouldFindByEditCode() {
        ProblemSet saved = problemSetRepository.save(ProblemSetMother.validInstance());
        em.refresh(saved);
        ProblemSet problemSet = problemSetRepository.findOneByEditCode(saved.getEditCode());
        Assert.assertNotNull(problemSet);
    }
}

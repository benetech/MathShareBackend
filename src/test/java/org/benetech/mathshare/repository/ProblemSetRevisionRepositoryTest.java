package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.entity.ProblemSet;
import org.benetech.mathshare.model.entity.ProblemSetRevision;
import org.benetech.mathshare.model.mother.ProblemSetRevisionMother;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.HSQL, replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@Transactional
public class ProblemSetRevisionRepositoryTest {

    @Autowired
    private ProblemSetRevisionRepository problemSetRevisionRepository;

    @Autowired
    private ProblemSetRepository problemSetRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void shouldSaveProblemSetRevision() {
        int dbSizeBeforeSave = problemSetRevisionRepository.findAll().size();
        problemSetRevisionRepository.saveAndFlush(ProblemSetRevisionMother.createValidInstance());
        int dbSizeAfterSave = problemSetRevisionRepository.findAll().size();
        Assert.assertEquals(dbSizeBeforeSave + 1, dbSizeAfterSave);
    }

    @Test
    public void shouldFindProblemSetRevisionByShareCode() {
        ProblemSetRevision saved = problemSetRevisionRepository.save(ProblemSetRevisionMother.createValidInstance());
        em.refresh(saved);
        ProblemSetRevision problemSetRevision = problemSetRevisionRepository.findOneByShareCode(saved.getShareCode());
        Assert.assertNotNull(problemSetRevision);
        Assert.assertNotNull(problemSetRevision.getProblemSet());
    }

    @Test
    public void shouldFindByProblemSetAndReplacedBy() {
        ProblemSetRevision saved = problemSetRevisionRepository.save(ProblemSetRevisionMother.createValidInstance());
        em.refresh(saved);
        ProblemSet problemSet = problemSetRepository.findAll().get(0);
        ProblemSetRevision problemSetRevision = problemSetRevisionRepository.findAllByProblemSetAndReplacedBy(problemSet, null);
        Assert.assertNotNull(problemSetRevision);
        ProblemSetRevision newRevision = problemSetRevisionRepository.save(ProblemSetRevisionMother.createNewRevisionOfValidInstance(problemSet));
        Assert.assertNotNull(newRevision);
    }
}

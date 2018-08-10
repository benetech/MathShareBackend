package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.entity.ProblemSet;
import org.benetech.mathshare.model.entity.ProblemSetRevision;
import org.benetech.mathshare.model.mother.ProblemSetMother;
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

    @Autowired
    private ProblemRepository problemRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void shouldSaveProblemSetRevision() {
        int dbSizeBeforeSave = problemSetRevisionRepository.findAll().size();
        ProblemSet problemSet = problemSetRepository.save(ProblemSetMother.validInstance());
        problemSetRevisionRepository.saveAndFlush(ProblemSetRevisionMother.validInstance(problemSet));
        int dbSizeAfterSave = problemSetRevisionRepository.findAll().size();
        Assert.assertEquals(dbSizeBeforeSave + 1, dbSizeAfterSave);
    }

    @Test
    public void shouldFindOneByShareCode() {
        ProblemSet problemSet = problemSetRepository.save(ProblemSetMother.validInstance());
        ProblemSetRevision saved = problemSetRevisionRepository.save(ProblemSetRevisionMother.validInstance(problemSet));
        em.refresh(saved);
        Assert.assertNotNull(problemSetRevisionRepository.findOneByShareCode(saved.getShareCode()));
    }

    @Test
    public void shouldFindProblemSetRevisionByShareCode() {
        ProblemSet problemSet = problemSetRepository.save(ProblemSetMother.validInstance());
        ProblemSetRevision saved = problemSetRevisionRepository.save(ProblemSetRevisionMother.validInstance(problemSet));
        em.refresh(saved);
        ProblemSetRevision problemSetRevision = problemSetRevisionRepository.findOneByShareCode(saved.getShareCode());
        Assert.assertNotNull(problemSetRevision);
        Assert.assertNotNull(problemSetRevision.getProblemSet());
    }

    @Test
    public void shouldFindByProblemSetAndReplacedBy() {
        ProblemSet problemSet = problemSetRepository.save(ProblemSetMother.validInstance());
        problemSetRevisionRepository.save(ProblemSetRevisionMother.validInstance(problemSet));
        ProblemSet problemSetFromDB = problemSetRepository.findAll().get(0);
        ProblemSetRevision problemSetRevision = problemSetRevisionRepository.findOneByProblemSetAndReplacedBy(problemSetFromDB, null);
        Assert.assertNotNull(problemSetRevision);
        ProblemSetRevision newRevision = problemSetRevisionRepository.save(ProblemSetRevisionMother.revisionOf(problemSetFromDB));
        Assert.assertNotNull(newRevision);
    }

    @Test
    public void shouldFindOldestOne() {
        problemSetRevisionRepository.deleteAll();
        ProblemSet problemSet = problemSetRepository.save(ProblemSetMother.validInstance());
        ProblemSetRevision first = problemSetRevisionRepository.save(ProblemSetRevisionMother.validInstance(problemSet));
        problemSetRevisionRepository.save(ProblemSetRevisionMother.validInstance(problemSet));

        ProblemSetRevision result = problemSetRevisionRepository.findFirstByOrderByDateCreatedAsc();
        Assert.assertNotNull(result);
        Assert.assertEquals(first.getId(), result.getId());
    }

    @Test
    public void shouldSaveProblemSetRevisionAndAllItsProblems() {
        int problemsBeforeSave = problemRepository.findAll().size();
        ProblemSet problemSet = problemSetRepository.save(ProblemSetMother.validInstance());
        problemSetRevisionRepository.save(ProblemSetRevisionMother.withProblems(problemSet, 3));
        Assert.assertEquals(problemsBeforeSave + 3, problemRepository.findAll().size());
    }
}

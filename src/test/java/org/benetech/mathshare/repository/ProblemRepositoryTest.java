package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.entity.Problem;
import org.benetech.mathshare.model.entity.ProblemSet;
import org.benetech.mathshare.model.entity.ProblemSetRevision;
import org.benetech.mathshare.model.mother.ProblemMother;
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

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2, replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProblemRepositoryTest {

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private ProblemSetRepository problemSetRepository;

    @Autowired
    private ProblemSetRevisionRepository problemSetRevisionRepository;

    @Test
    public void shouldSaveProblem() {
        ProblemSet problemSet = problemSetRepository.save(ProblemSetMother.validInstance());
        ProblemSetRevision revision = problemSetRevisionRepository.save(ProblemSetRevisionMother.validInstance(problemSet));
        Problem saved = problemRepository.save(ProblemMother.validInstance(revision));
        Problem problemFromDB = problemRepository.findById(saved.getId()).get();
        Assert.assertEquals(ProblemMother.DEFAULT_PROBLEM_TEXT, problemFromDB.getProblemText());
        Assert.assertEquals(ProblemMother.DEFAULT_PROBLEM_TITLE, problemFromDB.getTitle());
    }

    @Test
    public void shouldFindAllByProblemSet() {
        ProblemSet problemSet = problemSetRepository.save(ProblemSetMother.validInstance());
        ProblemSetRevision uniqueRevision = problemSetRevisionRepository.save(ProblemSetRevisionMother.validInstance(problemSet));
        ProblemSetRevision commonRevision = problemSetRevisionRepository.save(ProblemSetRevisionMother.validInstance(problemSet));
        problemRepository.save(ProblemMother.validInstance(commonRevision));
        problemRepository.save(ProblemMother.validInstance(commonRevision));
        problemRepository.save(ProblemMother.validInstance(uniqueRevision));
        Assert.assertEquals(2, problemRepository.findAllByProblemSetRevision(commonRevision).size());
    }
}

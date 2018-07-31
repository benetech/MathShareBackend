package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.entity.Problem;
import org.benetech.mathshare.model.mother.ProblemMother;
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
public class ProblemRepositoryTest {

    @Autowired
    private ProblemRepository problemRepository;

    @Test
    public void shouldSaveProblem() {
        problemRepository.save(ProblemMother.validInstance());
        Problem problemFromDB = problemRepository.findAll().get(0);
        Assert.assertEquals(ProblemMother.DEFAULT_PROBLEM_TEXT, problemFromDB.getProblemText());
    }
}

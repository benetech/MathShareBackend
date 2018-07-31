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

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.HSQL, replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProblemSolutionRepositoryTest {

    @Autowired
    private ProblemSolutionRepository problemSolutionRepository;

    @Test
    public void shouldSaveProblemSolution() {
        problemSolutionRepository.save(ProblemSolutionMother.validInstance());
        ProblemSolution problemSolutionFromDB = problemSolutionRepository.findAll().get(0);
        Assert.assertEquals(ProblemSolutionMother.DEFAULT_EDIT_CODE, problemSolutionFromDB.getEditCode());
    }
}

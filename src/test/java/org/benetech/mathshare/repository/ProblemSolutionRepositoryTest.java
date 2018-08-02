package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.mother.ProblemSolutionUtils;
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
        int dbSizeBeforeSave = problemSolutionRepository.findAll().size();
        problemSolutionRepository.saveAndFlush(ProblemSolutionUtils.createValidInstance());
        int dbSizeAfterSave = problemSolutionRepository.findAll().size();
        Assert.assertEquals(dbSizeBeforeSave + 1, dbSizeAfterSave);
    }
}

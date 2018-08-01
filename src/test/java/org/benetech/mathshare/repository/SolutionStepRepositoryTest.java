package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.entity.SolutionStep;
import org.benetech.mathshare.model.mother.SolutionStepUtils;
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
public class SolutionStepRepositoryTest {

    @Autowired
    private SolutionStepRepository solutionStepRepository;

    @Test
    public void shouldSaveSolutionStep() {
        solutionStepRepository.save(SolutionStepUtils.createValidInstance());
        SolutionStep solutionStepFromDB = solutionStepRepository.findAll().get(0);
        Assert.assertEquals(SolutionStepUtils.DEFAULT_STEP_VALUE, solutionStepFromDB.getStepValue());
        Assert.assertEquals(SolutionStepUtils.DEFAULT_DELETED_VALUE, solutionStepFromDB.getDeleted());
    }
}

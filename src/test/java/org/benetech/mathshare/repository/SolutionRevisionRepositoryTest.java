package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.entity.SolutionRevision;
import org.benetech.mathshare.model.mother.ProblemSolutionUtils;
import org.benetech.mathshare.model.mother.SolutionRevisionUtils;
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
public class SolutionRevisionRepositoryTest {

    @Autowired
    private SolutionRevisionRepository solutionRevisionRepository;

    @Test
    public void shouldSaveSolutionRevision() {
        int dbSizeBeforeSave = solutionRevisionRepository.findAll().size();
        solutionRevisionRepository.saveAndFlush(SolutionRevisionUtils.createValidInstance());
        int dbSizeAfterSave = solutionRevisionRepository.findAll().size();
        Assert.assertEquals(dbSizeBeforeSave + 1, dbSizeAfterSave);
    }
}

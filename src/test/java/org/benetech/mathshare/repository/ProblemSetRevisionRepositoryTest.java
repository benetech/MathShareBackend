package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.entity.ProblemSetRevision;
import org.benetech.mathshare.model.mother.ProblemSetRevisionUtils;
import org.benetech.mathshare.model.mother.ProblemSetUtils;
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
public class ProblemSetRevisionRepositoryTest {

    @Autowired
    private ProblemSetRevisionRepository problemSetRevisionRepository;

    @Test
    public void shouldSaveProblemSetRevision() {
        int dbSizeBeforeSave = problemSetRevisionRepository.findAll().size();
        problemSetRevisionRepository.saveAndFlush(ProblemSetRevisionUtils.createValidInstance());
        int dbSizeAfterSave = problemSetRevisionRepository.findAll().size();
        Assert.assertEquals(dbSizeBeforeSave + 1, dbSizeAfterSave);
    }
}

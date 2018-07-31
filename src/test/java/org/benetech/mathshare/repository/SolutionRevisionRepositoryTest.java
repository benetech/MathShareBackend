package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.entity.SolutionRevision;
import org.benetech.mathshare.model.mother.SolutionRevisionMother;
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
        solutionRevisionRepository.save(SolutionRevisionMother.validInstance());
        SolutionRevision solutionRevisionFromDB = solutionRevisionRepository.findAll().get(0);
        Assert.assertEquals(SolutionRevisionMother.DEFAULT_SHARE_CODE, solutionRevisionFromDB.getShareCode());
    }
}

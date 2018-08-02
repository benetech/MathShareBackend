package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.mother.ProblemSetMother;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProblemSetRepositoryTest {

    @Autowired
    private ProblemSetRepository problemSetRepository;

    @Test
    public void shouldSaveProblemSet() {
        int dbSizeBeforeSave = problemSetRepository.findAll().size();
        problemSetRepository.saveAndFlush(ProblemSetMother.createValidInstance());
        int dbSizeAfterSave = problemSetRepository.findAll().size();
        Assert.assertEquals(dbSizeBeforeSave + 1, dbSizeAfterSave);
    }
}

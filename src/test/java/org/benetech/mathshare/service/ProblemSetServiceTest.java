package org.benetech.mathshare.service;

import org.benetech.mathshare.model.entity.ProblemSetRevision;
import org.benetech.mathshare.model.mother.ProblemSetMother;
import org.benetech.mathshare.model.mother.ProblemSetRevisionMother;
import org.benetech.mathshare.repository.ProblemSetRepository;
import org.benetech.mathshare.repository.ProblemSetRevisionRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.HSQL, replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProblemSetServiceTest {

    private static final Long CODE = 1L;

    @MockBean
    private ProblemSetRevisionRepository problemSetRevisionRepository;

    @MockBean
    private ProblemSetRepository problemSetRepository;

    @InjectMocks
    private ProblemSetService problemSetService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldGetProblemSet() {
        given(this.problemSetRevisionRepository.findOneByShareCode(CODE))
                .willReturn(ProblemSetRevisionMother.validInstance());
        ProblemSetRevision problemSetRevisionFromDB = problemSetService.getProblemSetByShareUrl(CODE);
        Assert.assertEquals(ProblemSetRevisionMother.validInstance(), problemSetRevisionFromDB);
    }

    @Test
    public void shouldGetNewestProblemSet() {
        given(this.problemSetRepository.findOneByEditCode(CODE)).willReturn(ProblemSetMother.validInstance());
        given(this.problemSetRevisionRepository.findOneByProblemSetAndReplacedBy(ProblemSetMother.validInstance(), null))
                .willReturn(ProblemSetRevisionMother.createNewRevisionOfValidInstance(ProblemSetMother.validInstance()));
        ProblemSetRevision problemSetRevisionFromDB = problemSetService.getLatestProblemSet(CODE);
        Assert.assertEquals(ProblemSetRevisionMother.createNewRevisionOfValidInstance(ProblemSetMother.validInstance()), problemSetRevisionFromDB);
    }

    @Test
    public void shouldSaveNewProblemSetRevision() {
        given(this.problemSetRevisionRepository.findOneByProblemSetAndReplacedBy(ProblemSetMother.validInstance(), null))
                .willReturn(ProblemSetRevisionMother.createNewRevisionOfValidInstance(ProblemSetMother.validInstance()));
        given(this.problemSetRevisionRepository.save(new ProblemSetRevision(ProblemSetMother.validInstance())))
                .willReturn(new ProblemSetRevision(ProblemSetMother.validInstance()));
        problemSetService.saveNewVersionOfProblemSet(ProblemSetMother.validInstance());
        ArgumentCaptor<ProblemSetRevision> revisionCaptor = ArgumentCaptor.forClass(ProblemSetRevision.class);
        verify(this.problemSetRevisionRepository, times(2)).save(revisionCaptor.capture());

        Assert.assertNotNull(revisionCaptor.getAllValues().get(1).getReplacedBy());
    }
}

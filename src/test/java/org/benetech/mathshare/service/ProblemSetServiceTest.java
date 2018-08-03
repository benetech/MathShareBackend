package org.benetech.mathshare.service;

import org.benetech.mathshare.model.entity.ProblemSetRevision;
import org.benetech.mathshare.model.mother.ProblemSetRevisionUtils;
import org.benetech.mathshare.model.mother.ProblemSetUtils;
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

    private static final String GENERATED_URL = "generatedUrl";

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
        given(this.problemSetRevisionRepository.findOneByShareCode(ProblemSetRevisionUtils.DEFAULT_SHARE_CODE))
                .willReturn(ProblemSetRevisionUtils.createValidInstance());
        ProblemSetRevision problemSetRevisionFromDB =  problemSetService.getProblemSetByShareUrl(ProblemSetRevisionUtils.DEFAULT_SHARE_CODE);
        Assert.assertEquals(ProblemSetRevisionUtils.createValidInstance(), problemSetRevisionFromDB);
    }

    @Test
    public void shouldGetNewestProblemSet() {
        given(this.problemSetRepository.findOneByEditCode(ProblemSetUtils.DEFAULT_EDIT_CODE)).willReturn(ProblemSetUtils.createValidInstance());
        given(this.problemSetRevisionRepository.findAllByProblemSetAndReplacedBy(ProblemSetUtils.createValidInstance(), null))
                .willReturn(ProblemSetRevisionUtils.createNewRevisionOfValidInstance(ProblemSetUtils.createValidInstance()));
        ProblemSetRevision problemSetRevisionFromDB = problemSetService.getLatestProblemSet(ProblemSetUtils.DEFAULT_EDIT_CODE);
        Assert.assertEquals(ProblemSetRevisionUtils.createNewRevisionOfValidInstance(ProblemSetUtils.createValidInstance()), problemSetRevisionFromDB);
    }

    @Test
    public void shouldSaveNewProblemSetRevision() {
        given(this.problemSetRevisionRepository.findAllByProblemSetAndReplacedBy(ProblemSetUtils.createValidInstance(), null))
                .willReturn(ProblemSetRevisionUtils.createNewRevisionOfValidInstance(ProblemSetUtils.createValidInstance()));
        given(this.problemSetRevisionRepository.save(new ProblemSetRevision(ProblemSetUtils.createValidInstance(), GENERATED_URL)))
                .willReturn(new ProblemSetRevision(ProblemSetUtils.createValidInstance(), GENERATED_URL));
        problemSetService.saveNewVersionOfProblemSet(ProblemSetUtils.createValidInstance());
        ArgumentCaptor<ProblemSetRevision> revisionCaptor = ArgumentCaptor.forClass(ProblemSetRevision.class);
        verify(this.problemSetRevisionRepository, times(2)).save(revisionCaptor.capture());

        Assert.assertEquals("generatedUrl", revisionCaptor.getAllValues().get(1).getReplacedBy().getShareCode());
    }
}

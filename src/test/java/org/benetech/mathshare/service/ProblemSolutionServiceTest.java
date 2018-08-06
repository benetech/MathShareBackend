package org.benetech.mathshare.service;

import org.benetech.mathshare.model.entity.SolutionRevision;
import org.benetech.mathshare.model.mother.ProblemSolutionUtils;
import org.benetech.mathshare.model.mother.SolutionRevisionUtils;
import org.benetech.mathshare.repository.ProblemSolutionRepository;
import org.benetech.mathshare.repository.SolutionRevisionRepository;
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
public class ProblemSolutionServiceTest {

    private static final String GENERATED_URL = "generatedUrl";

    @MockBean
    private SolutionRevisionRepository solutionRevisionRepository;

    @MockBean
    private ProblemSolutionRepository problemSolutionRepository;

    @InjectMocks
    private ProblemSolutionService problemSolutionService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldGetProblemSolution() {
        given(this.solutionRevisionRepository.findOneByShareCode(SolutionRevisionUtils.DEFAULT_SHARE_CODE))
                .willReturn(SolutionRevisionUtils.createValidInstance());
        SolutionRevision solutionRevisionFromDB = problemSolutionService.getSolutionRevisionByShareUrl(SolutionRevisionUtils.DEFAULT_SHARE_CODE);
        Assert.assertEquals(SolutionRevisionUtils.createValidInstance(), solutionRevisionFromDB);
    }

    @Test
    public void shouldGetNewestProblemSet() {
        given(this.problemSolutionRepository.findOneByEditCode(ProblemSolutionUtils.DEFAULT_EDIT_CODE)).willReturn(ProblemSolutionUtils.createValidInstance());
        given(this.solutionRevisionRepository.findAllByProblemSolutionAndReplacedBy(ProblemSolutionUtils.createValidInstance(), null))
                .willReturn(SolutionRevisionUtils.createNewRevisionOfValidInstance(ProblemSolutionUtils.createValidInstance()));
        SolutionRevision solutionRevisionFromDB = problemSolutionService.getLatestSolutionRevision(ProblemSolutionUtils.DEFAULT_EDIT_CODE);
        Assert.assertEquals(SolutionRevisionUtils.createNewRevisionOfValidInstance(ProblemSolutionUtils.createValidInstance()), solutionRevisionFromDB);
    }

    @Test
    public void shouldSaveNewProblemSetRevision() {
        given(this.solutionRevisionRepository.findAllByProblemSolutionAndReplacedBy(ProblemSolutionUtils.createValidInstance(), null))
                .willReturn(SolutionRevisionUtils.createNewRevisionOfValidInstance(ProblemSolutionUtils.createValidInstance()));
        given(this.solutionRevisionRepository.save(new SolutionRevision(GENERATED_URL, ProblemSolutionUtils.createValidInstance())))
                .willReturn(new SolutionRevision(GENERATED_URL, ProblemSolutionUtils.createValidInstance()));
        problemSolutionService.saveNewVersionOfSolution(ProblemSolutionUtils.createValidInstance());
        ArgumentCaptor<SolutionRevision> revisionCaptor = ArgumentCaptor.forClass(SolutionRevision.class);
        verify(this.solutionRevisionRepository, times(2)).save(revisionCaptor.capture());

        Assert.assertEquals("generatedUrl", revisionCaptor.getAllValues().get(1).getReplacedBy().getShareCode());
    }
}

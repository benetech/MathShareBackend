package org.benetech.mathshare.service;

import org.benetech.mathshare.converters.UrlCodeConverter;
import org.benetech.mathshare.mappers.SolutionMapper;
import org.benetech.mathshare.model.dto.SolutionDTO;
import org.benetech.mathshare.model.entity.Problem;
import org.benetech.mathshare.model.entity.SolutionRevision;
import org.benetech.mathshare.model.entity.SolutionStep;
import org.benetech.mathshare.model.mother.ProblemMother;
import org.benetech.mathshare.model.mother.ProblemSolutionMother;
import org.benetech.mathshare.model.mother.SolutionRevisionMother;
import org.benetech.mathshare.model.mother.SolutionStepMother;
import org.benetech.mathshare.repository.ProblemRepository;
import org.benetech.mathshare.repository.ProblemSolutionRepository;
import org.benetech.mathshare.repository.SolutionRevisionRepository;
import org.benetech.mathshare.repository.SolutionStepRepository;
import org.benetech.mathshare.service.impl.ProblemSolutionServiceImpl;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.benetech.mathshare.model.mother.ProblemSetRevisionMother.INVALID_CODE;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.HSQL, replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProblemSolutionServiceTest {

    private static final Long CODE = 1L;

    @MockBean
    private SolutionRevisionRepository solutionRevisionRepository;

    @MockBean
    private ProblemSolutionRepository problemSolutionRepository;

    @MockBean
    private ProblemRepository problemRepository;

    @MockBean
    private SolutionStepRepository solutionStepRepository;

    @InjectMocks
    private ProblemSolutionServiceImpl problemSolutionService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldGetProblemSolution() {
        given(this.solutionRevisionRepository.findOneByShareCode(CODE))
                .willReturn(SolutionRevisionMother.validInstance());
        SolutionRevision solutionRevisionFromDB = problemSolutionService.getSolutionRevisionByShareUrl(CODE);
        Assert.assertEquals(SolutionRevisionMother.validInstance(), solutionRevisionFromDB);
    }

    @Test
    public void shouldGetNewestProblemSet() {
        given(this.problemSolutionRepository.findOneByEditCode(CODE)).willReturn(ProblemSolutionMother.validInstance());
        given(this.solutionRevisionRepository.findAllByProblemSolutionAndReplacedBy(ProblemSolutionMother.validInstance(), null))
                .willReturn(SolutionRevisionMother.createNewRevisionOfValidInstance(ProblemSolutionMother.validInstance()));
        SolutionRevision solutionRevisionFromDB = problemSolutionService.getLatestSolutionRevision(CODE);
        Assert.assertEquals(SolutionRevisionMother.createNewRevisionOfValidInstance(ProblemSolutionMother.validInstance()), solutionRevisionFromDB);
    }

    @Test
    public void shouldSaveNewProblemSetRevision() {
        given(this.solutionRevisionRepository.findAllByProblemSolutionAndReplacedBy(ProblemSolutionMother.validInstance(), null))
                .willReturn(SolutionRevisionMother.createNewRevisionOfValidInstance(ProblemSolutionMother.validInstance()));
        given(this.solutionRevisionRepository.save(new SolutionRevision(ProblemSolutionMother.validInstance())))
                .willReturn(new SolutionRevision(ProblemSolutionMother.validInstance()));
        problemSolutionService.saveNewVersionOfSolution(ProblemSolutionMother.validInstance());
        ArgumentCaptor<SolutionRevision> revisionCaptor = ArgumentCaptor.forClass(SolutionRevision.class);
        verify(this.solutionRevisionRepository, times(2)).save(revisionCaptor.capture());

        Assert.assertNotNull(revisionCaptor.getAllValues().get(1).getReplacedBy());
    }

    @Test
    public void shouldReturnSolutionByUrlCode() {
        SolutionRevision solutionRevision = SolutionRevisionMother.withShareCodeAndEditCode(CODE, CODE);
        Problem problem = ProblemMother.validInstance();
        List<SolutionStep> solutionStepList = SolutionStepMother.createValidStepsList(3);

        when(solutionRevisionRepository.findOneByShareCode(CODE)).thenReturn(solutionRevision);
        when(problemRepository.findById(solutionRevision.getProblemSolution().getId())).thenReturn(Optional.of(problem));
        when(solutionStepRepository.findAllBySolutionRevision(solutionRevision))
                .thenReturn(solutionStepList);

        SolutionDTO result = problemSolutionService.findSolutionByUrlCode(UrlCodeConverter.toUrlCode(CODE));
        Assert.assertEquals(solutionStepList.stream().map(SolutionMapper.INSTANCE::toDto).collect(Collectors.toList()), result.getSteps());
    }

    @Test
    public void shouldReturnNullWhenProblemSetNotFound() {
        when(solutionRevisionRepository.findOneByShareCode(CODE)).thenReturn(null);
        SolutionDTO result = problemSolutionService.findSolutionByUrlCode(UrlCodeConverter.toUrlCode(CODE));
        Assert.assertNull(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenCodeIsInvalid() {
        problemSolutionService.findSolutionByUrlCode(INVALID_CODE);
    }
}

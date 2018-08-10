package org.benetech.mathshare.service;

import org.benetech.mathshare.converters.UrlCodeConverter;
import org.benetech.mathshare.mappers.SolutionMapper;
import org.benetech.mathshare.model.dto.SolutionDTO;
import org.benetech.mathshare.model.entity.Problem;
import org.benetech.mathshare.model.entity.ProblemSetRevision;
import org.benetech.mathshare.model.entity.ProblemSolution;
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
        SolutionRevision revision = SolutionRevisionMother.mockInstance();
        given(this.solutionRevisionRepository.findOneByShareCode(CODE)).willReturn(revision);
        SolutionRevision solutionRevisionFromDB = problemSolutionService.getSolutionRevisionByShareUrl(CODE);
        Assert.assertEquals(revision, solutionRevisionFromDB);
    }

    @Test
    public void shouldGetNewestProblemSet() {
        ProblemSolution solution = ProblemSolutionMother.mockInstance();
        given(this.problemSolutionRepository.findOneByEditCode(CODE)).willReturn(solution);
        given(this.solutionRevisionRepository.findOneByProblemSolutionAndReplacedBy(solution, null))
                .willReturn(SolutionRevisionMother.revisionOf(solution));
        SolutionRevision solutionRevisionFromDB = problemSolutionService.getLatestSolutionRevision(CODE);
        Assert.assertEquals(SolutionRevisionMother.revisionOf(solution), solutionRevisionFromDB);
    }

    @Test
    public void shouldSaveNewProblemSetRevision() {
        ProblemSolution solution = ProblemSolutionMother.mockInstance();
        SolutionRevision revision = SolutionRevisionMother.revisionOf(solution);
        given(this.solutionRevisionRepository.findOneByProblemSolutionAndReplacedBy(solution, null))
                .willReturn(SolutionRevisionMother.revisionOf(solution));
        given(this.solutionRevisionRepository.save(revision))
                .willReturn(revision);
        problemSolutionService.saveNewVersionOfSolution(solution);
        ArgumentCaptor<SolutionRevision> revisionCaptor = ArgumentCaptor.forClass(SolutionRevision.class);
        verify(this.solutionRevisionRepository, times(2)).save(revisionCaptor.capture());

        Assert.assertNotNull(revisionCaptor.getAllValues().get(1).getReplacedBy());
    }

    @Test
    public void shouldReturnSolutionByUrlCode() {
        SolutionRevision solutionRevision = SolutionRevisionMother.mockInstance();
        ProblemSetRevision revision1 = new ProblemSetRevision();
        Problem problem = ProblemMother.validInstance(revision1);
        List<SolutionStep> solutionStepList = SolutionStepMother.createValidStepsList(solutionRevision, 3);

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

    @Test
    public void shouldReturnStepListByEditUrlCode() {
        SolutionRevision revision = SolutionRevisionMother.mockInstance();
        List<SolutionStep> steps = SolutionStepMother.createValidStepsList(revision, 3);

        when(problemSolutionRepository.findOneByEditCode(CODE))
                .thenReturn(revision.getProblemSolution());
        when(solutionRevisionRepository.findOneByProblemSolutionAndReplacedBy(revision.getProblemSolution(), null))
                .thenReturn(revision);
        when(solutionStepRepository.findAllBySolutionRevision(revision))
                .thenReturn(steps);

        SolutionDTO result = problemSolutionService.getLatestProblemSolutionForEditing(UrlCodeConverter.toUrlCode(CODE));
        Assert.assertEquals(steps.stream().map(SolutionMapper.INSTANCE::toDto).collect(Collectors.toList()),
                result.getSteps());
    }

    @Test
    public void shouldCreateProblemSolution() {
        given(this.solutionRevisionRepository.findOneByProblemSolutionAndReplacedBy(ProblemSolutionMother.mockInstance(), null))
                .willReturn(SolutionRevisionMother.mockInstance());
        given(this.problemSolutionRepository.save(ProblemSolutionMother.mockInstance()))
                .willReturn(ProblemSolutionMother.mockInstance());
        given(this.solutionRevisionRepository.save(SolutionRevisionMother.mockInstance()))
                .willReturn(SolutionRevisionMother.mockInstance());
        problemSolutionService.createOrUpdateProblemSolution(ProblemSolutionMother.mockInstance());
        ArgumentCaptor<ProblemSolution> problemSolutionCaptor = ArgumentCaptor.forClass(ProblemSolution.class);
        verify(this.problemSolutionRepository, times(1)).save(problemSolutionCaptor.capture());
        ArgumentCaptor<SolutionRevision> revisionCaptor = ArgumentCaptor.forClass(SolutionRevision.class);
        verify(this.solutionRevisionRepository, times(1)).save(revisionCaptor.capture());

        Assert.assertNull(revisionCaptor.getAllValues().get(0).getReplacedBy());
    }

    @Test
    public void shouldUpdateProblemSolution() {
        ProblemSolution problemSolution = ProblemSolutionMother.mockInstance();
        problemSolution.setId(1);
        given(this.solutionRevisionRepository.findOneByProblemSolutionAndReplacedBy(problemSolution, null))
                .willReturn(SolutionRevisionMother.mockInstance());
        given(this.solutionRevisionRepository.save(new SolutionRevision(problemSolution)))
                .willReturn(SolutionRevisionMother.mockInstance());
        SolutionRevision rev = SolutionRevisionMother.mockInstance();
        rev.setReplacedBy(new SolutionRevision(ProblemSolutionMother.mockInstance()));
        given(this.solutionRevisionRepository.save(rev))
                .willReturn(rev);
        problemSolutionService.createOrUpdateProblemSolution(problemSolution);
        ArgumentCaptor<SolutionRevision> revisionCaptor = ArgumentCaptor.forClass(SolutionRevision.class);
        verify(this.solutionRevisionRepository, times(2)).save(revisionCaptor.capture());

        Assert.assertNotNull(revisionCaptor.getAllValues().get(1).getReplacedBy());
    }
}

package org.benetech.mathshare.service;

import org.benetech.mathshare.converters.UrlCodeConverter;
import org.benetech.mathshare.mappers.ProblemMapper;
import org.benetech.mathshare.mappers.SolutionMapper;
import org.benetech.mathshare.model.dto.ProblemDTO;
import org.benetech.mathshare.model.dto.SolutionDTO;
import org.benetech.mathshare.model.dto.SolutionStepDTO;
import org.benetech.mathshare.model.entity.Problem;
import org.benetech.mathshare.model.entity.ProblemSetRevision;
//import org.benetech.mathshare.model.entity.ProblemSetRevisionSolution;
import org.benetech.mathshare.model.entity.ProblemSolution;
import org.benetech.mathshare.model.entity.ReviewSolutionRevision;
import org.benetech.mathshare.model.entity.SolutionRevision;
import org.benetech.mathshare.model.entity.SolutionStep;
import org.benetech.mathshare.model.mother.ProblemMother;
import org.benetech.mathshare.model.mother.ProblemSetMother;
import org.benetech.mathshare.model.mother.ProblemSetRevisionMother;
import org.benetech.mathshare.model.mother.ProblemSolutionMother;
import org.benetech.mathshare.model.mother.ReviewSolutionRevisionMother;
import org.benetech.mathshare.model.mother.SolutionRevisionMother;
import org.benetech.mathshare.model.mother.SolutionStepMother;
import org.benetech.mathshare.repository.ProblemRepository;
import org.benetech.mathshare.repository.ProblemSetRevisionRepository;
import org.benetech.mathshare.repository.ProblemSolutionRepository;
import org.benetech.mathshare.repository.ReviewSolutionRevisionRepository;
import org.benetech.mathshare.repository.SolutionRevisionRepository;
import org.benetech.mathshare.repository.SolutionStepRepository;
import org.benetech.mathshare.service.impl.ProblemSolutionServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import java.util.ArrayList;
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
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2, replace = AutoConfigureTestDatabase.Replace.NONE)
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

    @MockBean
    private ProblemSetRevisionRepository problemSetRevisionRepository;

    @MockBean
    private ReviewSolutionRevisionRepository reviewSolutionRevisionRepository;

    @InjectMocks
    private ProblemSolutionServiceImpl problemSolutionService;

    @Mock
    @SuppressWarnings("PMD.UnusedPrivateField")
    private EntityManager em;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldGetProblemSolution() {
        SolutionRevision revision = SolutionRevisionMother.mockInstance();
        given(solutionRevisionRepository.findOneByShareCode(CODE)).willReturn(revision);
        SolutionRevision solutionRevisionFromDB = problemSolutionService.getSolutionRevisionByShareUrl(CODE);
        Assert.assertEquals(revision, solutionRevisionFromDB);
    }

    @Test
    public void shouldGetNewestProblemSet() {
        ProblemSolution solution = ProblemSolutionMother.mockInstance();
        given(problemSolutionRepository.findOneByEditCode(CODE)).willReturn(solution);
        given(solutionRevisionRepository.findOneByProblemSolutionAndReplacedBy(solution, null))
                .willReturn(SolutionRevisionMother.revisionOf(solution));
        SolutionRevision solutionRevisionFromDB = problemSolutionService.getLatestSolutionRevision(CODE);
        Assert.assertEquals(SolutionRevisionMother.revisionOf(solution), solutionRevisionFromDB);
    }

//    @Test
//    public void shouldSaveNewProblemSetRevision() {
//        ProblemSolution solution = ProblemSolutionMother.mockInstance();
//        SolutionRevision revision = SolutionRevisionMother.revisionOf(solution);
//        given(solutionRevisionRepository.findOneByProblemSolutionAndReplacedBy(solution, null))
//                .willReturn(SolutionRevisionMother.revisionOf(solution));
//        given(solutionRevisionRepository.save(revision))
//                .willReturn(revision);
//        Problem problem = revision.getProblemSolution().getProblem();
//        given(problemSolutionRepository.save(new ProblemSolution(problem))).willReturn(revision.getProblemSolution());
//        given(problemSetRevisionRepository.findOneByShareCode(
//                UrlCodeConverter.fromUrlCode(ProblemSetRevisionMother.VALID_CODE)))
//                .willReturn(problem.getProblemSetRevision());
//        given(problemRepository.findOneByTitleAndProblemTextAndProblemSetRevision(
//                ProblemMother.DEFAULT_PROBLEM_TITLE, ProblemMother.DEFAULT_PROBLEM_TEXT, problem.getProblemSetRevision()))
//                .willReturn(problem);
//
//        problemSolutionService.saveNewVersionOfSolution(SolutionMapper.INSTANCE.toDto(solution));
//        ArgumentCaptor<SolutionRevision> revisionCaptor = ArgumentCaptor.forClass(SolutionRevision.class);
//        verify(solutionRevisionRepository, times(2)).save(revisionCaptor.capture());
//
//        Assert.assertNotNull(revisionCaptor.getAllValues().get(1).getReplacedBy());
//    }

    @Test
    public void shouldReturnSolutionByUrlCode() {
        SolutionRevision solutionRevision = SolutionRevisionMother.mockInstance();
        ProblemSetRevision revision1 = new ProblemSetRevision();
        Problem problem = ProblemMother.validInstance(revision1);
        List<SolutionStep> solutionStepList = SolutionStepMother.createValidStepsList(solutionRevision, 3);
        ReviewSolutionRevision reviewSolutionRevision = ReviewSolutionRevisionMother.mockInstance();

        when(solutionRevisionRepository.findOneByShareCode(CODE)).thenReturn(solutionRevision);
        when(problemRepository.findById(solutionRevision.getProblemSolution().getId())).thenReturn(Optional.of(problem));
        when(solutionStepRepository.findAllBySolutionRevision(solutionRevision))
                .thenReturn(solutionStepList);
        when(reviewSolutionRevisionRepository.findOneBySolutionRevision(solutionRevision))
                .thenReturn(reviewSolutionRevision);

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
        ReviewSolutionRevision reviewSolutionRevision = ReviewSolutionRevisionMother.mockInstance();
        SolutionRevision revision = SolutionRevisionMother.mockInstance();
        List<SolutionStep> steps = SolutionStepMother.createValidStepsList(revision, 3);

        when(problemSolutionRepository.findOneByEditCode(CODE))
                .thenReturn(revision.getProblemSolution());
        when(solutionRevisionRepository.findOneByProblemSolutionAndReplacedBy(revision.getProblemSolution(), null))
                .thenReturn(revision);
        when(solutionStepRepository.findAllBySolutionRevision(revision))
                .thenReturn(steps);
        when(reviewSolutionRevisionRepository.findOneBySolutionRevision(revision))
                .thenReturn(reviewSolutionRevision);

        SolutionDTO result = problemSolutionService.getLatestProblemSolutionForEditing(UrlCodeConverter.toUrlCode(CODE));
        Assert.assertEquals(steps.stream().map(SolutionMapper.INSTANCE::toDto).collect(Collectors.toList()),
                result.getSteps());
    }

//    @Test
//    public void shouldCreateProblemSolution() {
//        SolutionDTO solutionDTO = SolutionMapper.INSTANCE.toDto(ProblemSolutionMother.mockInstance());
//        ProblemDTO problemDTO = ProblemMapper.INSTANCE.toDto(ProblemMother.mockInstance());
//        problemDTO.setProblemSetRevisionShareCode(ProblemSetRevisionMother.VALID_CODE);
//        solutionDTO.setProblem(problemDTO);
//        solutionDTO.setEditCode(UrlCodeConverter.toUrlCode(ProblemSolutionMother.EDIT_CODE));
//        List<SolutionStepDTO> steps = new ArrayList<>();
//        solutionDTO.setSteps(steps);
//        given(this.problemSolutionRepository.findOneByEditCode(ProblemSolutionMother.EDIT_CODE))
//                .willReturn(null);
//        given(this.problemSetRevisionRepository.findOneByShareCode(UrlCodeConverter
//                .fromUrlCode(ProblemSetRevisionMother.VALID_CODE)))
//                .willReturn(ProblemSetRevisionMother.mockInstance());
//        given(this.problemRepository.findOneByTitleAndProblemTextAndProblemSetRevision(solutionDTO.getProblem().getTitle(),
//                solutionDTO.getProblem().getText(), ProblemSetRevisionMother.mockInstance()))
//                .willReturn(ProblemMother.mockInstance());
//        given(this.problemSolutionRepository.save(new ProblemSolution(ProblemMother.mockInstance())))
//                .willReturn(new ProblemSolution(ProblemMother.validInstance(ProblemSetRevisionMother
//                        .withShareCode(ProblemSetMother.mockInstance(), CODE))));
//        given(this.problemSolutionRepository.findOneByEditCode(CODE))
//                .willReturn(ProblemSolutionMother.mockInstance());
//
//        given(this.solutionRevisionRepository.save(new SolutionRevision(
//                new ProblemSolution(ProblemMother.validInstance(ProblemSetRevisionMother
//                        .withShareCode(ProblemSetMother.mockInstance(), CODE))))))
//                .willReturn(SolutionRevisionMother.mockInstance());
//
//        problemSolutionService.createOrUpdateProblemSolution(solutionDTO.getEditCode(), solutionDTO);
//        ArgumentCaptor<ProblemSolution> problemSolutionCaptor = ArgumentCaptor.forClass(ProblemSolution.class);
//        verify(this.problemSolutionRepository, times(1)).save(problemSolutionCaptor.capture());
//        ArgumentCaptor<SolutionRevision> revisionCaptor = ArgumentCaptor.forClass(SolutionRevision.class);
//        verify(this.solutionRevisionRepository, times(1)).save(revisionCaptor.capture());
//
//        Assert.assertNull(revisionCaptor.getAllValues().get(0).getReplacedBy());
//    }

//    @Test
//    public void shouldUpdateProblemSolution() {
//        SolutionDTO solutionDTO = SolutionMapper.INSTANCE.toDto(ProblemSolutionMother.mockInstance());
//        ProblemDTO problemDTO = ProblemMapper.INSTANCE.toDto(ProblemMother.mockInstance());
//        problemDTO.setProblemSetRevisionShareCode(ProblemSetRevisionMother.VALID_CODE);
//        solutionDTO.setProblem(problemDTO);
//        solutionDTO.setEditCode(UrlCodeConverter.toUrlCode(ProblemSolutionMother.EDIT_CODE));
//        List<SolutionStepDTO> steps = new ArrayList<>();
//        solutionDTO.setSteps(steps);
//        given(this.problemSolutionRepository.findOneByEditCode(ProblemSolutionMother.EDIT_CODE))
//                .willReturn(ProblemSolutionMother.mockInstance());
//        given(this.solutionRevisionRepository.findOneByProblemSolutionAndReplacedBy(ProblemSolutionMother.mockInstance(), null))
//                .willReturn(SolutionRevisionMother.mockInstance());
//        given(this.solutionRevisionRepository.save(new SolutionRevision(ProblemSolutionMother.mockInstance())))
//                .willReturn(SolutionRevisionMother.mockInstance());
//        SolutionRevision rev = SolutionRevisionMother.mockInstance();
//        rev.setReplacedBy(new SolutionRevision(ProblemSolutionMother.mockInstance()));
//        given(this.solutionRevisionRepository.save(rev))
//                .willReturn(rev);
//
//        problemSolutionService.createOrUpdateProblemSolution(solutionDTO.getEditCode(), solutionDTO);
//        ArgumentCaptor<SolutionRevision> revisionCaptor = ArgumentCaptor.forClass(SolutionRevision.class);
//        verify(this.solutionRevisionRepository, times(2)).save(revisionCaptor.capture());
//
//        Assert.assertNotNull(revisionCaptor.getAllValues().get(1).getReplacedBy());
//    }
}

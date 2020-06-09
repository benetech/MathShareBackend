package org.benetech.mathshare.service;

import org.benetech.mathshare.converters.UrlCodeConverter;
import org.benetech.mathshare.mappers.ProblemMapper;
import org.benetech.mathshare.model.dto.ProblemSetDTO;
import org.benetech.mathshare.model.entity.Problem;
import org.benetech.mathshare.model.entity.ProblemSet;
import org.benetech.mathshare.model.entity.ProblemSetRevision;
import org.benetech.mathshare.model.mother.ProblemMother;
import org.benetech.mathshare.model.mother.ProblemSetMother;
import org.benetech.mathshare.model.mother.ProblemSetRevisionMother;
import org.benetech.mathshare.repository.ProblemRepository;
import org.benetech.mathshare.repository.ProblemSetRepository;
import org.benetech.mathshare.repository.ProblemSetRevisionRepository;
import org.benetech.mathshare.repository.ProblemStepRepository;
import org.benetech.mathshare.service.impl.ProblemSetServiceImpl;
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
import java.util.stream.Collectors;

import static org.benetech.mathshare.model.mother.ProblemSetRevisionMother.INVALID_CODE;
import static org.benetech.mathshare.model.mother.ProblemSetRevisionMother.VALID_CODE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2, replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProblemSetServiceTest {

    private static final Long CODE = 1L;

    private static final String USER_ID = "aab63d78-cff6-11e9-83a9-935feb96b1df";

    @MockBean
    private ProblemSetRevisionRepository problemSetRevisionRepository;

    @MockBean
    private ProblemSetRepository problemSetRepository;

    @MockBean
    private ProblemRepository problemRepository;

    @MockBean
    private ProblemStepRepository problemStepRepository;

    @SuppressWarnings("PMD.UnusedPrivateField")
    @Mock
    private EntityManager em;

    @InjectMocks
    private ProblemSetServiceImpl problemSetService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldGetProblemSet() {
        ProblemSet problemSet = ProblemSetMother.mockInstance();
        ProblemSetRevision revision = ProblemSetRevisionMother.validInstance(problemSet);
        given(problemSetRevisionRepository.findOneByShareCode(CODE))
                .willReturn(revision);
        ProblemSetRevision problemSetRevisionFromDB = problemSetService.getProblemSetByShareUrl(CODE);
        Assert.assertEquals(revision, problemSetRevisionFromDB);
    }

    @Test
    public void shouldGetNewestProblemSet() {
        ProblemSet problemSet = ProblemSetMother.mockInstance();
        ProblemSetRevision revision = ProblemSetRevisionMother.revisionOf(problemSet);
        given(problemSetRepository.findOneByEditCode(CODE)).willReturn(problemSet);
        given(problemSetRevisionRepository.findOneByProblemSetAndReplacedBy(problemSet, null))
                .willReturn(revision);
        ProblemSetRevision problemSetRevisionFromDB = problemSetService.getLatestProblemSet(CODE);
        Assert.assertEquals(revision, problemSetRevisionFromDB);
    }

//    @Test
//    public void shouldSaveNewProblemSetRevision() {
//        ProblemSetDTO problemSetDTO = ProblemMapper.INSTANCE.toProblemSetDTO(ProblemSetRevisionMother.revisionOf(ProblemSetMother.mockInstance()));
//        ProblemSet problemSet = ProblemMapper.INSTANCE.fromDto(problemSetDTO);
//        given(problemSetRepository.save(problemSet))
//                .willReturn(problemSet);
//        ProblemSetRevision revision = new ProblemSetRevision(problemSet, problemSetDTO.getTitle());
//        given(problemSetRevisionRepository.save(revision))
//                .willReturn(revision);
//        revision.setProblems(new ArrayList<>());
//
//        given(problemSetRevisionRepository.save(revision))
//                .willReturn(revision);
//
//        problemSetService.saveNewProblemSet(ProblemMapper.INSTANCE.toProblemSetDTO(ProblemSetRevisionMother.revisionOf(problemSet)), USER_ID);
//        ArgumentCaptor<ProblemSet> problemSetCaptor = ArgumentCaptor.forClass(ProblemSet.class);
//        verify(problemSetRepository, times(1)).save(problemSetCaptor.capture());
//        ArgumentCaptor<ProblemSetRevision> revisionCaptor = ArgumentCaptor.forClass(ProblemSetRevision.class);
//        verify(this.problemSetRevisionRepository, times(2)).save(revisionCaptor.capture());
//    }

    @Test
    public void shouldReturnProblemsListByUrlCode() {
        ProblemSet problemSet = ProblemSetMother.mockInstance();
        ProblemSetRevision revision = ProblemSetRevisionMother.revisionOf(problemSet);
        revision.setShareCode(UrlCodeConverter.fromUrlCode(ProblemSetRevisionMother.REVISION_CODE));
        List<Problem> problems = ProblemMother.createValidProblemsList(revision, 3);

        when(problemSetRevisionRepository.findOneByShareCode(UrlCodeConverter.fromUrlCode(VALID_CODE)))
                .thenReturn(revision);
        when(problemRepository.findAllByProblemSetRevision(revision))
                .thenReturn(problems);
        when(problemStepRepository.findAllByProblemOrderByIdAsc(any()))
                .thenReturn(new ArrayList<>());

        ProblemSetDTO result = problemSetService.findProblemsByUrlCode(VALID_CODE);
        Assert.assertEquals(problems.stream().map(ProblemMapper.INSTANCE::toDto).collect(Collectors.toList()),
                result.getProblems());
    }

    @Test
    public void shouldReturnNullWhenProblemSetNotFound() {
        when(problemSetRevisionRepository.findOneByShareCode(UrlCodeConverter.fromUrlCode(VALID_CODE)))
                .thenReturn(null);
        ProblemSetDTO result = problemSetService.findProblemsByUrlCode(VALID_CODE);
        Assert.assertNull(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenCodeIsInvalid() {
        problemSetService.findProblemsByUrlCode(INVALID_CODE);
    }

    @Test
    public void shouldReturnProblemsListByEditUrlCode() {
        ProblemSetRevision problemSetRevision = ProblemSetRevisionMother.withShareCode(
                ProblemSetMother.withEditCode(UrlCodeConverter.fromUrlCode(VALID_CODE)), UrlCodeConverter.fromUrlCode(VALID_CODE));
        List<Problem> problems = ProblemMother.createValidProblemsList(problemSetRevision, 3);

        when(problemSetRepository.findOneByEditCode(UrlCodeConverter.fromUrlCode(VALID_CODE)))
                .thenReturn(problemSetRevision.getProblemSet());
        when(problemSetRevisionRepository.findOneByProblemSetAndReplacedBy(problemSetRevision.getProblemSet(), null))
                .thenReturn(problemSetRevision);
        when(problemRepository.findAllByProblemSetRevision(problemSetRevision))
                .thenReturn(problems);

        ProblemSetDTO result = problemSetService.getLatestProblemSetForEditing(VALID_CODE);
        Assert.assertEquals(problems.stream().map(ProblemMapper.INSTANCE::toDto).collect(Collectors.toList()),
                result.getProblems());
    }

    @Test
    public void shouldCreateProblemSet() {
        ProblemSetDTO problemSetDTO = ProblemMapper.INSTANCE.toDto(ProblemSetMother.mockInstance());
        problemSetDTO.setProblems(new ArrayList<>());
        given(this.problemSetRepository.findOneByEditCode(ProblemSetMother.EDIT_CODE))
                .willReturn(null);
        given(this.problemSetRepository.save(ProblemMapper.INSTANCE.fromDto(problemSetDTO)))
                .willReturn(ProblemSetMother.validInstance());
        given(this.problemSetRevisionRepository.save(new ProblemSetRevision(ProblemSetMother.validInstance(), problemSetDTO.getTitle(),
                        false, false, problemSetDTO.getPalettes())))
                .willReturn(new ProblemSetRevision(ProblemSetMother.validInstance(), problemSetDTO.getTitle(), false, false,
                        problemSetDTO.getPalettes()));
        given(this.problemSetRevisionRepository.save(ProblemSetRevisionMother.withProblems(ProblemSetMother.validInstance(), 3)))
                .willReturn(ProblemSetRevisionMother.withProblems(ProblemSetMother.validInstance(), 3));

        problemSetService.createOrUpdateProblemSet(problemSetDTO.getEditCode(), problemSetDTO, USER_ID);
        ArgumentCaptor<ProblemSet> problemSetCaptor = ArgumentCaptor.forClass(ProblemSet.class);
        verify(this.problemSetRepository, times(1)).save(problemSetCaptor.capture());
        ArgumentCaptor<ProblemSetRevision> revisionCaptor = ArgumentCaptor.forClass(ProblemSetRevision.class);
        verify(this.problemSetRevisionRepository, times(2)).save(revisionCaptor.capture());

        Assert.assertNull(revisionCaptor.getAllValues().get(0).getReplacedBy());
    }

//    @Test
//    public void shouldUpdateProblemSet() {
//        ProblemSetDTO problemSetDTO = ProblemMapper.INSTANCE.toDto(ProblemSetMother.mockInstance());
//        problemSetDTO.setProblems(new ArrayList<>());
//        ProblemSetRevision revision = ProblemSetRevisionMother.revisionOf(ProblemSetMother.mockInstance());
//        given(this.problemSetRepository.findOneByEditCode(ProblemSetMother.EDIT_CODE))
//                .willReturn(ProblemSetMother.mockInstance());
//        given(this.problemSetRepository.findOneByEditCode(ProblemSetMother.EDIT_CODE))
//                .willReturn(ProblemSetMother.mockInstance());
//        given(this.problemSetRevisionRepository.findOneByProblemSetAndReplacedBy(ProblemSetMother.mockInstance(), null))
//                .willReturn(revision);
//        given(this.problemSetRevisionRepository.save(new ProblemSetRevision(ProblemSetMother.mockInstance(), problemSetDTO.getTitle())))
//                .willReturn(revision);
//        ProblemSetRevision withProblems = revision;
//        withProblems.setProblems(new ArrayList<>());
//        given(this.problemRepository.findAllByProblemSetRevision(revision))
//                .willReturn(new ArrayList<>());
//        given(this.problemSetRevisionRepository.save(revision))
//                .willReturn(withProblems);
//        given(this.problemSetRevisionRepository.save(revision))
//                .willReturn(withProblems);
//
//        problemSetService.createOrUpdateProblemSet(problemSetDTO.getEditCode(), problemSetDTO, USER_ID);
//        ArgumentCaptor<ProblemSetRevision> revisionCaptor = ArgumentCaptor.forClass(ProblemSetRevision.class);
//        verify(this.problemSetRevisionRepository, times(3)).save(revisionCaptor.capture());
//
//        Assert.assertNotNull(revisionCaptor.getAllValues().get(1).getReplacedBy());
//    }
}

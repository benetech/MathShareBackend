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
import org.benetech.mathshare.service.impl.ProblemSetServiceImpl;
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
import java.util.stream.Collectors;

import static org.benetech.mathshare.model.mother.ProblemSetRevisionMother.INVALID_CODE;
import static org.benetech.mathshare.model.mother.ProblemSetRevisionMother.VALID_CODE;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.HSQL, replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProblemSetServiceTest {

    private static final Long CODE = 1L;

    @MockBean
    private ProblemSetRevisionRepository problemSetRevisionRepository;

    @MockBean
    private ProblemSetRepository problemSetRepository;

    @MockBean
    private ProblemRepository problemRepository;

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
        given(this.problemSetRevisionRepository.findOneByShareCode(CODE))
                .willReturn(revision);
        ProblemSetRevision problemSetRevisionFromDB = problemSetService.getProblemSetByShareUrl(CODE);
        Assert.assertEquals(revision, problemSetRevisionFromDB);
    }

    @Test
    public void shouldGetNewestProblemSet() {
        ProblemSet problemSet = ProblemSetMother.mockInstance();
        ProblemSetRevision revision = ProblemSetRevisionMother.revisionOf(problemSet);
        given(this.problemSetRepository.findOneByEditCode(CODE)).willReturn(problemSet);
        given(this.problemSetRevisionRepository.findOneByProblemSetAndReplacedBy(problemSet, null))
                .willReturn(revision);
        ProblemSetRevision problemSetRevisionFromDB = problemSetService.getLatestProblemSet(CODE);
        Assert.assertEquals(revision, problemSetRevisionFromDB);
    }

    @Test
    public void shouldSaveNewProblemSetRevision() {
        ProblemSet problemSet = ProblemSetMother.mockInstance();
        given(this.problemSetRevisionRepository.findOneByProblemSetAndReplacedBy(problemSet, null))
                .willReturn(ProblemSetRevisionMother.revisionOf(problemSet));
        given(this.problemSetRevisionRepository.save(new ProblemSetRevision(problemSet)))
                .willReturn(new ProblemSetRevision(problemSet));
        problemSetService.saveNewVersionOfProblemSet(problemSet);
        ArgumentCaptor<ProblemSetRevision> revisionCaptor = ArgumentCaptor.forClass(ProblemSetRevision.class);
        verify(this.problemSetRevisionRepository, times(2)).save(revisionCaptor.capture());

        Assert.assertNotNull(revisionCaptor.getAllValues().get(1).getReplacedBy());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionForNotNullId() {
        ProblemSet toSave = new ProblemSet();
        toSave.setId(1);
        problemSetService.saveNewVersionOfProblemSet(toSave);
    }

    @Test
    public void shouldReturnProblemsListByUrlCode() {
        ProblemSet problemSet = ProblemSetMother.mockInstance();
        ProblemSetRevision revision = ProblemSetRevisionMother.revisionOf(problemSet);
        List<Problem> problems = ProblemMother.createValidProblemsList(revision, 3);

        when(problemSetRevisionRepository.findOneByShareCode(UrlCodeConverter.fromUrlCode(VALID_CODE)))
                .thenReturn(revision);
        when(problemRepository.findAllByProblemSetRevision(revision))
                .thenReturn(problems);

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
}

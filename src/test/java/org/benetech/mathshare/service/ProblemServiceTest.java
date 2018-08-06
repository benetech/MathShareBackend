package org.benetech.mathshare.service;

import org.benetech.mathshare.converters.UrlCodeConverter;
import org.benetech.mathshare.mappers.ProblemMapper;
import org.benetech.mathshare.model.dto.ProblemSetDTO;
import org.benetech.mathshare.model.entity.Problem;
import org.benetech.mathshare.model.entity.ProblemSet;
import org.benetech.mathshare.model.mother.ProblemMother;
import org.benetech.mathshare.model.mother.ProblemSetMother;
import org.benetech.mathshare.model.mother.ProblemSetRevisionMother;
import org.benetech.mathshare.repository.ProblemRepository;
import org.benetech.mathshare.repository.ProblemSetRevisionRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;

import static org.benetech.mathshare.model.mother.ProblemSetRevisionMother.INVALID_CODE;
import static org.benetech.mathshare.model.mother.ProblemSetRevisionMother.VALID_CODE;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringRunner.class)
public class ProblemServiceTest {

    @InjectMocks
    private ProblemService problemService;

    @Mock
    private ProblemSetRevisionRepository problemSetRevisionRepository;

    @Mock
    private ProblemRepository problemRepository;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldReturnProblemsListByUrlCode() {
        ProblemSet problemSet = ProblemSetMother.validInstance();
        List<Problem> problems = ProblemMother.createValidProblemsList(3);

        when(problemSetRevisionRepository.findOneByShareCode(UrlCodeConverter.fromUrlCode(VALID_CODE)))
                .thenReturn(ProblemSetRevisionMother.validInstance(problemSet));
        when(problemRepository.findAllByProblemSet(problemSet))
                .thenReturn(problems);

        ProblemSetDTO result = problemService.findProblemsByUrlCode(VALID_CODE);
        Assert.assertEquals(problems.stream().map(ProblemMapper.INSTANCE::toDto).collect(Collectors.toList()),
                result.getProblems());
    }

    @Test
    public void shouldReturnNullWhenProblemSetNotFound() {
        when(problemSetRevisionRepository.findOneByShareCode(UrlCodeConverter.fromUrlCode(VALID_CODE)))
                .thenReturn(null);
        ProblemSetDTO result = problemService.findProblemsByUrlCode(VALID_CODE);
        Assert.assertNull(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenCodeIsInvalid() {
        problemService.findProblemsByUrlCode(INVALID_CODE);
    }
}
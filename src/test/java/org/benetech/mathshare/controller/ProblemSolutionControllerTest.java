package org.benetech.mathshare.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.benetech.mathshare.Application;
import org.benetech.mathshare.converters.UrlCodeConverter;
import org.benetech.mathshare.mappers.ProblemMapper;
import org.benetech.mathshare.mappers.SolutionMapper;
import org.benetech.mathshare.model.dto.ProblemDTO;
import org.benetech.mathshare.model.dto.SolutionDTO;
import org.benetech.mathshare.model.dto.SolutionStepDTO;
import org.benetech.mathshare.model.entity.ProblemSetRevision;
import org.benetech.mathshare.model.entity.ProblemSolution;
import org.benetech.mathshare.model.entity.SolutionRevision;
import org.benetech.mathshare.model.mother.ProblemMother;
import org.benetech.mathshare.model.mother.ProblemSolutionMother;
import org.benetech.mathshare.model.mother.SolutionRevisionMother;
import org.benetech.mathshare.model.mother.SolutionStepMother;
import org.benetech.mathshare.service.ProblemSolutionService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.stream.Collectors;

import static org.benetech.mathshare.model.mother.ProblemSetRevisionMother.INVALID_CODE;
import static org.benetech.mathshare.model.mother.ProblemSetRevisionMother.VALID_CODE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@SuppressWarnings("PMD.SignatureDeclareThrowsException")
public class ProblemSolutionControllerTest {

    private static final String BASE_ENDPOINT = "/solution/";

    private static final String REVISION_ENDPOINT = "/solution/revision/";

    private static final Long SHARE_CODE = 19L;

    private static final Long EDIT_CODE = 31L;

    private MockMvc mockMvc;

    @Autowired
    @InjectMocks
    @SuppressWarnings("PMD.UnusedPrivateField")
    private ProblemSolutionController problemSolutionController;

    @Mock
    private ProblemSolutionService problemSolutionService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        initMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void shouldReturn400IfCodeNotExist() throws Exception {
        when(problemSolutionService.findSolutionByUrlCode(VALID_CODE)).thenReturn(null);
        mockMvc.perform(getSolution(true))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn400IfCodeIsInWrongFormat() throws Exception {
        when(problemSolutionService.findSolutionByUrlCode(INVALID_CODE)).thenThrow(new IllegalArgumentException());
        mockMvc.perform(getSolution(false))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn200IfCodeWasFound() throws Exception {
        when(problemSolutionService.findSolutionByUrlCode(VALID_CODE)).thenReturn(new SolutionDTO());
        mockMvc.perform(getSolution(true))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnProblemSetDTOWithProblemsList() throws Exception {
        SolutionRevision solutionRevision = SolutionRevisionMother.mockInstance();
        ProblemSetRevision problemSetRevision = new ProblemSetRevision();
        ProblemDTO problem = ProblemMapper.INSTANCE.toDto(ProblemMother.validInstance(problemSetRevision));
        List<SolutionStepDTO> solutionStepList = SolutionStepMother.createValidStepsList(solutionRevision, 3).stream()
                .map(SolutionMapper.INSTANCE::toDto).collect(Collectors.toList());

        when(problemSolutionService.findSolutionByUrlCode(VALID_CODE)).thenReturn(
                new SolutionDTO(problem, solutionStepList, UrlCodeConverter.toUrlCode(EDIT_CODE)));
        String response = mockMvc.perform(getSolution(true))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        SolutionDTO result = new ObjectMapper().readValue(response, SolutionDTO.class);
        Assert.assertEquals(solutionStepList, result.getSteps());
    }

    @Test
    public void shouldReturn201IfCreated() throws Exception {
        ProblemSolution toSave = ProblemSolutionMother.mockInstance();
        when(problemSolutionService.saveNewVersionOfSolution(SolutionMapper.INSTANCE.toDto(toSave))).thenReturn(null);
        mockMvc.perform(createProblemSolution(SolutionMapper.INSTANCE.toDto(toSave)))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldReturnProblemSolutionWithShareAndEditCodes() throws Exception {
        ProblemSolution problemSolution = ProblemSolutionMother.mockInstance();
        SolutionRevision revision = SolutionRevisionMother.withShareCode(problemSolution, SHARE_CODE);
        ProblemSolution toSave = revision.getProblemSolution();
        toSave.setEditCode(EDIT_CODE);
        when(problemSolutionService.saveNewVersionOfSolution(any())).thenReturn(revision);

        String response = mockMvc.perform(createProblemSolution(SolutionMapper.INSTANCE.toDto(toSave)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        SolutionDTO result = new ObjectMapper().readValue(response, SolutionDTO.class);

        Assert.assertEquals(UrlCodeConverter.toUrlCode(EDIT_CODE), result.getEditCode());
        Assert.assertNotNull(UrlCodeConverter.toUrlCode(SHARE_CODE), result.getShareCode());
    }

    @Test
    public void putShouldReturn201IfCreated() throws Exception {
        ProblemSolution toSave = ProblemSolutionMother.mockInstance();
        when(problemSolutionService.createOrUpdateProblemSolution(any(), any())).thenReturn(Pair.of(true, SolutionRevisionMother.validInstance(toSave)));
        mockMvc.perform(createOrUpdateProblemSolution(SolutionMapper.INSTANCE.toDto(toSave)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
    }

    @Test
    public void putShouldReturn200IfUpdated() throws Exception {
        ProblemSolution toSave = ProblemSolutionMother.mockInstance();
        when(problemSolutionService.createOrUpdateProblemSolution(any(), any())).thenReturn(Pair.of(false, SolutionRevisionMother.validInstance(toSave)));
        mockMvc.perform(createOrUpdateProblemSolution(SolutionMapper.INSTANCE.toDto(toSave)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    }

    @Test
    public void shouldReturnProblemSetDTOForEditing() throws Exception {
        List<SolutionStepDTO> steps = SolutionStepMother.createValidStepsList(SolutionRevisionMother.mockInstance(), 3)
                .stream().map(SolutionMapper.INSTANCE::toDto).collect(Collectors.toList());

        ProblemDTO problem = ProblemMapper.INSTANCE.toDto(ProblemMother.mockInstance());

        when(problemSolutionService.getLatestProblemSolutionForEditing(VALID_CODE)).thenReturn(
                new SolutionDTO(problem, steps, UrlCodeConverter.toUrlCode(EDIT_CODE)));
        String response = mockMvc.perform(getLatestProblemSolution(true))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        SolutionDTO result = new ObjectMapper().readValue(response, SolutionDTO.class);
        Assert.assertEquals(steps, result.getSteps());
    }

    @Test
    public void shouldReturn400WhenFailedToParseProblemSolution() throws Exception {
        mockMvc.perform(post(BASE_ENDPOINT)
                .content("not a problem solution")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private static MockHttpServletRequestBuilder getSolution(boolean validCode) {
        return get(REVISION_ENDPOINT + (validCode ? VALID_CODE : INVALID_CODE));
    }

    private static MockHttpServletRequestBuilder getLatestProblemSolution(boolean validCode) {
        return get(BASE_ENDPOINT + (validCode ? VALID_CODE : INVALID_CODE));
    }

    private static MockHttpServletRequestBuilder createProblemSolution(SolutionDTO solution) throws JsonProcessingException {
        return post(BASE_ENDPOINT)
                .content(new ObjectMapper().writeValueAsString(solution))
                .contentType(MediaType.APPLICATION_JSON);
    }

    private static MockHttpServletRequestBuilder createOrUpdateProblemSolution(SolutionDTO solution) throws JsonProcessingException {
        return put(BASE_ENDPOINT + solution.getEditCode())
                .content(new ObjectMapper().writeValueAsString(solution))
                .contentType(MediaType.APPLICATION_JSON);
    }
}

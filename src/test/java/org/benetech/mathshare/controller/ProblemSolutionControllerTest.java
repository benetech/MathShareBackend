package org.benetech.mathshare.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.benetech.mathshare.Application;
import org.benetech.mathshare.converters.UrlCodeConverter;
import org.benetech.mathshare.mappers.SolutionMapper;
import org.benetech.mathshare.model.dto.SolutionDTO;
import org.benetech.mathshare.model.entity.ProblemSolution;
import org.benetech.mathshare.model.entity.SolutionRevision;
import org.benetech.mathshare.model.mother.ProblemSolutionMother;
import org.benetech.mathshare.model.mother.SolutionRevisionMother;
import org.benetech.mathshare.service.ProblemSolutionService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@SuppressWarnings({"PMD.SignatureDeclareThrowsException", "PMD.UnusedPrivateField"})
public class ProblemSolutionControllerTest {

    private static final String BASE_ENDPOINT = "/solution/";

    private static final String CREATE_ENDPOINT = BASE_ENDPOINT + "new/";

    private MockMvc mockMvc;

    @Autowired
    @InjectMocks
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
    public void shouldReturn201IfCreated() throws Exception {
        ProblemSolution toSave = ProblemSolutionMother.validInstance();
        when(problemSolutionService.saveNewVersionOfSolution(toSave)).thenReturn(null);
        mockMvc.perform(createProblemSet(SolutionMapper.INSTANCE.toDto(toSave)))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldReturnProblemSolutionWithShareAndEditCodes() throws Exception {
        long editCode = 49L;
        long shareCode = 10L;
        SolutionRevision revision = SolutionRevisionMother.withShareCode(shareCode);
        ProblemSolution toSave = revision.getProblemSolution();
        toSave.setEditCode(editCode);
        when(problemSolutionService.saveNewVersionOfSolution(any())).thenReturn(revision);

        String response = mockMvc.perform(createProblemSet(SolutionMapper.INSTANCE.toDto(toSave)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        SolutionDTO result = new ObjectMapper().readValue(response, SolutionDTO.class);

        Assert.assertEquals(UrlCodeConverter.toUrlCode(editCode), result.getEditCode());
        Assert.assertNotNull(UrlCodeConverter.toUrlCode(shareCode), result.getShareCode());
    }

    @Test
    public void shouldReturn400WhenFailedToParseProblemSet() throws Exception {
        mockMvc.perform(post(CREATE_ENDPOINT)
                .content("not a problem solution")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private static MockHttpServletRequestBuilder createProblemSet(SolutionDTO solution) throws JsonProcessingException {
        return post(CREATE_ENDPOINT)
                .content(new ObjectMapper().writeValueAsString(solution))
                .contentType(MediaType.APPLICATION_JSON);
    }
}

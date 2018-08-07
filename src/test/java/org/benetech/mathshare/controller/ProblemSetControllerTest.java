package org.benetech.mathshare.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.benetech.mathshare.Application;
import org.benetech.mathshare.converters.UrlCodeConverter;
import org.benetech.mathshare.mappers.ProblemMapper;
import org.benetech.mathshare.model.dto.ProblemDTO;
import org.benetech.mathshare.model.dto.ProblemSetDTO;
import org.benetech.mathshare.model.entity.ProblemSet;
import org.benetech.mathshare.model.entity.ProblemSetRevision;
import org.benetech.mathshare.model.mother.ProblemMother;
import org.benetech.mathshare.model.mother.ProblemSetMother;
import org.benetech.mathshare.model.mother.ProblemSetRevisionMother;
import org.benetech.mathshare.service.ProblemSetService;
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

import java.util.List;
import java.util.stream.Collectors;

import static org.benetech.mathshare.model.mother.ProblemSetRevisionMother.INVALID_CODE;
import static org.benetech.mathshare.model.mother.ProblemSetRevisionMother.VALID_CODE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@SuppressWarnings({"PMD.SignatureDeclareThrowsException", "PMD.UnusedPrivateField"})
public class ProblemSetControllerTest {

    private static final String BASE_ENDPOINT = "/set/";

    private static final String VIEW_ENDPOINT = BASE_ENDPOINT + "view/";

    private static final String CREATE_ENDPOINT = BASE_ENDPOINT + "new/";

    private MockMvc mockMvc;

    @Autowired
    @InjectMocks
    private ProblemSetController problemSetController;

    @Mock
    private ProblemSetService problemSetService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        initMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void shouldReturn400IfCodeNotExist() throws Exception {
        when(problemSetService.findProblemsByUrlCode(VALID_CODE)).thenReturn(null);
        mockMvc.perform(getProblemSet(true))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn400IfCodeIsInWrongFormat() throws Exception {
        when(problemSetService.findProblemsByUrlCode(INVALID_CODE)).thenThrow(new IllegalArgumentException());
        mockMvc.perform(getProblemSet(false))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn200IfCodeWasFound() throws Exception {
        when(problemSetService.findProblemsByUrlCode(VALID_CODE)).thenReturn(new ProblemSetDTO());
        mockMvc.perform(getProblemSet(true))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnProblemSetDTOWithProblemsList() throws Exception {
        List<ProblemDTO> problems = ProblemMother.createValidProblemsList(3).stream()
                .map(ProblemMapper.INSTANCE::toDto).collect(Collectors.toList());
        when(problemSetService.findProblemsByUrlCode(VALID_CODE)).thenReturn(new ProblemSetDTO(problems));
        String response = mockMvc.perform(getProblemSet(true))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        ProblemSetDTO result = new ObjectMapper().readValue(response, ProblemSetDTO.class);
        Assert.assertEquals(problems, result.getProblems());
    }

    @Test
    public void shouldReturn201IfCreated() throws Exception {
        ProblemSet toSave = ProblemSetMother.validInstance();
        when(problemSetService.saveNewVersionOfProblemSet(toSave)).thenReturn(null);
        mockMvc.perform(createProblemSet(ProblemMapper.INSTANCE.toDto(toSave)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
    }

    @Test
    public void shouldReturnProblemSetWithShareAndEditCodes() throws Exception {
        long editCode = 54L;
        long shareCode = 32L;
        ProblemSetRevision revision = ProblemSetRevisionMother.withShareCode(shareCode);
        ProblemSet toSave = revision.getProblemSet();
        toSave.setEditCode(editCode);
        when(problemSetService.saveNewVersionOfProblemSet(any())).thenReturn(revision);

        String response = mockMvc.perform(createProblemSet(ProblemMapper.INSTANCE.toDto(toSave)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        ProblemSetDTO result = new ObjectMapper().readValue(response, ProblemSetDTO.class);

        Assert.assertEquals(UrlCodeConverter.toUrlCode(editCode), result.getEditCode());
        Assert.assertEquals(UrlCodeConverter.toUrlCode(shareCode), result.getShareCode());
    }

    @Test
    public void shouldReturn400WhenFailedToParseProblemSet() throws Exception {
        mockMvc.perform(post(CREATE_ENDPOINT)
                .content("not a problem set object")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private static MockHttpServletRequestBuilder getProblemSet(boolean validCode) {
        return get(VIEW_ENDPOINT + (validCode ? VALID_CODE : INVALID_CODE));
    }

    private static MockHttpServletRequestBuilder createProblemSet(ProblemSetDTO problemSet) throws JsonProcessingException {
        return post(CREATE_ENDPOINT)
                .content(new ObjectMapper().writeValueAsString(problemSet))
                .contentType(MediaType.APPLICATION_JSON);
    }
}

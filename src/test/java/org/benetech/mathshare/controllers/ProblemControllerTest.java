package org.benetech.mathshare.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.benetech.mathshare.Application;
import org.benetech.mathshare.mappers.ProblemMapper;
import org.benetech.mathshare.model.dto.ProblemDTO;
import org.benetech.mathshare.model.dto.ProblemSetDTO;
import org.benetech.mathshare.model.mother.ProblemMother;
import org.benetech.mathshare.service.ProblemService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@SuppressWarnings({"PMD.SignatureDeclareThrowsException", "PMD.UnusedPrivateField"})
public class ProblemControllerTest {

    private static final String VIEW_ENDPOINT = "/set/view/";

    private MockMvc mockMvc;

    @Autowired
    @InjectMocks
    private ProblemController problemController;

    @Mock
    private ProblemService problemService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        initMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void shouldReturn400IfCodeNotExist() throws Exception {
        when(problemService.findProblemsByUrlCode(VALID_CODE)).thenReturn(null);
        mockMvc.perform(getProblemSet(true))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn400IfCodeIsInWrongFormat() throws Exception {
        when(problemService.findProblemsByUrlCode(INVALID_CODE)).thenThrow(new IllegalArgumentException());
        mockMvc.perform(getProblemSet(false))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn200IfCodeWasFound() throws Exception {
        when(problemService.findProblemsByUrlCode(VALID_CODE)).thenReturn(new ProblemSetDTO());
        mockMvc.perform(getProblemSet(true))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnProblemSetDTOWithProblemsList() throws Exception {
        List<ProblemDTO> problems = ProblemMother.createValidProblemsList(3).stream()
                .map(ProblemMapper.INSTANCE::toDto).collect(Collectors.toList());
        when(problemService.findProblemsByUrlCode(VALID_CODE)).thenReturn(new ProblemSetDTO(problems));
        String response = mockMvc.perform(getProblemSet(true))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        ProblemSetDTO result = new ObjectMapper().readValue(response, ProblemSetDTO.class);
        Assert.assertEquals(problems, result.getProblems());
    }

    private static MockHttpServletRequestBuilder getProblemSet(boolean validCode) {
        return get(VIEW_ENDPOINT + (validCode ? VALID_CODE : INVALID_CODE));
    }
}

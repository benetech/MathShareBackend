package org.benetech.mathshare.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Data
@NoArgsConstructor
@SuppressFBWarnings(value = "SIC_INNER_SHOULD_BE_STATIC_ANON", justification = "TypeReference<> is for Jackson")
public class SolutionSetDTO {

    private List<SolutionDTO> solutions = new ArrayList<>();

    private String reviewCode;

    private String editCode;

    private String title;

    private Map<String, String> metadata;

    private String archiveMode;

    public SolutionSetDTO(List<SolutionDTO> solutions, String reviewCode, String editCode, String title) {
        this.solutions = solutions;
        this.reviewCode = reviewCode;
        this.editCode = editCode;
        this.title = title;
    }

    public SolutionSetDTO(List<SolutionDTO> solutions, String reviewCode, String editCode, String title,
            String metadata) {
        this(solutions, reviewCode, editCode, title);
        this.deserializeAndSetMetadata(metadata);
    }

    public void deserializeAndSetMetadata(String metadata) {
        if (metadata == null) {
            return;
        }
        TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() { };
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.metadata = mapper.readValue(metadata, typeRef);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getSolutionUrl() {
        return System.getenv("CORS_ORIGIN") + "/#/app/problemSet/solve/" + this.editCode;
    }
}

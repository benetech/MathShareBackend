package org.benetech.mathshare.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolutionSetDTO {

    private List<SolutionDTO> solutions = new ArrayList<>();

    private String reviewCode;

    private String editCode;

    private String title;

    private String archiveMode;

    public SolutionSetDTO(List<SolutionDTO> solutions, String reviewCode, String editCode, String title) {
        this.solutions = solutions;
        this.reviewCode = reviewCode;
        this.editCode = editCode;
        this.title = title;
    }
}

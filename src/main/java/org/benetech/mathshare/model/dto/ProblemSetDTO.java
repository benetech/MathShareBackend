package org.benetech.mathshare.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProblemSetDTO {

    private List<ProblemDTO> problems;

    private String editCode;

    private String shareCode;

    private List<String> palettes;

    private String title;

    private String userId;

    private int problemCount;

    private String archiveMode;

    private Boolean optionalExplanations;

    private Boolean hideSteps;

    public ProblemSetDTO(
        List<ProblemDTO> problems, String editCode, String shareCode,
        List<String> palettes, String title, String userId, int problemCount
    ) {
        this.problems = problems;
        this.editCode = editCode;
        this.shareCode = shareCode;
        this.palettes = palettes;
        this.title = title;
        this.userId = userId;
        this.problemCount = problemCount;
    }

    public ProblemSetDTO(
        List<ProblemDTO> problems, String editCode, String shareCode,
        List<String> palettes, String title, String userId, int problemCount,
        Boolean optionalExplanations, Boolean hideSteps
    ) {
        this(problems, editCode, shareCode, palettes, title, userId, problemCount);
        this.hideSteps = hideSteps;
        this.optionalExplanations = optionalExplanations;
    }
}

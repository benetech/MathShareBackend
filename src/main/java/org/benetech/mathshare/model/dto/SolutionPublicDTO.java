package org.benetech.mathshare.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolutionPublicDTO {

    private ProblemDTO problem;

    private String shareCode;

    private String reviewCode;

    public SolutionPublicDTO(ProblemDTO problem) {
        this.problem = problem;
    }

    public SolutionPublicDTO(ProblemDTO problem, String reviewCode) {
        this(problem);
        this.reviewCode = reviewCode;
    }
}

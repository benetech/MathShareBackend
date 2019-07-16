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

    public SolutionPublicDTO(ProblemDTO problem) {
        this.problem = problem;
    }
}

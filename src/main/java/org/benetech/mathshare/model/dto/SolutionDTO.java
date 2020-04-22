package org.benetech.mathshare.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolutionDTO {

    private ProblemDTO problem;

    private String editCode;

    private String shareCode;

    private String problemSetSolutionEditCode;

    private List<SolutionStepDTO> steps = new ArrayList<>();

    private List<String> palettes;

    private Boolean finished = false;

    public SolutionDTO(ProblemDTO problem, List<SolutionStepDTO> steps, String editCode, List<String> palettes) {
        this.problem = problem;
        this.steps = steps;
        this.editCode = editCode;
        this.palettes = palettes;
    }

    public SolutionDTO(ProblemDTO problem, List<SolutionStepDTO> steps, String editCode, List<String> palettes,
    Boolean finished
    ) {
        this(problem, steps, editCode, palettes);
        this.finished = finished;
    }
}

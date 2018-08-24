package org.benetech.mathshare.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ProblemSetDTO {

    private List<ProblemDTO> problems;

    private String editCode;

    private String shareCode;

    public ProblemSetDTO(List<ProblemDTO> problems, String editCode, String shareCode) {
        this.problems = problems;
        this.shareCode = shareCode;
        this.editCode = editCode;
    }
}

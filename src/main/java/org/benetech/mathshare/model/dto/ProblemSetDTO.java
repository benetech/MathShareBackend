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

}

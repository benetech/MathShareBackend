package org.benetech.mathshare.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ProblemDTO {

    private Integer id;

    private String problemSetRevisionShareCode;

    private String editCode;

    private String text;

    private String title;

    private String scratchpad;

    private int position;

    private List<ProblemStepDTO> steps = new ArrayList<>();
}

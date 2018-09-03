package org.benetech.mathshare.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProblemDTO {

    private String problemSetRevisionShareCode;

    private String text;

    private String title;

    private String scratchpad;

    private int position;
}

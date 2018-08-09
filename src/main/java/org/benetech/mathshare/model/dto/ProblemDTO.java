package org.benetech.mathshare.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProblemDTO {

    private String problemSetEditCode;

    private String text;

    private String title;
}

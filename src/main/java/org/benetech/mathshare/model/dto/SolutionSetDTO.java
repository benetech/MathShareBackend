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
}

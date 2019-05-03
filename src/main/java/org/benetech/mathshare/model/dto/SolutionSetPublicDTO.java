package org.benetech.mathshare.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolutionSetPublicDTO {

    private List<SolutionPublicDTO> solutions = new ArrayList<>();
}

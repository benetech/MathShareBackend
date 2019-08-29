package org.benetech.mathshare.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {

    private String email;

    private String userType;

    private List<String> grades;

    private String role;
}

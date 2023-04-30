package com.sepidar.accounting.models.administrative_divisions;

import com.sepidar.accounting.constants.AdministrativeDivisionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class AdministrativeDivisionDTO {

    private Integer divisionId;
    private String title;
    private AdministrativeDivisionType type;
    private Integer parentDivisionRef;
}

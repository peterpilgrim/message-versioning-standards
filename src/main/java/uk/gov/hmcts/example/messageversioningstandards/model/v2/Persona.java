package uk.gov.hmcts.example.messageversioningstandards.model.v2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Persona {
    private String name;
    private String role;
    private Allegiance allegiance;
    private String note;
}

package uk.gov.hmcts.example.messageversioningstandards.model.v2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProductV2 {
    private String version;
    private String media;
    private String name;
    private String author;
    private String genre;
    private List<Persona> personas;

}

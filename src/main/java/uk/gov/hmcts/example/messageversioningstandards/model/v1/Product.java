package uk.gov.hmcts.example.messageversioningstandards.model.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Product {
    private String media;
    private String name;
    private String author;
    private String genre;
    private Map<String,Attributes> personas;
}

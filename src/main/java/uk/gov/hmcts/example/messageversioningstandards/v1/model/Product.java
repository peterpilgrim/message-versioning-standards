package uk.gov.hmcts.example.messageversioningstandards.v1.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/*
        {
            "media": "Book",
            "name": "Consider Phlebas",
            "author": "Iain M Banks",
            "genre": "Science Fiction",
            "personas": {
                "Horza": { "attributes": [ "Changer", "Idiran Empire" ]},
                "Perosteck Balveda": { "attributes": [ "Operative", "The Culture" ]}
            }
        }
 */
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

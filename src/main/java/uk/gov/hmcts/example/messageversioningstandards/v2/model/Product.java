package uk.gov.hmcts.example.messageversioningstandards.v2.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/*
{
	"version": "2.0.0",
	"media": "Book",
    "name": "Consider Phlebas",
	"author": "Iain M Banks",
	"genre": "Science Fiction",
	"personas": [
		{"name": "Horza", "role": "Changer", "allegience": "Idiran Empire"},
		{"name": "Perosteck Balveda", "role": "Operative", "allegience": "The Culture" }
	]
}
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Product {
    private String version;
    private String media;
    private String name;
    private String author;
    private String genre;
    private List<Persona> personas;

}

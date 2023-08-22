package uk.gov.hmcts.example.messageversioningstandards.v2.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
{
	"version": "2.0.0",
	"media": "Book",
    "name": "Consider Phlebas",
	"author": "Iain M Banks",
	"genre": "Science Fiction",
	"personas": [
		{"name": "Horza", "role": "Changer", "allegiance": "Idiran Empire"},
		{"name": "Perosteck Balveda", "role": "Operative", "allegiance": "The Culture" }
	]
}
 */


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

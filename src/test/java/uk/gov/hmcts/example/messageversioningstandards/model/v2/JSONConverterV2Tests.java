package uk.gov.hmcts.example.messageversioningstandards.model.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class JSONConverterV2Tests {

    @DisplayName("should convert Product V2 to JSON")
    @Test
    public void convertProductV1toJson() throws IOException {
        var objectMapper = new ObjectMapper();

        var product = ProductV2.builder().media("Book")
                .version("2.0.0")
                .name("Consider Phlebas").author("Iain M Banks").genre("Science Fiction")
                .personas(
                        List.of(
                                Persona.builder()
                                        .name("Horza")
                                        .allegiance(Allegiance.IDIRAN)
                                        .role("Changer")
                                        .build(),
                                Persona.builder()
                                        .name("Perosteck Balveda")
                                        .allegiance(Allegiance.CULTURE)
                                        .role("Operative")
                                        .build()
                        )
                )
                .build();

        var swriter = new StringWriter();
        objectMapper.writeValue(swriter, product);
        var jsonDataSourceString = swriter.toString();

//        System.out.printf("jsonDataSourceString=%s\n",jsonDataSourceString);

        DocumentContext jsonContext = JsonPath.parse(jsonDataSourceString);

        assertThat( jsonContext.read("$.version"), is("2.0.0"));
        assertThat( jsonContext.read("$.media"), is("Book"));
        assertThat( jsonContext.read("$.name"), is("Consider Phlebas"));
        assertThat( jsonContext.read("$.author"), is("Iain M Banks"));
        assertThat( jsonContext.read("$.genre"), is("Science Fiction"));
        assertThat( jsonContext.read("$.personas[0].name"), is("Horza"));
        assertThat( jsonContext.read("$.personas[0].allegiance"), is("IDIRAN"));
        assertThat( jsonContext.read("$.personas[0].role"), is("Changer"));
        assertThat( jsonContext.read("$.personas[1].name"), is("Perosteck Balveda"));
        assertThat( jsonContext.read("$.personas[1].allegiance"), is("CULTURE"));
        assertThat( jsonContext.read("$.personas[1].role"), is("Operative"));
    }

    @DisplayName("should convert JSON to Product V2")
    @Test
    public void convertJsonToProductV1() throws IOException {
        var objectMapper = new ObjectMapper();
        var json = """
               {
                     "version": "2.0.1",
                     "media": "Book",
                     "name": "The Player of Games",
                     "author": "Iain M Banks",
                     "genre": "Science Fiction",
                     "personas":
                     [
                         {
                             "name": "Jernau Morat Gurgeh",
                             "role": "Board Game Player",
                             "allegiance": "UNALIGNED",
                             "note": "Chiark Orbital Citizen"
                         },
                         {
                             "name": "Mawhrin-Skel",
                             "role": "Drone",
                             "allegiance": "CULTURE",
                             "note": "Special Circumstance"
                         }
                     ]
               }
                """;
        var product = objectMapper.readValue( json, ProductV2.class);
        assertThat( product, is(notNullValue()));
        assertThat( product.getVersion(), is("2.0.1"));
        assertThat( product.getMedia(), is("Book"));
        assertThat( product.getAuthor(), is("Iain M Banks"));
        assertThat( product.getGenre(), is("Science Fiction"));
        assertThat( product.getName(), is("The Player of Games"));
        assertThat( product.getPersonas().size(), is(2));
        assertThat( product.getPersonas(), is(
                List.of(
                        Persona.builder()
                                .name("Jernau Morat Gurgeh")
                                .allegiance(Allegiance.UNALIGNED)
                                .role("Board Game Player")
                                .note("Chiark Orbital Citizen")
                                .build(),
                        Persona.builder()
                                .name("Mawhrin-Skel")
                                .allegiance(Allegiance.CULTURE)
                                .role("Drone")
                                .note("Special Circumstance")
                                .build()
                )));
    }

}

package uk.gov.hmcts.example.messageversioningstandards.model.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.example.messageversioningstandards.model.v1.Attributes;
import uk.gov.hmcts.example.messageversioningstandards.model.v1.Product;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

public class JSONConverterV1Tests {

    @DisplayName("should convert Product V1 to JSON")
    @Test
    public void convertProductV1toJson() throws IOException {
        var objectMapper = new ObjectMapper();

        var product = Product.builder().media("Book")
                .name("Consider Phlebas").author("Iain M Banks").genre("Science Fiction")
                .personas(
                        Map.of(
                                "Horza",
                                    Attributes.builder().attributes(List.of("Changer", "Idiran Empire")).build(),
                                "Perosteck Balveda",
                                    Attributes.builder().attributes(List.of("Operative", "The Culture")).build())
                )
                .build();

        var swriter = new StringWriter();
        objectMapper.writeValue(swriter, product);
        var jsonDataSourceString = swriter.toString();

//        System.out.printf("jsonDataSourceString=%s\n",jsonDataSourceString);

        DocumentContext jsonContext = JsonPath.parse(jsonDataSourceString);

        assertThat( jsonContext.read("$.media"), is("Book"));
        assertThat( jsonContext.read("$.name"), is("Consider Phlebas"));
        assertThat( jsonContext.read("$.author"), is("Iain M Banks"));
        assertThat( jsonContext.read("$.genre"), is("Science Fiction"));
        assertThat( jsonContext.read("$.personas.Horza.attributes[0]"), is("Changer"));
        assertThat( jsonContext.read("$.personas.Horza.attributes[1]"), is("Idiran Empire"));
        assertThat( jsonContext.read("$.personas.['Perosteck Balveda'].attributes[0]"), is("Operative"));
        assertThat( jsonContext.read("$.personas.['Perosteck Balveda'].attributes[1]"), is("The Culture"));
    }

    @DisplayName("should convert JSON to Product V1")
    @Test
    public void convertJsonToProductV1() throws IOException {
        var objectMapper = new ObjectMapper();
        var json = """
                {
                    "media":"Book","name":"The Player of Games","author":"Iain M Banks","genre":"Science Fiction",
                    "personas":{
                        "Jernau Morat Gurgeh":{"attributes":["Board Game Player","Chiark Orbital Citizen"]},
                        "Mawhrin-Skel":{"attributes":["Drone","Special Circumstance", "The Culture"]}
                    }
                }
                """;
        var product = objectMapper.readValue( json, Product.class);
        assertThat( product, is(notNullValue()));
        assertThat( product.getMedia(), is("Book"));
        assertThat( product.getAuthor(), is("Iain M Banks"));
        assertThat( product.getGenre(), is("Science Fiction"));
        assertThat( product.getName(), is("The Player of Games"));
        assertThat( product.getPersonas().size(), is(2));
        assertThat( product.getPersonas(), is(
                Map.of(
                "Jernau Morat Gurgeh",
                Attributes.builder().attributes(List.of("Board Game Player", "Chiark Orbital Citizen")).build(),
                "Mawhrin-Skel",
                Attributes.builder().attributes(List.of("Drone", "Special Circumstance", "The Culture")).build())
        ));
    }

}

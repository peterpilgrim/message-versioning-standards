package uk.gov.hmcts.example.messageversioningstandards;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.example.messageversioningstandards.v1.model.Attributes;
import uk.gov.hmcts.example.messageversioningstandards.v1.model.Product;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Map;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

public class JSONConverterTests {

    @DisplayName("should convert Product v1 to JSON")
    @Test
    public void convertProductV1toJson() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        var product = Product.builder().media("Book")
                .name("Consider Phlebas").author("Iain M Banks").genre("Science Fiction")
                .personas(
                        Map.of(
                                "Horza",
                                    Attributes.builder().attributes(Arrays.asList("Changer", "Idiran Empire")).build(),
                                "Perosteck Balveda",
                                    Attributes.builder().attributes(Arrays.asList("Operative", "The Culture")).build())
                )
                .build();

        var swriter = new StringWriter();
        objectMapper.writeValue(swriter, product);
        var jsonDataSourceString = swriter.toString();

        System.out.printf("jsonDataSourceString=%s\n",jsonDataSourceString);

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
}

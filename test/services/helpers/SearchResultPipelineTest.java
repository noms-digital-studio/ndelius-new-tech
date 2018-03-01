package services.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import play.libs.Json;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class SearchResultPipelineTest {

    private ObjectMapper mapper;
    private ObjectNode rootNode;
    private Map<String, Map.Entry<Function<ObjectNode, Optional<JsonNode>>, BiFunction<ObjectNode, JsonNode, ObjectNode>>> pipeline;

    @Before
    public void setup() {
        mapper = Json.mapper();
        rootNode = mapper.createObjectNode();
        pipeline = SearchResultPipeline.create(Function.identity(), "", "", ImmutableMap.of());
    }

    @Test
    public void appendsAgeIfDateOfBirthPresent() {

        rootNode.put("dateOfBirth", "2018-02-28");
        ObjectNode newNode = SearchResultPipeline.process(rootNode, ImmutableList.of(pipeline.get("addAgeAndHighlightDob")));
        assertThat(newNode.get("age")).isNotNull();
    }

    @Test
    public void doesNotAppendAgeIfDateOfBirthNotPresent() {
        ObjectNode newNode = SearchResultPipeline.process(rootNode, ImmutableList.of(pipeline.get("addAgeAndHighlightDob")));
        assertThat(newNode.get("age")).isNull();
    }

    @Test
    public void appendsOneTimeNomisRefIfNomsNumberPresent() {
        ObjectNode nomsNode = mapper.createObjectNode();
        nomsNode.put("nomsNumber", "A123");
        rootNode.set("otherIds", nomsNode);
        ObjectNode newNode = SearchResultPipeline.process(rootNode, ImmutableList.of(pipeline.get("addOneTimeNomisRef")));
        assertThat(newNode.get("oneTimeNomisRef")).isNotNull();
        System.out.println(newNode.get("oneTimeNomisRef"));
        assertThat(newNode.get("oneTimeNomisRef").asText()
            .startsWith("{\"user\":\"unknown\",\"noms\":\"A123\",\"tick\"")).isTrue();
    }

    @Test
    public void doesNotAppendsOneTimeNomisRefIfnomsNumberNotPresent() {
        ObjectNode nomsNode = mapper.createObjectNode();
        nomsNode.put("pncNumber", "2018/12345");
        rootNode.set("otherIds", nomsNode);
        ObjectNode newNode = SearchResultPipeline.process(rootNode, ImmutableList.of(pipeline.get("addOneTimeNomisRef")));
        assertThat(newNode.get("oneTimeNomisRef")).isNull();
    }
}
package services.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import helpers.JsonHelper;
import helpers.JwtHelper;
import lombok.val;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import play.libs.Json;

import java.time.Instant;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static helpers.DateTimeHelper.calculateAge;
import static java.time.Clock.systemUTC;
import static helpers.DateTimeHelper.termsThatLookLikeDates;

public interface SearchResultPipeline {

    static Map<String, Map.Entry<Function<ObjectNode, Optional<JsonNode>>, BiFunction<ObjectNode, JsonNode, ObjectNode>>> create(
            Function<String, String> encrypter,
            String bearerToken,
            String searchTerm,
            Map<String, HighlightField> highlightsMap
    ) {
        final BiFunction<String, Long, String> oneTimeNomisRef = (nomisId, tick) -> {

            val reference = ImmutableMap.of(                        // Creates a limited-time reference to the nomisId number as
                    "user", JwtHelper.principal(bearerToken),   // a string that can only be used by the same user within a
                    "noms", nomisId,                            // limited time frame. This allows safe access to a NomisId
                    "tick", tick                                // only by a User that has already had Offender canAccess() checked
            );

            return encrypter.apply(JsonHelper.stringify(reference));
        };

        val highlightFields = highlightsMap.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> Arrays.stream(entry.getValue().fragments()).map(Text::string).collect(Collectors.toList()))
        );

        return ImmutableMap.of(

                "addHighlightFields", new AbstractMap.SimpleEntry<>(

                        source -> Optional.of(source.nullNode()),
                        (result, ignored) -> (ObjectNode) result.set("highlight", Json.toJson(highlightFields))
                ),

                "addAgeAndHighlightDob", new AbstractMap.SimpleEntry<>(

                        source -> Optional.ofNullable(source.get("dateOfBirth")),
                        (result, jsonNode) -> {

                            val dateOfBirth = jsonNode.asText();

                            if (termsThatLookLikeDates(searchTerm).stream().anyMatch(date -> date.equals(dateOfBirth))) {

                                val highlightList = Json.toJson(ImmutableList.of(dateOfBirth));

                                ((ObjectNode) result.get("highlight")).set("dateOfBirth", highlightList);
                            }

                            return result.put("age", calculateAge(dateOfBirth, systemUTC()));
                        }
                ),

                "addOneTimeNomisRef", new AbstractMap.SimpleEntry<>(

                        source -> Optional.ofNullable(source.get("otherIds")).flatMap(otherIds -> Optional.ofNullable(otherIds.get("nomsNumber"))),
                        (result, nomsNumber) -> result.put("oneTimeNomisRef", oneTimeNomisRef.apply(nomsNumber.asText(), Instant.now().toEpochMilli()))
                )
        );
    }

    static ObjectNode process(ObjectNode rootNode, Iterable<Map.Entry<Function<ObjectNode, Optional<JsonNode>>, BiFunction<ObjectNode, JsonNode, ObjectNode>>> pipeline) {

        for (val entry : pipeline) {

            val objectNode = rootNode;
            rootNode = entry.getKey().apply(objectNode).map(jsonNode -> entry.getValue().apply(objectNode, jsonNode)).orElse(rootNode);
        }

        return rootNode;
    }
}

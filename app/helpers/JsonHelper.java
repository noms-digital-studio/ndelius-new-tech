package helpers;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.Map;
import lombok.val;
import play.libs.Json;
import play.mvc.Result;

import static com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT;
import static com.fasterxml.jackson.databind.DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS;
import static play.mvc.Http.Status.SERVICE_UNAVAILABLE;
import static play.mvc.Results.badRequest;
import static play.mvc.Results.ok;
import static play.mvc.Results.status;

public interface JsonHelper {

    static Map<String, String> jsonToMap(String json) {

        return jsonToMap(Json.parse(json));
    }

    static Map<String, String> jsonToMap(JsonNode json) {

        // mapper that allows array to be serialised as a String
        val mapper = Json.newDefaultMapper()
                .configure(UNWRAP_SINGLE_VALUE_ARRAYS, true)
                .configure(ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);

        try {
            return mapper.readValue(
                    mapper.treeAsTokens(json),
                    mapper.getTypeFactory().constructMapType(Map.class, String.class, String.class)
            );
        } catch (IOException ex) {

            return null;
        }
    }

    static <T> Result okJson(T data) {

        return ok(Json.toJson(data));
    }

    static <T> Result serverUnavailableJson(T data) {
        return status(SERVICE_UNAVAILABLE, Json.toJson(data));
    }

    static <T> Result badRequestJson(T data) {
        return badRequest(Json.toJson(data));
    }

    static <T> String stringify(T data) {

        return Json.stringify(Json.toJson(data));
    }

    static <T> T readValue(String json, Class<T> clazz) {

        return Json.fromJson(Json.parse(json), clazz);
    }
}

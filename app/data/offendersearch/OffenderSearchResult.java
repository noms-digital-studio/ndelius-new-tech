package data.offendersearch;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.List;

import static java.util.Collections.emptyList;

@Data
public class OffenderSearchResult {

    private List<JsonNode> offenders = emptyList();
    private List<String> suggestions = emptyList();
    private long total;
}

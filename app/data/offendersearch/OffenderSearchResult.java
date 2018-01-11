package data.offendersearch;

import lombok.Data;

import java.util.List;

import static java.util.Collections.emptyList;

@Data
public class OffenderSearchResult {

    private List<OffenderSummary> offenders = emptyList();
    private List<String> suggestions = emptyList();
}

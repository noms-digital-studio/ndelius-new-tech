package data.offendersearch;

import lombok.Data;

import java.util.List;

@Data
public class OffenderSearchResult {
    private List<OffenderDetails> offenders;
}

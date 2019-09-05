package tfidf.labeling;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Label {
    @JsonProperty
    private final String label;
    @JsonProperty
    private final String id;

    @JsonCreator
    public Label(@JsonProperty("label") String label, @JsonProperty("id") String id) {
        this.label = label;
        this.id = id;
    }

}

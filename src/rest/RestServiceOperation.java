package rest;

import lombok.Builder;
import lombok.Data;
import models.ModeledParam;

import java.util.List;

@Data
@Builder
public class RestServiceOperation {
    private final String operationName;
    private final List<ModeledParam> parameters;
}

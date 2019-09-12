package wsdl;

import lombok.Builder;
import lombok.Data;
import models.ModeledParam;
import rest.HttpMethod;

import java.util.List;

@Data
@Builder
public class ServiceOperation {
    private final String operationName;
    private final HttpMethod httpMethod;
    private final List<ModeledParam> parameters;
    private final String path;
    private final String host;
}

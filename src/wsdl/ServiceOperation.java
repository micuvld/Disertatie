package wsdl;

import lombok.Builder;
import lombok.Data;
import rest.HttpMethod;

import java.util.List;

@Data
@Builder
public class ServiceOperation {
    private final String operationName;
    private final HttpMethod httpMethod;
    private final List<OperationParameter> parameters;
}

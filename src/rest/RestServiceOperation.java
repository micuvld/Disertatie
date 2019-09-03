package rest;

import lombok.Builder;
import lombok.Data;
import wsdl.OperationParameter;

import java.util.List;

@Data
@Builder
public class RestServiceOperation {
    private final String operationName;
    private final List<OperationParameter> parameters;
}

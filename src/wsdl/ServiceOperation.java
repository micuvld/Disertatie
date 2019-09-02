package wsdl;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ServiceOperation {
    private final String operationName;
    private final List<OperationParameter> parameters;
}

package wsdl;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class WsdlPojo {
    private final String serviceName;
    private final String serviceDescription;
    private final List<ServiceOperation> operations;

    @Override
    public String toString() {
        return String.format("Service Name: %s\n" +
                        "Service Description: %s\n" +
                        "Service Operations: \n%s\n",
                serviceName,
                serviceDescription,
                operations.stream()
                        .map(operation -> operation.getOperationName() + ":" + operation.getParameters().stream()
                                .map(param -> param.getDirection() + "::" + param.getName())
                                .collect(Collectors.joining("-")))
                        .collect(Collectors.joining("\n")));
    }
}

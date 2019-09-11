package rest;

import lombok.Builder;
import lombok.Data;
import models.ModeledOntologyDevice;
import wsdl.WsdlPojo;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class RestInterface {
    private String endpoint;
    private List<RestMethod> methods;

    public static RestInterface fromModeledDevice(ModeledOntologyDevice modeledOntologyDevice, WsdlPojo wsdlPojo) {
        return RestInterface.builder()
                .endpoint(wsdlPojo.getEndpoint())
                .methods(getRestMethods(modeledOntologyDevice))
                .build();
    }

    private static List<RestMethod> getRestMethods(ModeledOntologyDevice modeledOntologyDevice) {
        List<RestMethod> restMethods = modeledOntologyDevice.getPrimaryMethods().stream()
                .map(RestMethod::fromModeledMethod)
                .collect(Collectors.toList());
        restMethods.addAll(modeledOntologyDevice.getSecondaryMethods().stream()
                .map(RestMethod::fromModeledMethod)
                .collect(Collectors.toList()));
        return restMethods;
    }

    @Override
    public String toString() {
        return String.format("Host: %s\n" +
                "Methods: \n\n%s",
                endpoint,
                methods.stream().map(RestMethod::toString).collect(Collectors.joining("\n-----\n")));
    }
}

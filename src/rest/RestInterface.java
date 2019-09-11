package rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import models.ModeledOntologyDevice;
import wsdl.WsdlPojo;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class RestInterface {
    @JsonProperty("endpoint")
    private String endpoint;
    @JsonProperty("methods")
    private List<RestMethod> methods;

    @JsonCreator
    public RestInterface(@JsonProperty("endpoint") String endpoint,
                         @JsonProperty("methods") List<RestMethod> methods) {
        this.endpoint = endpoint;
        this.methods = methods;
    }

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

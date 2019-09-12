package rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.Builder;
import lombok.Data;
import models.ModeledOntologyDevice;
import wsdl.WsdlPojo;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class RestInterface {
    @JsonProperty("deviceLabel")
    private String deviceLabel;
    @JsonProperty("endpoint")
    private String endpoint;
    @JsonProperty("methods")
    private List<RestMethod> methods;

    @JsonCreator
    public RestInterface(@JsonProperty("deviceLabel") String deviceLabel,
                         @JsonProperty("endpoint") String endpoint,
                         @JsonProperty("methods") List<RestMethod> methods) {
        this.deviceLabel = deviceLabel;
        this.endpoint = endpoint;
        this.methods = methods;
    }

    public static RestInterface fromModeledDevice(ModeledOntologyDevice modeledOntologyDevice, WsdlPojo wsdlPojo) {
        return RestInterface.builder()
                .deviceLabel(modeledOntologyDevice.getDeviceLabel())
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
        return String.format("Device Label: %s\n" +
                        "Host: %s\n" +
                        "Methods: \n\n%s",
                deviceLabel,
                endpoint,
                methods.stream().map(RestMethod::toString).collect(Collectors.joining("\n-----\n")));
    }
}

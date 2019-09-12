package rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.xml.internal.ws.util.StringUtils;
import lombok.Builder;
import lombok.Data;
import models.ModeledClass;
import models.ModeledMethod;
import models.ModeledParam;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class RestMethod {
    //shown to the user in UI, in order to briefly describe the method
    @JsonProperty("label")
    private String label;
    @JsonProperty("httpMethod")
    private HttpMethod httpMethod;
    @JsonProperty("name")
    private String name;
    @JsonProperty("params")
    private List<ModeledParam> params;
    @JsonProperty("path")
    private String path;
    @JsonProperty("host")
    private String host;

    @JsonCreator
    public RestMethod(
            @JsonProperty("label") String label,
            @JsonProperty("httpMethod") HttpMethod httpMethod,
            @JsonProperty("name") String name,
            @JsonProperty("params") List<ModeledParam> params,
            @JsonProperty("path") String path,
            @JsonProperty("host") String host) {
        this.label = label;
        this.httpMethod = httpMethod;
        this.name = name;
        this.params = params;
        this.path = path;
        this.host = host;
    }

    public static RestMethod fromModeledMethod(ModeledMethod method) {
        return RestMethod.builder()
                .httpMethod(method.getHttpMethod())
                .label(buildLabel(method.getClasses()))
                .name(method.getName())
                .params(method.getParams())
                .path(method.getPath())
                .host(method.getHost())
                .build();
    }

    //["get", "physical_quantity"] -> "Get Physical Quantity"
    private static String buildLabel(List<ModeledClass> classes) {
        return classes.stream()
                .map(cls -> Arrays.stream(cls.getClassName().split("_"))
                        .map(StringUtils::capitalize)
                        .collect(Collectors.joining(" ")))
                .collect(Collectors.joining(" "));
    }

    @Override
    public String toString() {
        return String.format("Label: %s\n" +
                        "Name: %s\n" +
                        "Host: %s\n" +
                        "Request Line: %s %s HTTP/1.1\n" +
                        "Parameters: \n%s",
                label,
                name,
                host,
                httpMethod,
                path,
                params.stream()
                        .map(ModeledParam::toString)
                        .collect(Collectors.joining("\n")));
    }
}

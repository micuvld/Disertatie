package rest;

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
    private String label;
    private HttpMethod httpMethod;
    private String path;
    private List<ModeledParam> params;

    @Data
    public static class RestParam {
        private String name;
        private String type;
    }

    public static RestMethod fromModeledMethod(ModeledMethod method) {
        return RestMethod.builder()
                .httpMethod(method.getHttpMethod())
                .label(buildLabel(method.getClasses()))
                .path(method.getName())
                .params(method.getParams())
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
        return String.format("Label:%s\n" +
                        "Request Header: %s /%s HTTP/1.1\n" +
                        "Parameters: \n%s",
                label,
                httpMethod,
                path,
                params.stream()
                        .map(ModeledParam::toString)
                        .collect(Collectors.joining("\n")));
    }
}

package ontologies;

import lombok.Builder;
import lombok.Data;
import rest.HttpMethod;

import java.util.List;

@Data
public class ModeledMethod {
    private HttpMethod httpMethod;
    private List<ModeledClass> classes;
    private List<ModeledParam> params;
}

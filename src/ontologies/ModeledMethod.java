package ontologies;

import lombok.Builder;
import lombok.Data;
import rest.HttpMethod;

import java.util.ArrayList;
import java.util.List;

@Data
public class ModeledMethod {
    private MethodType methodType;
    private HttpMethod httpMethod;
    private List<ModeledClass> classes;
    private List<ModeledParam> params;

    public ModeledMethod () {
        this.classes = new ArrayList<>();
    }

    public void addClass(ModeledClass cls) {
        this.classes.add(cls);
    }
}

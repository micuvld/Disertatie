package models;

import lombok.Data;
import rest.HttpMethod;

import java.util.ArrayList;
import java.util.List;

@Data
public class ModeledMethod {
    private String name;
    private HttpMethod httpMethod;
    private List<ModeledClass> classes;
    private List<ModeledParam> params;
    private String path;
    private String host;

    public ModeledMethod () {
        this.classes = new ArrayList<>();
        this.params = new ArrayList<>();
    }

    public void addClass(ModeledClass cls) {
        this.classes.add(cls);
    }
    public void addParam(ModeledParam param) {this.params.add(param); }

    public boolean matchesWith(ModeledMethod methodToMatch) {
        if (!this.httpMethod.equals(methodToMatch.httpMethod)) {
            return false;
        }

        if (!(this.classes.containsAll(methodToMatch.classes)
                && methodToMatch.classes.containsAll(this.classes))) {
            return false;
        }

        //to match params
        if (!(this.params.containsAll(methodToMatch.params)
                && methodToMatch.params.containsAll(this.params))) {
            return false;
        }

        return true;
    }
}

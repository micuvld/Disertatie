package models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ModeledOntologyDevice {
    private String deviceLabel;
    //primary methods: methods that are mandatory to be present
    private List<ModeledMethod> primaryMethods;
    //secondary methods: methods that can be integrated with human assistance
    private List<ModeledMethod> secondaryMethods;

    public ModeledOntologyDevice() {
        this.primaryMethods = new ArrayList<>();
        this.secondaryMethods = new ArrayList<>();
    }

    public void addPrimaryMethod(ModeledMethod modeledMethod) {
        this.primaryMethods.add(modeledMethod);
    }

    public void addSecondaryMethod(ModeledMethod modeledMethod) {
        this.secondaryMethods.add(modeledMethod);
    }

}

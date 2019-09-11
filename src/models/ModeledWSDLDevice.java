package models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ModeledWSDLDevice {
    private List<ModeledMethod> methods = new ArrayList<>();

    public ModeledWSDLDevice() {
        this.methods = new ArrayList<>();
    }

    public void addMethod(ModeledMethod modeledMethod) {
        this.methods.add(modeledMethod);
    }
}

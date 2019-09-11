package rest;

import lombok.Data;
import models.ModeledParam;

import java.util.List;

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
}

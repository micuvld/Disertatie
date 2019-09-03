package rest;

import org.semanticweb.owlapi.model.OWLClass;

public enum RestOperation {
    GET_VALUE("get_value", HttpMethod.GET),
    CONFIGURE("configure", HttpMethod.PUT);

    String owlClass;
    HttpMethod httpMethod;

    public String getOwlClass() {
        return this.owlClass;
    }

    public HttpMethod getHttpMethod() {
        return this.httpMethod;
    }

    public RestOperation fromOwlClass(String owlClass) {
        for (RestOperation restOperation : values()) {
            if (owlClass.equals(restOperation.getOwlClass())) {
                return restOperation;
            }
        }

        return null;
    }

    RestOperation(String owlClass, HttpMethod httpMethod) {
        this.owlClass = owlClass;
        this.httpMethod = httpMethod;
    }
}

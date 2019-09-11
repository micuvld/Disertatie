package models;

import org.semanticweb.owlapi.model.OWLDatatype;

public enum ParamType {
    INTEGER,
    DOUBLE,
    STRING;

    public static ParamType fromOwlDatatype(OWLDatatype owlDatatype) {
        if (owlDatatype.isDouble()) {
            return DOUBLE;
        }

        if (owlDatatype.isInteger()) {
            return INTEGER;
        }

        if (owlDatatype.isString()) {
            return STRING;
        }

        return null;
    }

    public static ParamType fromXmlDataType(String xmlDataType) {
        switch(xmlDataType) {
            case "double":
            return DOUBLE;
            case "integer":
                return INTEGER;
            case "string":
                return STRING;
            default:
                return null;
        }
    }
}
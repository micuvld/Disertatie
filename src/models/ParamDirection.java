package models;

import org.apache.woden.wsdl20.enumeration.Direction;

public enum ParamDirection {
    IN("inParam"),
    OUT("outParam");

    private String ontologyProperty;

    ParamDirection(String ontologyProperty) {
        this.ontologyProperty = ontologyProperty;
    }

    public static ParamDirection fromOntologyProperty(String ontologyProperty) {
        for (ParamDirection direction : ParamDirection.values()) {
            if (direction.ontologyProperty.equals(ontologyProperty)) {
                return direction;
            }
        }

        return null;
    }

    public static ParamDirection fromXmlDirection(Direction xmlDirection) {
        return xmlDirection.equals(Direction.IN) ? ParamDirection.IN : ParamDirection.OUT;
    }
}
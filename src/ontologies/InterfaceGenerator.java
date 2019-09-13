package ontologies;

import exceptions.InvalidIndividualException;
import exceptions.PrimaryMethodsNotMatchingException;
import models.*;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import rest.HttpMethod;
import wsdl.WsdlPojo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class InterfaceGenerator {
    private final String baseIri;
    private final Reasoner reasoner;
    private final OWLOntology ontology;
    private final OWLDataFactory dataFactory;

    public InterfaceGenerator(String baseIri, String ontologyLocation) throws OWLOntologyCreationException {
        this.baseIri = baseIri;
        OWLOntologyManager owlOntologyManager = OWLManager.createOWLOntologyManager();
        ontology = owlOntologyManager.loadOntologyFromOntologyDocument(new File(ontologyLocation));
        this.dataFactory = owlOntologyManager.getOWLDataFactory();
        this.reasoner = new Reasoner(ontology);
    }

    public ModeledOntologyDevice getMatchingModel(WsdlPojo wsdlPojo, String deviceLabel)
            throws PrimaryMethodsNotMatchingException, InvalidIndividualException {
        ModeledOntologyDevice matchingModel;
        OWLIndividual owlIndividual = dataFactory.getOWLNamedIndividual(IRI.create(baseIri, deviceLabel));

        //find the device types
        //devices have a single type that may be a single class or a conjunction of multiple classes
        //if the type is a conjunction of multiple classes, it means that we have a complex device built from
        //functionalities of multiple basic devices
        //the reasoner will convert the ObjectIntersectionOf into a set of classes
        Set<OWLClass> deviceTypes = reasoner.getTypes(owlIndividual.asOWLNamedIndividual(), true).getFlattened();
        if (!deviceTypes.iterator().hasNext()) {
            throw new InvalidIndividualException();
        }

        ModeledOntologyDevice expectedModel = new ModeledOntologyDevice();
        //go through each device class and gather the primary and secondary methods
        for (OWLClassExpression deviceClass : deviceTypes) {
            //equivalent classes are the methods of the device

            for (OWLClassExpression eqCls : deviceClass.asOWLClass().getEquivalentClasses(ontology)) {
                //create the expected model using the ontology
                appendMethods(eqCls, expectedModel);
            }
        }

        //create the wsdl model, using the information from the WSDL and the ontology
        ModeledWSDLDevice modeledWSDLDevice = getWsdlModeledDevice(wsdlPojo);

        //get the actual matching model, which consists of the methods from the expectedModel that have
        //a match in the wsdl model
        matchingModel = getMatchingModel(expectedModel, modeledWSDLDevice);
        matchingModel.setDeviceLabel(deviceLabel);
        //if not all of the primary methods from the expected model have a match in the wsdl model
        //then we consider that the device is invalid, because it can't provide the functionalities
        //that we expect it to
        if (matchingModel.getPrimaryMethods().size() < expectedModel.getPrimaryMethods().size()) {
            throw new PrimaryMethodsNotMatchingException();
        }


        return matchingModel;
    }

    public ModeledOntologyDevice getMatchingModel(ModeledOntologyDevice expectedModel, ModeledWSDLDevice wsdlModel) {
        ModeledOntologyDevice matchingModel = new ModeledOntologyDevice();
        wsdlModel.getMethods().forEach(wsdlMethod -> {
            boolean matches = expectedModel.getPrimaryMethods().stream().anyMatch(wsdlMethod::matchesWith);
            if (matches) {
                matchingModel.addPrimaryMethod(wsdlMethod);
            } else {
                matches = expectedModel.getSecondaryMethods().stream().anyMatch(wsdlMethod::matchesWith);
                if (matches) {
                    matchingModel.addSecondaryMethod(wsdlMethod);
                }
            }
        });

        return matchingModel;
    }

    public ModeledWSDLDevice getWsdlModeledDevice(WsdlPojo wsdlPojo) {
        ModeledWSDLDevice modeledWSDLDevice = new ModeledWSDLDevice();

        wsdlPojo.getOperations().forEach(wsdlOperation -> {
                    List<OWLClass> wsdlOwlClasses = determineOntologyClasses(wsdlOperation.getOperationName());

                    ModeledMethod modeledMethod = new ModeledMethod();
                    modeledMethod.setName(wsdlOperation.getOperationName());
                    modeledMethod.setClasses(wsdlOwlClasses.stream()
                            .map(owlClass -> new ModeledClass(owlClass.getIRI().getFragment()))
                            .collect(Collectors.toList()));
                    modeledMethod.setHttpMethod(wsdlOperation.getHttpMethod());
                    modeledMethod.setParams(wsdlOperation.getParameters().stream()
                            .map(wsdlParam -> new ModeledParam(wsdlParam.getName(), wsdlParam.getDirection(), wsdlParam.getParamType()))
                            .collect(Collectors.toList()));
                    modeledMethod.setPath(wsdlOperation.getPath());
                    modeledMethod.setHost(wsdlOperation.getHost());
                    modeledWSDLDevice.addMethod(modeledMethod);
                }
        );

        return modeledWSDLDevice;
    }

    public void appendMethods(OWLClassExpression ce, ModeledOntologyDevice modeledOntologyDevice) {
        switch (ce.getClassExpressionType()) {
            case OBJECT_INTERSECTION_OF:
                ce.asConjunctSet().forEach(subCe -> appendMethods(subCe, modeledOntologyDevice));
                break;
            case OBJECT_MIN_CARDINALITY:
                OWLObjectMinCardinality minCardinality = ((OWLObjectMinCardinality) ce);
                String objectProperty = minCardinality.getProperty().asOWLObjectProperty().getIRI().getFragment();

                switch (objectProperty) {
                    case "hasPrimaryMethod":
                        modeledOntologyDevice.addPrimaryMethod(getModeledMethod(minCardinality.getFiller().asOWLClass()));
                        break;
                    case "hasSecondaryMethod":
                        modeledOntologyDevice.addSecondaryMethod(getModeledMethod(minCardinality.getFiller().asOWLClass()));
                        break;
                }
                break;
        }

    }

    private ModeledMethod getModeledMethod(OWLClass owlClass) {
        //params and classes
        ModeledMethod modeledMethod = new ModeledMethod();

        Set<OWLClassExpression> eqClasses = owlClass.getEquivalentClasses(ontology);

        eqClasses.forEach(eqCls -> {
            switch (eqCls.getClassExpressionType()) {
                case OBJECT_INTERSECTION_OF:
                    eqCls.asConjunctSet().forEach(subExp -> {
                        switch (subExp.getClassExpressionType()) {
                            case OWL_CLASS:
                                modeledMethod.addClass(new ModeledClass(subExp.asOWLClass().getIRI().getFragment()));
                                break;
                            case DATA_MIN_CARDINALITY:
                                OWLDataMinCardinality minCardinality = (OWLDataMinCardinality) subExp;
                                String dataProperty = minCardinality.getProperty().asOWLDataProperty().getIRI().getFragment();
                                modeledMethod.addParam(new ModeledParam(
                                        null,
                                        ParamDirection.fromOntologyProperty(dataProperty),
                                        ParamType.fromOwlDatatype(minCardinality.getFiller().asOWLDatatype()))
                                );
                                break;
                        }
                    });
            }
        });

        modeledMethod.getClasses().forEach(methodClass -> {
            if (HttpMethod.isHttpMethod(methodClass.getClassName())) {
                modeledMethod.setHttpMethod(HttpMethod.valueOf(methodClass.getClassName().toUpperCase()));
            }
        });

        return modeledMethod;
    }

    private List<OWLClass> determineOntologyClasses(String operationName) {
        List<OWLClass> classes = new ArrayList<>();
        List<String> tokens = tokenize(operationName);
        tokens.forEach(token -> {
            classes.add(determineOntologyClass(token));
        });

        return classes;
    }

    private OWLClass determineOntologyClass(String token) {
        OWLNamedIndividual owlNamedIndividual = dataFactory.getOWLNamedIndividual(IRI.create(baseIri + token));
        return reasoner.getTypes(owlNamedIndividual, true).getFlattened().iterator().next();
    }

    private List<String> tokenize(String operationName) {
        List<String> tokens = new ArrayList<>();
        StringBuilder tokenBuilder = new StringBuilder();

        for (int i = 0; i < operationName.length(); ++i) {
            char currentChar = operationName.charAt(i);
            if (Character.isUpperCase(currentChar)) {
                tokens.add(tokenBuilder.toString());
                //clear tokenBuilder
                tokenBuilder.replace(0, tokenBuilder.length(), "");
                currentChar = Character.toLowerCase(currentChar);
            }

            tokenBuilder.append(currentChar);
        }

        tokens.add(tokenBuilder.toString());

        return tokens;
    }
}

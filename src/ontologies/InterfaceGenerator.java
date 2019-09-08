package ontologies;

import org.apache.tools.ant.filters.StringInputStream;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.util.OWLClassExpressionCollector;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import rest.HttpMethod;
import uk.ac.manchester.cs.owl.owlapi.*;
import wsdl.WsdlPojo;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class InterfaceGenerator {
    private final String baseIri;
    private final Reasoner reasoner;
    private final OWLOntology ontology;
    private final OWLDataFactory dataFactory;
    private final DLQueryEngine dlQueryEngine;

    public InterfaceGenerator(String baseIri, String ontologyLocation) throws OWLOntologyCreationException {
        this.baseIri = baseIri;
        OWLOntologyManager owlOntologyManager = OWLManager.createOWLOntologyManager();
        ontology = owlOntologyManager.loadOntologyFromOntologyDocument(new File(ontologyLocation));
        this.dataFactory = owlOntologyManager.getOWLDataFactory();
        this.reasoner = new Reasoner(ontology);
        this.dlQueryEngine = new DLQueryEngine(reasoner, new SimpleShortFormProvider());
    }

    public void getSmth(WsdlPojo wsdlPojo) {
        OWLIndividual owlIndividual = dataFactory.getOWLNamedIndividual(IRI.create(baseIri, "temperature_sensor"));

        Set<OWLClassExpression> deviceTypes = owlIndividual.getTypes(ontology);
        deviceTypes.forEach(type -> {
            type.asOWLClass().getEquivalentClasses(ontology).stream().forEach(eqCls -> {
                ModeledDevice modeledDevice = new ModeledDevice();
                dive(eqCls, modeledDevice);

                modeledDevice.getPrimaryMethods().forEach(primaryOperation -> {
            wsdlPojo.getOperations().forEach(wsdlOperation -> {
                if (primaryOperation.getHttpMethod().equals(wsdlOperation.getHttpMethod())) {
                    //found matching method
                    //need to see if the ontology is ok as well
                    List<OWLClass> wsdlOwlClasses = determineOntologyClasses(wsdlOperation.getOperationName());

                    List<String> stringWsdlOwlClasses = wsdlOwlClasses.stream()
                            .map(owlClass -> owlClass.getIRI().getFragment())
                            .collect(Collectors.toList());
                    List<String> primaryOperationStringClasses = primaryOperation.getClasses().stream()
                            .map(ModeledClass::getClassName)
                            .collect(Collectors.toList());

                    if (stringWsdlOwlClasses.containsAll(primaryOperationStringClasses)
                            && primaryOperationStringClasses.containsAll(stringWsdlOwlClasses)) {
                        System.out.println("Found matching methods");
                    }
                }
            });
        });


                    }
            );
        });



//        List<ModeledMethod> primaryMethods = new ArrayList<>();
//        List<ModeledMethod> secondaryMethods = new ArrayList<>();
//        objectPropertyValues.entrySet().forEach(entry -> {
//            String objectProperty = entry.getKey().getNamedProperty().getIRI().getFragment();
//
//            entry.getValue().stream().forEach(method -> {
//
//                Set<OWLClassExpression> types = method.getTypes(ontology);
//                types.forEach(type -> {
//                    switch (type.getClassExpressionType()) {
//                        case OBJECT_INTERSECTION_OF:
//
//                            //TODO: add the case where you check for params
////                            case DATA_MIN_CARDINALITY:
////                                modeledMethod.get
////                                type.asOWLClass().getReferencingAxioms(ontology).forEach(axiom -> System.out.println(axiom.isOfType(AxiomType.DATA_PROPERTY_ASSERTION)));
//////                                OWLDataMinCardinality owlDataMinCardinality = new OWLDataMinCardinalityImpl()
//                    }
//                });
//
//                //set method based on the found classes
//                modeledMethod.getClasses().forEach(methodClass -> {
//                    if (HttpMethod.isHttpMethod(methodClass.getClassName())) {
//                        modeledMethod.setHttpMethod(HttpMethod.valueOf(methodClass.getClassName().toUpperCase()));
//                    }
//                });
//
//                if (objectProperty.equals("hasPrimaryMethod")) {
//                    primaryMethods.add(modeledMethod);
//                }
//
//                if (objectProperty.equals("hasSecondaryMethod")) {
//                    secondaryMethods.add(modeledMethod);
//                }
//            });
//        });
//
//        primaryMethods.forEach(primaryOperation -> {
//            wsdlPojo.getOperations().forEach(wsdlOperation -> {
//                if (primaryOperation.getHttpMethod().equals(wsdlOperation.getHttpMethod())) {
//                    //found matching method
//                    //need to see if the ontology is ok as well
//                    List<OWLClass> wsdlOwlClasses = determineOntologyClasses(wsdlOperation.getOperationName());
//
//                    List<String> stringWsdlOwlClasses = wsdlOwlClasses.stream()
//                            .map(owlClass -> owlClass.getIRI().getFragment())
//                            .collect(Collectors.toList());
//                    List<String> primaryOperationStringClasses = primaryOperation.getClasses().stream()
//                            .map(ModeledClass::getClassName)
//                            .collect(Collectors.toList());
//
//                    if (stringWsdlOwlClasses.containsAll(primaryOperationStringClasses)
//                            && primaryOperationStringClasses.containsAll(stringWsdlOwlClasses)) {
//                        System.out.println("Found matching methods");
//                    }
//                }
//            });
//        });

        System.out.println("hi");
    }

    public void dive(OWLClassExpression ce, ModeledDevice modeledDevice) {
        System.out.println(ce + "\n########\n");
        switch (ce.getClassExpressionType()) {
            case OBJECT_INTERSECTION_OF:
                ce.asConjunctSet().forEach(subCe -> dive(subCe, modeledDevice));
                break;
            case OBJECT_MIN_CARDINALITY:
                OWLObjectMinCardinality minCardinality = ((OWLObjectMinCardinality) ce);
                String objectProperty = minCardinality.getProperty().asOWLObjectProperty().getIRI().getFragment();

                switch (objectProperty) {
                    case "hasPrimaryMethod":
                        modeledDevice.addPrimaryMethod(getModeledMethod(minCardinality.getFiller().asOWLClass()));
                        break;
                    case "hasSecondaryMethod":
                        modeledDevice.addSecondaryMethod(getModeledMethod(minCardinality.getFiller().asOWLClass()));
                        break;
                }
                break;
        }

    }

    private ModeledMethod getModeledMethod(OWLClass owlClass) {
        //params and classes
        ModeledMethod modeledMethod = new ModeledMethod();

        Set<OWLClassExpression> eqClasses = owlClass.getEquivalentClasses(ontology);
        if (eqClasses.isEmpty()) {
            owlClass.getSuperClasses(ontology)
                    .forEach(superClass -> eqClasses.addAll(superClass.asOWLClass().getEquivalentClasses(ontology)));
        }

        eqClasses.forEach(eqCls -> {
            switch(eqCls.getClassExpressionType()) {
                case OBJECT_INTERSECTION_OF:
                    eqCls.asConjunctSet().forEach(subExp -> {
                        switch (subExp.getClassExpressionType()) {
                            case OWL_CLASS:
                                //TODO: if it has equivalent classes, then split
                                modeledMethod.addClass(new ModeledClass(subExp.asOWLClass().getIRI().getFragment()));
                                break;
                            case DATA_MIN_CARDINALITY:
                                //to add support for parameters
                            case DATA_EXACT_CARDINALITY:
                                //to add support for parameters
                                System.out.println("No support for parameters yet");
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

    public void generateInterface(WsdlPojo wsdlPojo) {
        List<OWLClass> operationsClasses = new ArrayList<>();
        wsdlPojo.getOperations().forEach(operation -> {
            List<OWLClass> classes = new ArrayList<>();
            List<String> tokens = tokenize(operation.getOperationName());
            tokens.forEach(token -> {
                classes.add(determineOntologyClass(token));
            });

            Set<OWLClass> equivalentClasses = dlQueryEngine.getEquivalentClasses(
                    classes.stream().map(this::getClassName).collect(Collectors.joining(" and ")));
        });
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

    private String getClassName(OWLClass owlClass) {
        return owlClass.getIRI().getFragment();
    }
}

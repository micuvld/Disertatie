package ontologies;

import org.apache.tools.ant.filters.StringInputStream;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLClassExpressionCollector;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import uk.ac.manchester.cs.owl.owlapi.OWLClassExpressionImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLEquivalentClassesAxiomImpl;
import wsdl.WsdlPojo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class InterfaceGenerator {
    private final String baseIri;
    private final Reasoner reasoner;
    private final OWLDataFactory dataFactory;
    private final DLQueryEngine dlQueryEngine;

    public InterfaceGenerator(String baseIri, String ontologyLocation) throws OWLOntologyCreationException {
        this.baseIri = baseIri;
        OWLOntologyManager owlOntologyManager = OWLManager.createOWLOntologyManager();
        OWLOntology o = owlOntologyManager.loadOntologyFromOntologyDocument(new File(ontologyLocation));
        this.dataFactory = owlOntologyManager.getOWLDataFactory();
        this.reasoner = new Reasoner(o);
        this.dlQueryEngine = new DLQueryEngine(reasoner, new SimpleShortFormProvider());
    }

    public void generateInterface(WsdlPojo wsdlPojo) {
        List<OWLClass> classes = new ArrayList<>();
        wsdlPojo.getOperations().forEach(operation -> {
            List<String> tokens = tokenize(operation.getOperationName());
            tokens.forEach(token -> {
                classes.add(determineOntologyClass(token));
            });

            Set<OWLClass> equivalentClasses = dlQueryEngine.getEquivalentClasses(
                    classes.stream().map(this::getClassName).collect(Collectors.joining(" and ")));
            System.out.println(equivalentClasses);
        });
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

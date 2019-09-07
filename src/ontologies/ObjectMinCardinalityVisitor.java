package ontologies;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLClassExpressionVisitorAdapter;

import java.util.HashSet;
import java.util.Set;

public class ObjectMinCardinalityVisitor extends OWLClassExpressionVisitorAdapter {
    @Override
    public void visit(OWLObjectMinCardinality ce) {
        System.out.println(ce.getProperty());
        // This method gets called when a class expression is an existential
        // (someValuesFrom) restriction and it asks us to visit it
    }

    @Override
    public void visit(OWLObjectIntersectionOf oe) {
        oe.asConjunctSet().stream().forEach(item -> this.visit((OWLObjectMinCardinality)item));
        // This method gets called when a class expression is an existential
        // (someValuesFrom) restriction and it asks us to visit it
    }
}
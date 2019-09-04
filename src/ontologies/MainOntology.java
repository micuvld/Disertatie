package ontologies;

import org.apache.woden.WSDLException;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import wsdl.WsdlParser;
import wsdl.WsdlPojo;

public class MainOntology {

    public static void main(String[] args) throws OWLOntologyCreationException, WSDLException {
        WsdlParser wsdlParser = new WsdlParser();
        WsdlPojo wsdlPojo = wsdlParser.parseWsdl("a");
        System.out.println(wsdlPojo);

        //get device label using tf-idf
        String deviceLabel = "temperature_sensor";

        //get the label from ontologies (as individual) and start looking for the main expected methods

        //get methods of interest from WSDL

        InterfaceGenerator interfaceGenerator = new InterfaceGenerator(
                "http://www.semanticweb.org/vlad/ontologies/2019/8/Disertatie#",
                "/home/vlad/ws/Disertatie/ontologies/Disertatie.owl");
//        interfaceGenerator.generateInterface(wsdlPojo);
        interfaceGenerator.getSmth(wsdlPojo);
    }
}

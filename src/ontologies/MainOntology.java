package ontologies;

import org.apache.woden.WSDLException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import wsdl.WsdlParser;
import wsdl.WsdlPojo;

public class MainOntology {

    public static void main(String[] args) throws OWLOntologyCreationException, WSDLException {
        WsdlParser wsdlParser = new WsdlParser();
        WsdlPojo wsdlPojo = wsdlParser.parseWsdl("a");
        System.out.println(wsdlPojo);

        InterfaceGenerator interfaceGenerator = new InterfaceGenerator(
                "http://www.semanticweb.org/vlad/ontologies/2019/8/SensorAPI#",
                "/home/vlad/ws/Disertatie/ontologies/SensorsAPI.owl");
        interfaceGenerator.generateInterface(wsdlPojo);
    }
}

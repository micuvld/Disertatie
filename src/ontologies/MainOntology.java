package ontologies;

import org.apache.woden.WSDLException;
import org.apache.woden.WSDLFactory;
import org.apache.woden.WSDLReader;
import org.apache.woden.wsdl20.Description;
import org.apache.woden.wsdl20.xml.DescriptionElement;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import wsdl.WsdlParser;
import wsdl.WsdlPojo;

import java.io.File;
import java.util.Arrays;
import java.util.Set;

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

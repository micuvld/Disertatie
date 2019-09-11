package ontologies;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import exceptions.InvalidIndividualException;
import exceptions.PrimaryMethodsNotMatchingException;
import org.apache.woden.WSDLException;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import tfidf.labeling.Labeler;
import tfidf.search.Search;
import tfidf.utils.Configs;
import wsdl.WsdlParser;
import wsdl.WsdlPojo;

import java.io.IOException;

public class MainOntology {

    public static void main(String[] args) throws OWLOntologyCreationException, WSDLException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        Configs.loadProperties("/home/vlad/ws/Disertatie/config.properties");
        WsdlParser wsdlParser = new WsdlParser();
        WsdlPojo wsdlPojo = wsdlParser.parseWsdl("a");
        System.out.println(wsdlPojo);

        Labeler labeler = new Labeler();
        Search search = new Search();
        String deviceLabel = labeler.getLabel(search.rankedSearch(wsdlPojo.toString()).get(0).getFileName()).getLabel();
        System.out.println("Device label:" + deviceLabel);

        InterfaceGenerator interfaceGenerator = new InterfaceGenerator(
                "http://www.semanticweb.org/vlad/ontologies/2019/8/Disertatie#",
                "/home/vlad/ws/Disertatie/ontologies/Disertatie.owl");
        try {
            System.out.println(objectMapper.writeValueAsString(interfaceGenerator.getMatchingModel(wsdlPojo, deviceLabel)));
        } catch (PrimaryMethodsNotMatchingException | InvalidIndividualException e) {
            e.printStackTrace();
        }
    }
}

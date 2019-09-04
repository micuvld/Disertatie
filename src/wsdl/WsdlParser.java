package wsdl;

import org.apache.woden.WSDLException;
import org.apache.woden.WSDLFactory;
import org.apache.woden.WSDLReader;
import org.apache.woden.wsdl20.*;
import org.apache.woden.wsdl20.extensions.ExtensionProperty;
import org.w3c.dom.Element;
import rest.HttpMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WsdlParser {
    private static final String HTTP_METHOD_EXTENSION_NAME = "http method";
    private final WSDLReader wsdlReader;

    public WsdlParser() {
        try {
            WSDLFactory wsdlFactory = WSDLFactory.newInstance();
            wsdlReader = wsdlFactory.newWSDLReader();
        } catch (WSDLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to instantiate WSDLFactory");
        }
    }

    public WsdlPojo parseWsdl(String wsdlUrl) throws WSDLException {
        wsdlUrl = "/home/vlad/ws/Disertatie/wsdl/example.wsdl";
        Description descComp = wsdlReader.readWSDL(wsdlUrl);
        Service service = descComp.getServices()[0];
        return WsdlPojo.builder()
                .serviceName(service.getName().getLocalPart())
                .serviceDescription(getServiceDocumentation(service))
                .operations(getOperations(service))
                .build();
    }

    private String getServiceDocumentation(Service service) {
        return ((Element) service.toElement().getDocumentationElements()[0].getContent().getSource()).getTextContent();
    }

    private List<ServiceOperation> getOperations(Service service) {
        List<ServiceOperation> serviceOperations = new ArrayList<>();

        Arrays.stream(service.getEndpoints()).forEach(ep -> {
            Binding binding = ep.getBinding();
            Arrays.stream(binding.getBindingOperations()).forEach(bo -> {
                InterfaceOperation io = bo.getInterfaceOperation();
                String name = io.getName().getLocalPart();
                List<OperationParameter> parameters = new ArrayList<>();

                Arrays.stream(io.getInterfaceMessageReferences()).forEach(imr -> {
                    parameters.add(OperationParameter.fromInterfaceMessageReference(imr));
                });

                ServiceOperation serviceOperation = ServiceOperation.builder()
                        .operationName(name)
                        .httpMethod(getMethodOfBindingOperation(bo))
                        .parameters(parameters)
                        .build();
                serviceOperations.add(serviceOperation);
            });
        });

        return serviceOperations;
    }

    private HttpMethod getMethodOfBindingOperation(BindingOperation bindingOperation) {
        ExtensionProperty httpMethodProperty = Arrays.stream(bindingOperation.getExtensionProperties())
                .filter(prop -> prop.getName().equals(HTTP_METHOD_EXTENSION_NAME))
                .findFirst().orElse(null);

        return httpMethodProperty == null
                ? HttpMethod.NONE
                : httpMethodProperty.getContent() == null
                    ? HttpMethod.NONE
                    : HttpMethod.valueOf(httpMethodProperty.getContent().toString());
    }
}

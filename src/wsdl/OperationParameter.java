package wsdl;

import lombok.Builder;
import lombok.Data;
import org.apache.woden.wsdl20.InterfaceMessageReference;
import org.apache.woden.wsdl20.enumeration.Direction;

@Data
@Builder
public class OperationParameter {
    private final String name;
    private final Direction direction;

    public static OperationParameter fromInterfaceMessageReference(InterfaceMessageReference interfaceMessageReference) {
        return OperationParameter.builder()
                .name(interfaceMessageReference.getMessageLabel().toString())
                .direction(interfaceMessageReference.getDirection())
                .build();
    }
}

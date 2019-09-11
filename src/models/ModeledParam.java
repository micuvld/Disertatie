package models;

import lombok.*;
import org.apache.woden.internal.wsdl20.InterfaceMessageReferenceImpl;
import org.apache.woden.wsdl20.InterfaceMessageReference;

import static javax.annotation.processing.Completions.of;

@Data
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"direction", "paramType"})
@NoArgsConstructor
public class ModeledParam {
    private String name;
    private ParamDirection direction;
    private ParamType paramType;

    public static ModeledParam fromInterfaceMessageReference(InterfaceMessageReference interfaceMessageReference) {
        return ModeledParam.builder()
                .name(interfaceMessageReference.getMessageLabel().toString())
                .direction(ParamDirection.fromXmlDirection(interfaceMessageReference.getDirection()))
                .paramType(ParamType.fromXmlDataType(((InterfaceMessageReferenceImpl)interfaceMessageReference).getElement().getQName().getLocalPart()))
                .build();
    }

    @Override
    public String toString() {
        return "\t\t" + name + ":" + direction.name() + "::" + paramType.name();
    }
}

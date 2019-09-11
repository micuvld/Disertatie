package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.woden.internal.wsdl20.InterfaceMessageReferenceImpl;
import org.apache.woden.wsdl20.InterfaceMessageReference;

import static javax.annotation.processing.Completions.of;

@Data
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"direction", "paramType"})
public class ModeledParam {
    private final String name;
    private final ParamDirection direction;
    private final ParamType paramType;

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

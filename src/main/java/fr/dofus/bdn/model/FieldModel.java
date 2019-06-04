package fr.dofus.bdn.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import fr.dofus.bdn.utils.DeserializeUtils;
import fr.dofus.bdn.utils.SerializeUtils;
import fr.dofus.bdn.utils.StrUtils;
import fr.dofus.bdn.utils.TypeUtils;

public class FieldModel {

    @JsonProperty(value = "Name")
    private String name;

    @JsonProperty(value = "Type")
    private String type;

    @JsonProperty(value = "WriteMethod")
    private String writeMethod;

    @JsonProperty(value = "Method")
    private String method;

    @JsonProperty(value = "IsVector")
    private boolean isVector;

    @JsonProperty(value = "IsDynamicLength")
    private boolean isDynamicLength;

    @JsonProperty(value = "Length")
    private long length;

    @JsonProperty(value = "WriteLengthMethod")
    private String writeLengthMethod;

    @JsonProperty(value = "UseTypeManager")
    private boolean useTypeManager;

    @JsonProperty(value = "UseBBW")
    private boolean bbw;

    @JsonProperty(value = "BBWPosition")
    private long bbwPosition;


    /**
     * Get the java formated type of the field
     *
     * @return String
     */
    private String getJavaType() {
        return isVector ? TypeUtils.getJavaListFromAsType(type) : TypeUtils.getJavaTypeFromAsType(type);
    }


    /**
     * Get the formated java field name
     *
     * @return String
     */
    public String getFieldName() {
        return String.format("private %s %s;", getJavaType(), name);
    }


    /**
     * Get the formated java getter of the field
     *
     * @return String
     */
    public String getGetter() {
        return String.format("public %s %s { return this.%s; }",
            getJavaType(),
            StrUtils.getGetterName(name),
            name
        );
    }


    /**
     * Get the formated java getter of the field
     *
     * @return String
     */
    public String getSetter() {
        return String.format("public void %s { this.%s = %s; }",
            StrUtils.getSetterName(name, getJavaType()),
            name,
            name
        );
    }


    /**
     * Get the String param to create a constructor with this field
     *
     * @return String
     */
    public String getConstructorParam() {
        return getJavaType() + " " + name;
    }


    /**
     * Get the String value to create a constructor with this field
     *
     * @return String
     */
    public String getConstructorValue() {
        return String.format("this.%s = %s;", name, name);
    }


    /**
     * Get the serialize method as a String for this feild
     *
     * @return String
     */
    public List<String> getSerializeMethod() {
        writeLengthMethod = writeLengthMethod.isEmpty() ? "writeShort" : writeLengthMethod;

        if (bbw) {
            return SerializeUtils.serialiseBbw(bbwPosition, name);
        } else if (isVector) {
            return SerializeUtils.serialiseVector(
                isDynamicLength,
                length,
                useTypeManager,
                writeLengthMethod,
                writeMethod,
                name,
                getJavaType()
            );
        } else {
            return SerializeUtils.serialiseSimpleField(useTypeManager, writeMethod, getJavaType(), name);
        }
    }

    /**
     * Get the deserialize method as a String for this feild
     *
     * @return String
     */
    public List<String> getDeserializeMethod() {
        String readLengthMethod = writeLengthMethod.isEmpty() ?
            "readShort" :
            TypeUtils.getReadMethod(writeLengthMethod);

        if (bbw) {
            return DeserializeUtils.deserialiseBbw(bbwPosition, name);
        } else if (isVector) {
            return DeserializeUtils.deserialiseVector(
                isDynamicLength,
                length,
                useTypeManager,
                readLengthMethod,
                writeMethod,
                name,
                getJavaType()
            );
        } else {
            return DeserializeUtils.deserialiseSimpleField(useTypeManager, writeMethod, getJavaType(), name);
        }
    }

    public boolean isVector() {
        return isVector;
    }

    public boolean isUseTypeManager() {
        return useTypeManager;
    }

    public boolean isBbw() {
        return bbw;
    }

    public String getImportType() {
        String type = TypeUtils.getJavaTypeFromAsType(this.type);

        if (type.equals(this.type)) {
            return type;
        }

        return null;
    }
}

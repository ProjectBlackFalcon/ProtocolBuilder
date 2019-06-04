package fr.dofus.bdn.utils;

import java.util.ArrayList;
import java.util.List;

public class DeserializeUtils {

    private DeserializeUtils() {

    }


    public static List<String> deserialiseBbw(final long bbwPosition, final String name) {
        List<String> deserialiseList = new ArrayList<>();
        if (bbwPosition == 0) {
            deserialiseList.add("flag = (byte) reader.readUnsignedByte();");
        }
        deserialiseList.add(String.format(
            "this.%s = BooleanByteWrapper.getFlag(flag, (byte) %s);",
            bbwPosition,
            name)
        );
        return deserialiseList;
    }

    public static List<String> deserialiseVector(final boolean isDynamicLength,
                                               final long length,
                                               final boolean useTypeManager,
                                               final String readLengthMethod,
                                               final String writeMethod,
                                               final String name,
                                               final String type) {
        List<String> deserialiseList = new ArrayList<>();
        final String nameInstance = name + "Instance";
        final String nameSize = name + "Size";

        deserialiseList.add(String.format("this.%s = new ArrayList<%s>();",
            name,
            TypeUtils.getJavaPrimitiveType(type))
        );

        if (isDynamicLength) {
            deserialiseList.add(String.format("int %s = reader.%s;", nameSize, readLengthMethod));
            deserialiseList.add(String.format("for (int i = 0; i < this.%s.size(); i++) {", nameSize));
        } else {
            deserialiseList.add(String.format("for (int i = 0; i < %s; i++) {", length));
        }

        if (useTypeManager) {
            deserialiseList.add(
                StrUtils.formatTab(
                    "%s %s = (%s) ProtocolTypeManager.getInstance(reader.readShort());",
                    type,
                    nameInstance,
                    type)
            );
            deserialiseList.add(StrUtils.formatTab("%s.deserialize(reader);", nameInstance));
        } else if (writeMethod.isEmpty()) {
            deserialiseList.add(StrUtils.formatTab("%s %s = new %s();", type, nameInstance, type));
            deserialiseList.add(StrUtils.formatTab("%s.deserialize(reader);", nameInstance));
        } else {
            deserialiseList.add(StrUtils.formatTab("%s %s = reader.%s;", type, nameInstance, readLengthMethod));
        }
        deserialiseList.add(StrUtils.formatTab("this.%s.add(%s)", name, nameInstance));
        deserialiseList.add("}");
        return deserialiseList;
    }

    public static List<String> deserialiseSimpleField(final boolean useTypeManager,
                                                    final String writeMethod,
                                                    final String type,
                                                    final String name) {
        List<String> deserialiseList = new ArrayList<>();

        if (writeMethod.isEmpty()) {
            if (useTypeManager) {
                deserialiseList.add(
                    String.format(
                        "this.%s = (%s) ProtocolTypeManager.getInstance(reader.readShort());",
                        name,
                        type)
                );
            } else {
                deserialiseList.add(String.format("this.%s = new %s();", name, type));
            }
            deserialiseList.add(String.format("this.%s.deserialize(reader);", name));
        } else {
            deserialiseList.add(String.format("this.%s = reader.%s;", name, TypeUtils.getReadMethod(writeMethod)));
        }
        return deserialiseList;
    }
}

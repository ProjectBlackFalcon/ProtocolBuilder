package fr.dofus.bdn.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import static fr.dofus.bdn.ProtocolBuilder.PROCOTOL_ID;

public class SerializeUtils {

    private SerializeUtils(){

    }

    public static List<String> serialiseBbw(final long bbwPosition, final String name){
        List<String> serialiseList = new ArrayList<>();
        serialiseList.add(String.format(
            "flag = BooleanByteWrapper.setFlag(%s, flag, %s);",
            bbwPosition,
            name)
        );
        if (bbwPosition == 7) {
            serialiseList.add("writer.writeByte(flag);");
        }
        return serialiseList;
    }

    public static List<String> serialiseVector(final boolean isDynamicLength,
                                                final long length,
                                                final boolean useTypeManager,
                                                final String writeLengthMethod,
                                                final String writeMethod,
                                                final String name,
                                                final String type){
        List<String> serialiseList = new ArrayList<>();
        if (isDynamicLength) {
            serialiseList.add(String.format("writer.%s(this.%s.size());", writeLengthMethod, name));
            serialiseList.add(String.format("for (int i = 0; i < this.%s.size(); i++) {", name));
        } else {
            serialiseList.add(String.format("for (int i = 0; i < %s; i++) {", length));
        }

        if (useTypeManager) {
            serialiseList.add(StrUtils.formatTab("writer.writeShort(%s.%s);", type, PROCOTOL_ID));
            serialiseList.add(StrUtils.formatTab("this.%s.get(i).serialize(writer);", name));

        } else if (writeMethod.isEmpty()) {
            serialiseList.add(StrUtils.formatTab("this.%s.get(i).serialize(writer);", name));
        } else {
            serialiseList.add(StrUtils.formatTab("writer.%s(this.%s.get(i));", writeMethod, name));
        }

        serialiseList.add("}");
        return serialiseList;
    }

    public static List<String> serialiseSimpleField(final boolean useTypeManager,
                                                    final String writeMethod,
                                                    final String type,
                                                    final String name){
        if (useTypeManager) {
            return Collections.singletonList(String.format("writer.writeShort(%s.%s);", type, PROCOTOL_ID));
        } else if (writeMethod.isEmpty()) {
            return Collections.singletonList(String.format("%s.serialize(writer);", name));
        } else {
            return Collections.singletonList(String.format("writer.%s(this.%s);", writeMethod, name));
        }
    }
}

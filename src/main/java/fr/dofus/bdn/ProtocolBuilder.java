package fr.dofus.bdn;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import fr.dofus.bdn.model.D2JsonModel;
import fr.dofus.bdn.model.EnumModel;
import fr.dofus.bdn.model.FieldModel;
import fr.dofus.bdn.model.MessageModel;
import fr.dofus.bdn.utils.FilesUtils;
import fr.dofus.bdn.utils.OutputUtils;
import fr.dofus.bdn.utils.StrUtils;

public class ProtocolBuilder {

    private static final Logger log = Logger.getLogger(ProtocolBuilder.class);

    public static final String PROCOTOL_ID = "PROTOCOL_ID";

    private D2JsonModel d2JsonModel;

    public ProtocolBuilder(final D2JsonModel d2JsonModel) {
        this.d2JsonModel = d2JsonModel;
    }

    public void generateClasses() {


        OutputUtils.init(System.currentTimeMillis(), (long) d2JsonModel.getMessages().size());
        log.info("Generating Messages...");
        d2JsonModel.getMessages().forEach(this::generateClass);

        OutputUtils.init(System.currentTimeMillis(), (long) d2JsonModel.getTypes().size());
        log.info("Generating Types...");
        d2JsonModel.getTypes().forEach(this::generateClass);

        OutputUtils.init(System.currentTimeMillis(), (long) d2JsonModel.getEnums().size());
        log.info("Generating Enums...");
        d2JsonModel.getEnums().forEach(this::generateEnum);


    }

    private void generateClass(final MessageModel messageModel) {
        OutputUtils.printProgress();
        StringBuilder builder = new StringBuilder();
        builder.append("package ").append(messageModel.getNamespace()).append(";");
        builder.append(StrUtils.appendLine(getImports(messageModel)));

        String classString = String.format("public class %s %s {", messageModel.getName(),
            messageModel.getParents().isEmpty()
                ? "extends NetworkMessage"
                : "extends " + messageModel.getParents()
        );

        builder.append(System.lineSeparator());

        builder.append(StrUtils.appendLine(classString));
        builder.append(StrUtils.appendLine(
            StrUtils.formatTab("public static final int %s = %s;", PROCOTOL_ID, messageModel.getProtocolId()))
        );

        builder.append(System.lineSeparator());

        messageModel.getFieldModels().forEach(field ->
            builder.append(StrUtils.appendLineTabbed(field.getFieldName()))
        );

        builder.append(System.lineSeparator());

        messageModel.getFieldModels().forEach(field -> builder.append(StrUtils.appendLineTabbed(field.getGetter())));

        builder.append(System.lineSeparator());

        messageModel.getFieldModels().forEach(field -> builder.append(StrUtils.appendLineTabbed(field.getSetter())));

        if (!messageModel.getFieldModels().isEmpty()){
            builder.append(System.lineSeparator());
            builder.append(StrUtils.appendFormatedLine(1, "public %s(){}", messageModel.getName()));
        }

        builder.append(System.lineSeparator());

        String constructorParams = messageModel
            .getFieldModels()
            .stream()
            .map(field -> field.getJavaType() + " " + field.getName())
            .collect(Collectors.joining(", "));

        builder.append(StrUtils.appendFormatedLine(1, "public %s(%s){", messageModel.getName(), constructorParams));
        messageModel.getFieldModels().forEach(field ->
            builder.append(StrUtils.appendFormatedLine(2, "this.%s = %s;", field.getName(), field.getName()))
        );
        builder.append(StrUtils.appendLineTabbed("}"));

        builder.append(System.lineSeparator());

        List<FieldModel> bbwFields = messageModel.getFieldModels().stream()
            .filter(FieldModel::isBbw)
            .collect(Collectors.toList());

        List<FieldModel> otherFields = messageModel.getFieldModels().stream()
            .filter(field -> !field.isBbw())
            .collect(Collectors.toList());


        builder.append(StrUtils.appendLineTabbed("@Override"));
        builder.append(StrUtils.appendLineTabbed("public void serialize(DofusDataWriter writer) {"));
        builder.append(StrUtils.appendLineTabbed(2, "try {"));

        if (!messageModel.getParents().isEmpty()){
            builder.append(StrUtils.appendLineTabbed(3, "super.serialize(writer);"));
        }

        if (!bbwFields.isEmpty()) {
            builder.append(StrUtils.appendLineTabbed(3, "byte flag = 0;"));
        }

        for (int i = 0; i < bbwFields.size(); i++) {
            bbwFields.get(i).getSerializeMethod().forEach(method ->
                builder.append(StrUtils.appendLineTabbed(3, method))
            );

            if (i == bbwFields.size() - 1 && bbwFields.get(i).getBbwPosition() != 7) {
                builder.append(StrUtils.appendLineTabbed(3, "writer.writeByte(flag);"));
            }
        }


        otherFields.forEach(field -> field.getSerializeMethod().forEach(method ->
            builder.append(StrUtils.appendLineTabbed(3, method))
        ));

        builder.append(StrUtils.appendLineTabbed(2, "} catch (Exception e){"));
        builder.append(StrUtils.appendLineTabbed(3, "e.printStackTrace();"));
        builder.append(StrUtils.appendLineTabbed(2, "}"));
        builder.append(StrUtils.appendLineTabbed("}"));

        builder.append(System.lineSeparator());

        builder.append(StrUtils.appendLineTabbed("@Override"));
        builder.append(StrUtils.appendLineTabbed("public void deserialize(DofusDataReader reader) {"));

        builder.append(StrUtils.appendLineTabbed(2, "try {"));

        if (!messageModel.getParents().isEmpty()){
            builder.append(StrUtils.appendLineTabbed(3, "super.deserialize(reader);"));
        }

        if (!bbwFields.isEmpty()) {
            builder.append(StrUtils.appendLineTabbed(3, "byte flag = 0;"));
        }

        bbwFields.forEach(field -> field.getDeserializeMethod().forEach(method ->
            builder.append(StrUtils.appendLineTabbed(3, method))
        ));

        otherFields.forEach(field -> field.getDeserializeMethod().forEach(method ->
            builder.append(StrUtils.appendLineTabbed(3, method))
        ));

        builder.append(StrUtils.appendLineTabbed(2, "} catch (Exception e){"));
        builder.append(StrUtils.appendLineTabbed(3, "e.printStackTrace();"));
        builder.append(StrUtils.appendLineTabbed(2, "}"));
        builder.append(StrUtils.appendLineTabbed("}"));
        builder.append(StrUtils.appendLine("}"));

        try {
            FilesUtils.writeFile(
                builder.toString(),
                messageModel.getNamespace().replaceAll("\\.", "/") + "/" + messageModel.getName() + ".java"
            );
        } catch (IOException e) {
            throw new Error("Cannot generate file", e);
        }

    }

    private String getImports(final MessageModel messageModel) {
        Set<String> imports = new HashSet<>();

        imports.add(StrUtils.appendLine("import com.ankamagames.dofus.network.utils.DofusDataReader;"));
        imports.add(StrUtils.appendLine("import com.ankamagames.dofus.network.utils.DofusDataWriter;"));

        if (!messageModel.getParents().isEmpty()) {
            String parentNamespace = this.d2JsonModel.findMessageByName(messageModel.getParents()).getNamespace();

            if (!parentNamespace.equals(messageModel.getNamespace())){
                imports.add(StrUtils.appendLine("import " + parentNamespace + "." + messageModel.getParents() + ";"));
            }
        } else {
            imports.add(StrUtils.appendLine("import com.ankamagames.dofus.network.NetworkMessage;"));
        }

        messageModel.getFieldModels().forEach(field -> {
            if (field.isVector()) {
                imports.add(StrUtils.appendLine("import java.util.ArrayList;"));
                imports.add(StrUtils.appendLine("import java.util.List;"));
            }
            if (field.isBbw()) {
                imports.add(StrUtils.appendLine("import com.ankamagames.dofus.network.utils.types.BooleanByteWrapper;"));
            }
            if (field.isUseTypeManager()) {
                imports.add(StrUtils.appendLine("import com.ankamagames.dofus.network.utils.ProtocolTypeManager;"));
            }

            String type = field.getImportType();

            if (type != null) {
                imports.add(StrUtils.appendLine(
                    "import " + this.d2JsonModel.findMessageByName(type).getNamespace() + "." + type + ";")
                );
            }
        });

        StringBuilder builder = new StringBuilder();
        imports.forEach(builder::append);
        return builder.toString();
    }

    private void generateEnum(final EnumModel enumModel) {
        OutputUtils.printProgress();

        StringBuilder builder = new StringBuilder();
        builder.append("package com.ankamagames.dofus.network.enums;");

        builder.append(System.lineSeparator());

        builder.append("import java.util.stream.Stream;");

        builder.append(System.lineSeparator());

        builder.append(StrUtils.appendLine(String.format("public enum %s {", enumModel.getName())));

        builder.append(System.lineSeparator());

        String constructorParams = enumModel
            .getValues()
            .stream()
            .map(field -> StrUtils.appendFormatedLine(1, "%s(%s)", field.getName(), field.getValue()))
            .collect(Collectors.joining(","));

        builder.append(constructorParams).append(";");

        builder.append(System.lineSeparator());

        builder.append(StrUtils.appendLineTabbed("private final int value;"));

        builder.append(System.lineSeparator());

        builder.append(StrUtils.appendFormatedLine(1, "%s(int value){ this.value = value; }",
            enumModel.getName())
        );

        builder.append(System.lineSeparator());

        builder.append(StrUtils.appendLineTabbed("public int value() { return this.value; }"));

        builder.append(System.lineSeparator());

        builder.append(StrUtils.appendFormatedLine(1, "public %s get(int value){", enumModel.getName()));
        builder.append(StrUtils.appendLineTabbed(2, "return Stream.of(values()).filter(current -> current.value == value).findAny().orElse(null);"));

        builder.append(StrUtils.appendLineTabbed("}"));

        builder.append(System.lineSeparator());
        builder.append(StrUtils.appendLine("}"));

        try {
            FilesUtils.writeFile(
                builder.toString(), "com/ankamagames/dofus/network/enums/" + enumModel.getName() + ".java"
            );
        } catch (IOException e) {
            throw new Error("Cannot generate file", e);
        }

    }


}

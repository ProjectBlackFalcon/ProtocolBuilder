package fr.dofus.bdn;

import java.io.IOException;
import java.util.HashSet;
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
        long startTime = System.currentTimeMillis();
        log.info("Generating files...");

        OutputUtils.init(System.currentTimeMillis(), (long) d2JsonModel.getMessages().size());
        log.info("Generating Messages...");
        d2JsonModel.getMessages().forEach(this::generateClass);

        OutputUtils.init(System.currentTimeMillis(), (long) d2JsonModel.getTypes().size());
        log.info("Generating Types...");
        d2JsonModel.getTypes().forEach(this::generateClass);

        OutputUtils.init(System.currentTimeMillis(), (long) d2JsonModel.getEnums().size());
        log.info("Generating Enums...");
        d2JsonModel.getEnums().forEach(this::generateEnum);

        long elapsedTime = System.currentTimeMillis() - startTime;
        log.info(String.format("Generating files... done in %s,%ss!", elapsedTime / 1000, elapsedTime % 1000));
    }

    private void generateClass(final MessageModel messageModel) {
        OutputUtils.printProgress();
        StringBuilder builder = new StringBuilder();
        builder.append("package ").append(messageModel.getNamespace()).append(";");
        builder.append(StrUtils.appendLine(getImports(messageModel)));

        String classString = String.format("public class %s %s {", messageModel.getName(),
            messageModel.getParents().isEmpty()
                ? StrUtils.EMPTY_STRING
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

        builder.append(StrUtils.appendFormatedLine(1, "public %s(){}", messageModel.getName()));

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

        builder.append(StrUtils.appendLineTabbed("@Override"));
        builder.append(StrUtils.appendLineTabbed("public void Serialize(DofusDataWriter writer) {"));
        builder.append(StrUtils.appendLineTabbed(2, "try {"));

        boolean bbwSerialize = messageModel.getFieldModels().stream().anyMatch(FieldModel::isBbw);

        if (bbwSerialize) {
            builder.append(StrUtils.appendLineTabbed(3, "byte flag = 0;"));
        }

        for (int i = 0; i < messageModel.getFieldModels().size(); i++) {
            messageModel.getFieldModels().get(i).getSerializeMethod().forEach(method ->
                builder.append(StrUtils.appendLineTabbed(3, method))
            );

            if (!bbwSerialize) {
                continue;
            }

            if (messageModel.getFieldModels().get(i).getBbwPosition() == 7) {
                continue;
            }


            if (i == messageModel.getFieldModels().size() - 1) {
                builder.append(StrUtils.appendLineTabbed(3, "writer.writeByte(flag);"));
            } else {
                if (!messageModel.getFieldModels().get(i + 1).isBbw()) {
                    builder.append(StrUtils.appendLineTabbed(3, "writer.writeByte(flag);"));
                }
            }
        }

        builder.append(StrUtils.appendLineTabbed(2, "} catch (Exception e){"));
        builder.append(StrUtils.appendLineTabbed(3, "e.printStackTrace();"));
        builder.append(StrUtils.appendLineTabbed(2, "}"));
        builder.append(StrUtils.appendLineTabbed("}"));

        builder.append(System.lineSeparator());

        builder.append(StrUtils.appendLineTabbed("@Override"));
        builder.append(StrUtils.appendLineTabbed("public void Deserialise(DofusDataReader reader) {"));

        builder.append(StrUtils.appendLineTabbed(2, "try {"));

        boolean bbwDeserialize = messageModel.getFieldModels().stream().anyMatch(FieldModel::isBbw);

        if (bbwDeserialize) {
            builder.append(StrUtils.appendLineTabbed(3, "byte flag;"));
        }

        messageModel.getFieldModels().forEach(fieldModel ->
            fieldModel.getDeserializeMethod().forEach(method ->
                builder.append(StrUtils.appendLineTabbed(3, method))
            )
        );

        builder.append(StrUtils.appendLineTabbed(2, "} catch (Exception e){"));
        builder.append(StrUtils.appendLineTabbed(3, "e.printStackTrace();"));
        builder.append(StrUtils.appendLineTabbed(2, "}"));
        builder.append(StrUtils.appendLineTabbed("}"));
        builder.append(StrUtils.appendLine("}"));

        try {
            FilesUtils.writeFile(
                builder.toString(),
                messageModel.getNamespace().replaceAll("\\.", "\\\\") + "\\" + messageModel.getName() + ".java"
            );
        } catch (IOException e) {
            throw new Error("Cannot generate file", e);
        }

    }

    private String getImports(final MessageModel messageModel) {
        Set<String> imports = new HashSet<>();

        imports.add(StrUtils.appendLine("import java.io.IOException;"));
        imports.add(StrUtils.appendLine("import com.ankamagames.dofus.utils.DofusDataReader;"));
        imports.add(StrUtils.appendLine("import com.ankamagames.dofus.utils.DofusDataWriter;"));

        if (!messageModel.getParents().isEmpty()) {
            imports.add(StrUtils.appendLine("import " + messageModel.getParents()));
        } else {
            imports.add(StrUtils.appendLine("import com.ankamagames.dofus.NetworkMessage"));
        }

        messageModel.getFieldModels().forEach(field -> {
            if (field.isVector()) {
                imports.add(StrUtils.appendLine("import java.util.ArrayList;"));
                imports.add(StrUtils.appendLine("import java.util.List;"));
            }
            if (field.isBbw()) {
                imports.add(StrUtils.appendLine("import com.ankamagames.dofus.util.types.BooleanByteWrapper;"));
            }
            if (field.isUseTypeManager()) {
                imports.add(StrUtils.appendLine("import com.ankamagames.dofus.utils.ProtocolTypeManager;"));
            }

            String type = field.getImportType();

            if (type != null) {
                imports.add(StrUtils.appendLine(
                    "import " + this.d2JsonModel.findMessageByName(type).getNamespace() + "." + type)
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

        builder.append(StrUtils.appendFormatedLine(1, "public %s(int value){ this.value = value; }",
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

package fr.dofus.bdn;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import fr.dofus.bdn.model.D2JsonModel;
import fr.dofus.bdn.model.EnumModel;
import fr.dofus.bdn.model.MessageModel;
import fr.dofus.bdn.utils.FilesUtils;
import fr.dofus.bdn.utils.StrUtils;

public class ProtocolBuilder {

    private D2JsonModel d2JsonModel;

    public ProtocolBuilder(final D2JsonModel d2JsonModel) {
        this.d2JsonModel = d2JsonModel;
    }

    public void generateClasses(){
        d2JsonModel.getMessages().forEach(this::generateClass);
        d2JsonModel.getTypes().forEach(this::generateClass);
    }

    private void generateClass(final MessageModel messageModel){
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
            StrUtils.formatTab("public static final int PROTOCOL_ID = %s;", messageModel.getProtocolId()))
        );

        builder.append(System.lineSeparator());

        messageModel.getFieldModels().forEach(field ->
            builder.append(StrUtils.appendLineTabbed(field.getFieldName()))
        );

        builder.append(System.lineSeparator());

        messageModel.getFieldModels().forEach(field -> builder.append(StrUtils.appendLineTabbed(field.getGetter())));

        builder.append(System.lineSeparator());

        builder.append(StrUtils.appendLineTabbed("@Override"));
        builder.append(StrUtils.appendLineTabbed("public void Serialize(DofusDataWriter writer) {"));
        builder.append(StrUtils.appendLineTabbed("}"));

        builder.append(System.lineSeparator());

        builder.append(StrUtils.appendLineTabbed("@Override"));
        builder.append(StrUtils.appendLineTabbed("public void Deserialise(DofusDataReader reader) {"));
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

    private String getImports(final MessageModel messageModel){
        Set<String> imports = new HashSet<>();

        imports.add(StrUtils.appendLine("import java.io.IOException;"));
        imports.add(StrUtils.appendLine("import com.ankamagames.dofus.utils.DofusDataReader;"));
        imports.add(StrUtils.appendLine("import com.ankamagames.dofus.utils.DofusDataWriter;"));

        if (!messageModel.getParents().isEmpty()){
            imports.add(StrUtils.appendLine("import " + messageModel.getParents()));
        } else {
            imports.add(StrUtils.appendLine("import com.ankamagames.dofus.NetworkMessage"));
        }

        messageModel.getFieldModels().forEach(field -> {
            if (field.isVector()){
                imports.add(StrUtils.appendLine("import java.util.ArrayList;"));
                imports.add(StrUtils.appendLine("import java.util.List;"));
            }
            if (field.isBbw()){
                imports.add(StrUtils.appendLine("import com.ankamagames.dofus.util.types.BooleanByteWrapper;"));
            }
            if (field.isUseTypeManager()){
                imports.add(StrUtils.appendLine("import com.ankamagames.dofus.utils.ProtocolTypeManager;"));
            }

            String type = field.getImportType();

            if(type != null){
                imports.add(StrUtils.appendLine(
                    "import " + this.d2JsonModel.findMessageByName(type).getNamespace() + "." + type)
                );
            }
        });

        StringBuilder builder = new StringBuilder();
        imports.forEach(builder::append);
        return builder.toString();
    }

    private void generateEnum(final EnumModel enumModel){
        //TODO
    }





}

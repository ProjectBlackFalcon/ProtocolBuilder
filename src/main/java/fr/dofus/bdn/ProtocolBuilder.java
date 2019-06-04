package fr.dofus.bdn;

import java.io.IOException;

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
        // Generate package
        builder.append(StrUtils.appendLine("package ")).append(messageModel.getNamespace()).append(";");

        // Generate import
        builder.append(StrUtils.appendLine(getImports(messageModel)));
        // Generate class + extends

        String classString = String.format("public class %s %s {", messageModel.getName(),
            messageModel.getParents().isEmpty()
                ? StrUtils.EMPTY_STRING
                : "extends " + messageModel.getParents()
        );

        builder.append(StrUtils.appendLine(classString));
            // Generate ID
            // Generate fields
            // Generate getters
            // Generate Serialise
            // Generate Deserialise

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
        StringBuilder builder = new StringBuilder();
        builder.append(StrUtils.appendLine("import java.io.IOException;"));
        builder.append(StrUtils.appendLine("import com.ankamagames.dofus.utils.DofusDataReader;"));
        builder.append(StrUtils.appendLine("import com.ankamagames.dofus.utils.DofusDataWriter;"));

        if (!messageModel.getParents().isEmpty()){
            builder.append(StrUtils.appendLine("import ")).append(messageModel.getParents());
        } else {
            builder.append(StrUtils.appendLine("import com.ankamagames.dofus.NetworkMessage"));
        }

        if (messageModel.getFieldModels() == null){
            return builder.toString();
        }

        messageModel.getFieldModels().forEach(field -> {
            if (field.isVector()){
                builder.append(StrUtils.appendLine("import java.util.ArrayList;"));
                builder.append(StrUtils.appendLine("import java.util.List;"));
            }
            if (field.isBbw()){
                builder.append(StrUtils.appendLine("import com.ankamagames.dofus.util.types.BooleanByteWrapper;"));
            }
            if (field.isUseTypeManager()){
                builder.append(StrUtils.appendLine("import com.ankamagames.dofus.utils.ProtocolTypeManager;"));
            }
            if(field.getImportType() != null){
                builder.append(StrUtils.appendLine("import "))
                    .append(this.d2JsonModel.findMessageByName(messageModel.getName())
                        .getNamespace()
                    );
            }

        });
        return builder.toString();
    }

    private void generateEnum(final EnumModel enumModel){
        //TODO
    }





}

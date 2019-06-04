package fr.dofus.bdn.utils;

public final class TypeUtils {

    private TypeUtils() {

    }

    /**
     * Get readMethod from the write method.
     * d2json only give the write method.
     *
     * @param writeMethod The writeMethod
     * @return The readMethod
     */
    public static String getReadMethod(String writeMethod) {
        String var = "";
        switch (writeMethod) {
            case "writeUTF":
                var = "readUTF()";
                break;
            case "writeByte":
                var = "readByte()";
                break;
            case "writeVarShort":
                var = "readVarShort()";
                break;
            case "writeDouble":
                var = "readDouble()";
                break;
            case "writeShort":
                var = "readShort()";
                break;
            case "writeInt":
                var = "readInt()";
                break;
            case "writeVarInt":
                var = "readVarInt()";
                break;
            case "writeVarLong":
                var = "readVarLong()";
                break;
            case "writeBoolean":
                var = "readBoolean()";
                break;
            case "writeFloat":
                var = "readDouble()";
                break;
            case "writeUnsignedInt":
                var = "readUnsignedByte()";
                break;
        }
        return var;
    }


    /**
     * Convert As type into Java type
     *
     * @param type As type
     * @return Java type
     */
    public static String getJavaTypeFromAsType(String type) {
        if (type.contains("int")) {
            return type.contains("int64") ? "long" : "int";
        } else if (type.contains("float")) {
            return type.equals("float64") ? "double" : "float";
        } else if (type.contains("string")) {
            return "String";
        } else if (type.contains("bool")) {
            return "boolean";
        } else {
            return type;
        }
    }

    /**
     * Get the primitive type from the basic type.
     *
     * @param type basic type
     * @return String : primitive type
     */
    public static String getJavaPrimitiveType(String type) {
        switch (type) {
            case "int":
                return "Integer";
            case "long":
                return "Long";
            case "double":
                return "Double";
            case "float":
                return "Float";
            case "boolean":
                return "Boolean";
            default:
                throw new IllegalArgumentException("This type does not have a Primitive type : " + type);
        }
    }

    /**
     * Get the java list string from an As type
     * @param type As type
     * @return String : java list string
     */
    public static String getJavaListFromAsType(String type){
        return "List<" + getJavaPrimitiveType(getJavaTypeFromAsType(type)) + ">";
    }
}

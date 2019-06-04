package fr.dofus.bdn.utils;

public final class StrUtils {

    public static final String EMPTY_STRING = "";

    private StrUtils() {

    }

    /**
     * Return the java name for a getter.
     *
     * @param name String : the field name.
     * @return String.
     */
    public static String getGetterName(String name) {
        return "get" + name.substring(1).toUpperCase() + name.substring(1, name.length()) + "()";
    }

    /**
     * Return the java name for a setter.
     *
     * @param name String : the field name.
     * @param type String : java type of the field.
     * @return String.
     */
    public static String getSetterName(String name, String type) {
        return String.format("set%s(%s %s)",
            name.substring(1).toUpperCase() + name.substring(1, name.length()),
            type,
            name
        );
    }

    /**
     * Override the format method to add a tab before.
     * @param format The string ot format.
     * @param args The args.
     * @return The string formated with a tab before.
     */
    public static String formatTab(String format, Object... args) {
        return String.format("\t" + format, args);
    }

    /**
     * Append a new line
     * @param text Text to append
     * @return Text appened at a new line
     */
    public static String appendLine(final String text){
        return System.lineSeparator() + text;
    }
}

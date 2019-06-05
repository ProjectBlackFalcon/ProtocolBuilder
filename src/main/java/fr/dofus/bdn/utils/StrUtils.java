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
        return "get" + name.substring(0, 1).toUpperCase() + name.substring(1, name.length()) + "()";
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
     * @param text The string ot text.
     * @param args The args.
     * @return The string formated with a tab before.
     */
    public static String formatTab(String text, Object... args) {
        return formatTab(1, text, args);
    }

    /**
     * Override the format method to add a tab before.
     * @param tabs Number of tabs
     * @param text The string ot text.
     * @param args The args.
     * @return The string formated with a tab before.
     */
    public static String formatTab(int tabs, String text, Object... args) {
        StringBuilder tab = new StringBuilder();

        for (int i = 0; i < tabs; i++) {
            tab.append("\t");
        }

        String finalText = tab + text;
        return String.format(finalText, args);
    }

    /**
     * Append a new line
     * @param text Text to append
     * @return Text appened at a new line
     */
    public static String appendLine(final String text){
        return System.lineSeparator() + text;
    }

    /**
     * Append to new line and add X tabulations
     * @param text Text to append and tab
     * @param tabs number of tabulation
     * @return text tabbed to new line
     */
    public static String appendLineTabbed(final int tabs, final String text){
        return appendLine(tabString(tabs, text));
    }

    /**
     * Append to new line and add X tabulations
     * @param text Text to append and tab
     * @return text tabbed to new line
     */
    public static String appendLineTabbed(final String text){
        return appendLine(tabString(1, text));
    }


    /**
     * Add tabulation to the text
     * @param tabs number of tabs
     * @param text the text
     * @return the text tabbed
     */
    public static String tabString(int tabs, String text){
        StringBuilder tab = new StringBuilder();

        for (int i = 0; i < tabs; i++) {
            tab.append("\t");
        }

        return tab + text;
    }
}

package pl.epsi.jtts.parser;

public class JavaUtils {

    public static String getMildlySimpleName(Class<?> clazz) {
        var split = clazz.getName().split("\\.");
        return split[split.length - 1];
    }

    public static String getMildlySimpleName(String name) {
        var split = name.split("\\.");
        return split[split.length - 1];
    }

}

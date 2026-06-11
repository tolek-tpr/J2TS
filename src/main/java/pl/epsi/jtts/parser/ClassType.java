package pl.epsi.jtts.parser;

import java.lang.reflect.Modifier;

public enum ClassType {
    ENUM,
    INTERFACE,
    CLASS,
    ABSTRACT_CLASS,
    RECORD;

    public static ClassType fromClass(Class<?> clazz) {
        if (clazz.isEnum()) return ENUM;
        if (clazz.isInterface()) return INTERFACE;
        if (Modifier.isAbstract(clazz.getModifiers())) return ABSTRACT_CLASS;
        if (clazz.isRecord()) return RECORD;
        return CLASS;
    }
}

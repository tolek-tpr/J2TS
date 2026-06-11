package pl.epsi.jtts;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class JTTSConfig {

    public static String JTTS_TYPINGS_PATH = "jtts/ts/";

    public static final List<Class<? extends Annotation>> ignoreAnnotatedWith = new ArrayList<>();
    public static final List<Class<?>> ignoreClasses = new ArrayList<>();
    public static final List<Class<?>> ignoreParameters = new ArrayList<>();

    public static boolean convertFItoLambda = true;

}

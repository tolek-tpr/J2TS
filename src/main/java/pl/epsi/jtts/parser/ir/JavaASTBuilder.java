package pl.epsi.jtts.parser.ir;

import pl.epsi.jtts.JTTSConfig;
import pl.epsi.jtts.parser.ast.ClassNode;
import pl.epsi.jtts.parser.ast.DeclarationNode;
import pl.epsi.jtts.parser.ast.EnumNode;
import pl.epsi.jtts.parser.ast.InterfaceNode;
import pl.epsi.jtts.parser.ast.method.ConstructorNode;
import pl.epsi.jtts.parser.ast.method.MethodNode;
import pl.epsi.jtts.parser.ast.method.Modifier;
import pl.epsi.jtts.parser.ast.method.ParameterNode;
import pl.epsi.jtts.parser.ast.type.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class JavaASTBuilder {

    public static DeclarationNode compile(Class<?> clazz) {
        if (clazz.isSynthetic() || isAnnotatedWithAny(clazz.getDeclaredAnnotations())) return null;
        if (JTTSConfig.ignoreClasses.contains(clazz)) return null;

        if (clazz.isEnum()) return buildEnum(clazz);

        HashMap<TypeVariable<?>, TypeParameterNode> scope = new HashMap<>();
        List<GenericParameterNode> generics = getGenerics(clazz.getTypeParameters(), scope);
        List<MethodNode> methods = astMethods(clazz.getDeclaredMethods(), scope);
        List<ConstructorNode> constructors = astConstructors(clazz.getDeclaredConstructors(), scope);
        Type superType = clazz.getGenericSuperclass();
        NamedTypeNode superClass = null;

        if (superType != null && superType != Object.class) {
            TypeNode node = toTypeNode(superType, scope);

            superClass = (node instanceof NamedTypeNode n) ? n : null;
        }

        List<NamedTypeNode> interfaces =
                Arrays.stream(clazz.getGenericInterfaces())
                        .map(t -> toTypeNode(t, scope))
                        .filter(n -> n instanceof NamedTypeNode)
                        .map(n -> (NamedTypeNode) n)
                        .toList();

        if (clazz.isInterface()) {
            return new InterfaceNode(clazz.getName(), generics, interfaces, methods);
        } else {
            return new ClassNode(clazz.getName(), generics, interfaces, superClass, constructors, methods);
        }
    }

    public static DeclarationNode buildEnum(Class<?> clazz) {
        HashMap<TypeVariable<?>, TypeParameterNode> scope = new HashMap<>(); // Useless for enums btw
        List<MethodNode> methods = astMethods(clazz.getDeclaredMethods(), scope);
        List<NamedTypeNode> interfaces =
                Arrays.stream(clazz.getGenericInterfaces())
                        .map(t -> toTypeNode(t, scope))
                        .filter(n -> n instanceof NamedTypeNode)
                        .map(n -> (NamedTypeNode) n)
                        .toList();

        List<EnumConstantNode> enumConstants = new ArrayList<>();

        for (Object enumConstant : clazz.getEnumConstants()) {
            enumConstants.add(new EnumConstantNode(enumConstant.toString(), clazz.getName()));
        }

        return new EnumNode(clazz.getName(), enumConstants, interfaces, methods);
    }

    public static List<ConstructorNode> astConstructors(Constructor<?>[] constructors, Map<TypeVariable<?>, TypeParameterNode> parentScope) {
        List<ConstructorNode> constructorList = new ArrayList<>();

        for (Constructor<?> constructor : constructors) {
            if (isAnnotatedWithAny(constructor.getDeclaredAnnotations())) continue;

            Map<TypeVariable<?>, TypeParameterNode> scope =
                    new HashMap<>(parentScope);

            List<GenericParameterNode> generics =
                    getGenerics(constructor.getTypeParameters(), scope);

            List<ParameterNode> params = astParams(constructor, scope);
            EnumSet<Modifier> mods = getModifiers(constructor.getModifiers());
            constructorList.add(new ConstructorNode(params, generics, mods));
        }

        return constructorList;
    }

    public static List<MethodNode> astMethods(Method[] methods, HashMap<TypeVariable<?>, TypeParameterNode> parentScope) {
        List<MethodNode> methodList = new ArrayList<>();

        for (Method method : methods) {
            if (isAnnotatedWithAny(method.getDeclaredAnnotations())) continue;
            if (method.isSynthetic()) continue;

            Map<TypeVariable<?>, TypeParameterNode> scope = new HashMap<>(parentScope);
            List<GenericParameterNode> generics = getGenerics(method.getTypeParameters(), scope);
            List<ParameterNode> params = astParams(method, scope);
            TypeNode returnType = toTypeNode(method.getGenericReturnType(), scope);
            EnumSet<Modifier> mods = getModifiers(method.getModifiers());

            methodList.add(new MethodNode(method.getName(), generics, params, returnType, mods));
        }

        return methodList;
    }

    public static List<ParameterNode> astParams(Executable method, Map<TypeVariable<?>, TypeParameterNode> scope) {
        List<ParameterNode> params = new ArrayList<>();

        Type[] types = method.getGenericParameterTypes();
        Parameter[] parameters = method.getParameters();

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Type type = types[i];

            if (JTTSConfig.ignoreParameters.contains(parameter.getType())) continue;
            if (JTTSConfig.convertFItoLambda && isFunctionalInterface(type)) {
                TypeNode lambdaType = functionalInterfaceToLambda(type, scope);

                params.add(new ParameterNode(
                        parameter.getName(),
                        lambdaType,
                        false,
                        parameter.isVarArgs()));

                continue;
            }

            TypeNode tn = toTypeNode(type, scope);

            params.add(new ParameterNode(
                    parameter.getName(),
                    tn,
                    false,
                    parameter.isVarArgs()));
        }

        return params;
    }

    private static TypeNode functionalInterfaceToLambda(Type type, Map<TypeVariable<?>, TypeParameterNode> scope) {
        Class<?> raw;
        Map<TypeVariable<?>, Type> substitutions;

        if (type instanceof ParameterizedType pt) {
            raw = (Class<?>) pt.getRawType();
            substitutions = buildSubstitutions(pt);
        } else {
            raw = (Class<?>) type;
            substitutions = Map.of();
        }

        Method sam = findFunctionalMethod(raw);

        List<ParameterNode> params =
                IntStream.range(0, sam.getParameterCount())
                        .mapToObj(i -> new ParameterNode(
                                "arg" + i,
                                substituteToTypeNode(
                                        sam.getGenericParameterTypes()[i],
                                        substitutions,
                                        scope),
                                false,
                                false))
                        .toList();

        TypeNode returnType =
                substituteToTypeNode(
                        sam.getGenericReturnType(),
                        substitutions,
                        scope);

        return new LambdaType(returnType, params);
    }

    private static Map<TypeVariable<?>, Type> buildSubstitutions(
            ParameterizedType type) {

        Class<?> raw = (Class<?>) type.getRawType();

        TypeVariable<?>[] vars = raw.getTypeParameters();
        Type[] args = type.getActualTypeArguments();

        Map<TypeVariable<?>, Type> map = new HashMap<>();

        for (int i = 0; i < vars.length; i++) {
            map.put(vars[i], args[i]);
        }

        return map;
    }

    private static Method findFunctionalMethod(Class<?> clazz) {
        Method result = null;

        for (Method m : clazz.getMethods()) {
            if (!java.lang.reflect.Modifier.isAbstract(m.getModifiers()))
                continue;

            if (m.getDeclaringClass() == Object.class)
                continue;

            if (result != null) {
                throw new IllegalStateException(
                        clazz.getName() + " has multiple abstract methods");
            }

            result = m;
        }

        return result;
    }

    private static TypeNode substituteToTypeNode(Type type, Map<TypeVariable<?>, Type> substitutions,
                                                 Map<TypeVariable<?>, TypeParameterNode> scope) {

        if (type instanceof TypeVariable<?> tv) {
            Type replacement = substitutions.get(tv);

            if (replacement != null) {
                return toTypeNode(replacement, scope);
            }

            return toTypeNode(tv, scope);
        }

        if (type instanceof ParameterizedType pt) {
            return new NamedTypeNode(
                    ((Class<?>) pt.getRawType()).getName(),
                    Arrays.stream(pt.getActualTypeArguments())
                            .map(t -> substituteToTypeNode(
                                    t,
                                    substitutions,
                                    scope))
                            .toList());
        }

        if (type instanceof GenericArrayType gat) {
            return new ArrayTypeNode(
                    substituteToTypeNode(
                            gat.getGenericComponentType(),
                            substitutions,
                            scope));
        }

        return toTypeNode(type, scope);
    }

    private static boolean isFunctionalInterface(Type type) {
        Class<?> clazz = null;
        if (type instanceof Class<?> c) {
            clazz = c;
        } else if (type instanceof ParameterizedType p) {
            clazz = (Class<?>) p.getRawType();
        }

        return clazz != null &&
                clazz.isAnnotationPresent(FunctionalInterface.class);
    }

    public static List<GenericParameterNode> getGenerics(TypeVariable<?>[] tvs, Map<TypeVariable<?>, TypeParameterNode> scope) {
        List<GenericParameterNode> list = new ArrayList<>();

        for (TypeVariable<?> tv : tvs) {
            TypeParameterNode node = new TypeParameterNode(tv.getName());

            scope.put(tv, node);

            list.add(new GenericParameterNode(tv.getName(),
                    Arrays.stream(tv.getBounds())
                            .map((type) -> toTypeNode(type, scope))
                            .toList()));
        }

        return list;
    }

    public static TypeNode toTypeNode(Type type, Map<TypeVariable<?>, TypeParameterNode> scope) {
        if (type instanceof Class<?> clazz) {
            if (clazz.isArray()) {
                return new ArrayTypeNode(toTypeNode(clazz.getComponentType(), scope));
            }

            return new NamedTypeNode(clazz.getName(), List.of());
        }

        if (type instanceof ParameterizedType parameterized) {
            return new NamedTypeNode(((Class<?>) parameterized.getRawType()).getName(),
                    Arrays.stream(parameterized.getActualTypeArguments())
                            .map(t -> toTypeNode(t, scope))
                            .toList());
        }

        if (type instanceof TypeVariable<?> variable) {
            TypeParameterNode node = scope.get(variable);
            if (node != null) return node;

            return new TypeParameterNode(variable.getName());
        }

        if (type instanceof WildcardType wildcard) {
            TypeNode upper = wildcard.getUpperBounds().length == 0
                    ? null
                    : toTypeNode(wildcard.getUpperBounds()[0], scope);

            TypeNode lower = wildcard.getLowerBounds().length == 0
                    ? null
                    : toTypeNode(wildcard.getLowerBounds()[0], scope);

            return new WildcardTypeNode(upper, lower);
        }

        if (type instanceof GenericArrayType array) {
            return new ArrayTypeNode(toTypeNode(array.getGenericComponentType(), scope));
        }

        throw new IllegalStateException("Unknown type: " + type.getClass());
    }

    public static EnumSet<Modifier> getModifiers(int mods) {
        EnumSet<Modifier> modifiers = EnumSet.noneOf(Modifier.class);
        if (java.lang.reflect.Modifier.isStatic(mods)) modifiers.add(Modifier.STATIC);
        if (java.lang.reflect.Modifier.isFinal(mods)) modifiers.add(Modifier.READONLY);

        if (java.lang.reflect.Modifier.isPrivate(mods)) {
            modifiers.add(Modifier.PRIVATE);
        } else if (java.lang.reflect.Modifier.isPublic(mods)) {
            modifiers.add(Modifier.PUBLIC);
        } else if (java.lang.reflect.Modifier.isProtected(mods)) {
            modifiers.add(Modifier.PROTECTED);
        }

        if (java.lang.reflect.Modifier.isAbstract(mods)) modifiers.add(Modifier.ABSTRACT);

        return modifiers;
    }

    private static boolean isAnnotatedWithAny(Annotation[] source) {
        for (Annotation annotation : source) {
            if (JTTSConfig.ignoreAnnotatedWith.contains(annotation.annotationType())) return true;
        }
        return false;
    }

}

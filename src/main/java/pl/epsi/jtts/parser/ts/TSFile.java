package pl.epsi.jtts.parser.ts;

import pl.epsi.jtts.parser.CodeWriter;
import pl.epsi.jtts.parser.JavaUtils;
import pl.epsi.jtts.parser.TokenStream;
import pl.epsi.jtts.parser.ast.DeclarationNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TSFile {

    private final HashMap<String, Set<String>> imports = new HashMap<>();
    private final HashMap<String, String> importToPath = new HashMap<>();
    private final HashMap<String, Integer> aliasCounter = new HashMap<>();

    private String requestImport(String javaType) {
        switch (javaType) {
            case "byte", "short", "int", "long", "float", "double",
                 "java.lang.Byte", "java.lang.Short", "java.lang.Integer",
                 "java.lang.Float", "java.lang.Double" -> {
                return "number";
            }
            case "boolean", "java.lang.Boolean" -> {
                return "boolean";
            }
            case "java.lang.String" -> {
                return "string";
            }
            case "java.lang.Object" -> {
                return "any"; // Record<string, any>
            }
            case "void", "java.lang.Void" -> {
                return "void";
            }
        }

        if (javaType.lastIndexOf('.') == -1) return javaType;

        String path = javaType.substring(0, javaType.lastIndexOf('.'));
        String typeName = javaType.substring(javaType.lastIndexOf('.') + 1);

        return requestImport(path, typeName);
    }

    private String requestImport(String path, String typeName) {
        if ((path + "." + typeName).equals(this.cn.name)) return typeName;

        if (importToPath.containsKey(typeName)) {
            if (importToPath.get(typeName).equals(path)) return typeName;

            aliasCounter.put(typeName, aliasCounter.getOrDefault(typeName, 0) + 1);
            String newTypeName = typeName + aliasCounter.get(typeName);
            importToPath.put(newTypeName, path);
            imports.computeIfAbsent(path, (c) -> new HashSet<>()).add(typeName + " as " + newTypeName);

            return newTypeName;
        } else {
            importToPath.put(typeName, path);
            imports.computeIfAbsent(path, (c) -> new HashSet<>()).add(typeName);
            return typeName;
        }
    }

    public final String name;
    private final DeclarationNode cn;

    public TSFile(DeclarationNode cn) {
        this.name = JavaUtils.getMildlySimpleName(cn.name);
        this.cn = cn;
    }

    public String write() {
        TokenStream writer = new TokenStream();
        cn.toTokens(writer);

        CodeWriter cw = new CodeWriter();

        for (TokenStream.Token token : writer.tokens) {
            if (token instanceof TokenStream.KeywordToken kt) {
                cw.append(kt.getString());
                cw.append(" ");
            } else if (token instanceof TokenStream.LiteralToken lt) {
                cw.append(lt.getString());
            } else if (token instanceof TokenStream.LBrace) {
                cw.append(" ");
                cw.append(token.getString());
                cw.indent();
                cw.newline();
                cw.newline();
            } else if (token instanceof TokenStream.RBrace) {
                cw.dedent();
                cw.newline();
                cw.append(token.getString());
                cw.newline();
            } else if (token instanceof TokenStream.LParen) {
                cw.append(token.getString());
            } else if (token instanceof TokenStream.RParen) {
                cw.append(token.getString());
            } else if (token instanceof TokenStream.Semicolon) {
                cw.append(token.getString());
                cw.newline();
            } else if (token instanceof TokenStream.Colon) {
                cw.append(token.getString());
                cw.append(" ");
            } else if (token instanceof TokenStream.TypeToken) {
                cw.append(this.requestImport(((TokenStream.TypeToken) token).typeLiteral));
            } else if (token instanceof TokenStream.LAngleBracket) {
                cw.append(token.getString());
            } else if (token instanceof TokenStream.RAngleBracket) {
                cw.append(token.getString());
            } else if (token instanceof TokenStream.Comma) {
                cw.append(token.getString());
                cw.append(" ");
            } else if (token instanceof TokenStream.VaragToken) {
                cw.append(token.getString());
            } else if (token instanceof TokenStream.QuestionMark) {
                cw.append(token.getString());
            } else if (token instanceof TokenStream.And) {
                cw.append(" ");
                cw.append(token.getString());
                cw.append(" ");
            } else if (token instanceof TokenStream.LBracket) {
                cw.append(token.getString());
            } else if (token instanceof TokenStream.RBracket) {
                cw.append(token.getString());
            }
        }

        return this.buildImports() + cw;
    }

    public String buildImports() {
        StringBuilder sb = new StringBuilder();

        imports.forEach((path, types) -> {
            sb.append("import { ");

            int i = 0;
            for (String t : types) {
                if (i++ > 0) sb.append(", ");
                sb.append(t);
            }

            sb.append(" } from \"@pts/").append(path.replace('.', '/')).append("\";\n");
        });

        return sb + "\n";
    }

}

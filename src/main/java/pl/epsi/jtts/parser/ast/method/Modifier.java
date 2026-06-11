package pl.epsi.jtts.parser.ast.method;

import pl.epsi.jtts.parser.TokenStream;

public enum Modifier {
    PUBLIC,
    PROTECTED,
    PRIVATE,

    STATIC,
    ABSTRACT,
    READONLY,
    OVERRIDE,
    DECLARE;

    public void toTokens(TokenStream writer) {
        switch (this) {
            case PUBLIC, PROTECTED -> writer.keyword("public");
            case PRIVATE -> writer.keyword("private");
            case STATIC -> writer.keyword("static");
        }
    }

}

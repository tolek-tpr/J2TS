package pl.epsi.jtts.parser;

import java.util.ArrayList;
import java.util.List;

public class TokenStream {

    public final List<Token> tokens = new ArrayList<>();

    public void write(String literal) {
        tokens.add(new LiteralToken(literal));
    }

    public void writeType(String typeName) {
        tokens.add(new TypeToken(typeName));
    }

    public void writeName(String name) { write(JavaUtils.getMildlySimpleName(name)); }

    public void keyword(String keyword) { tokens.add(new KeywordToken(keyword)); }

    public void nl() { tokens.add(new LiteralToken("\n")); }

    public void rbrace() {
        tokens.add(new RBrace());
    }

    public void lbrace() {
        tokens.add(new LBrace());
    }

    public void rbracket() {
        tokens.add(new RBracket());
    }

    public void lbracket() {
        tokens.add(new LBracket());
    }

    public void langlebracket() { tokens.add(new LAngleBracket()); }

    public void ranglebracket() { tokens.add(new RAngleBracket()); }

    public void colon() { tokens.add(new Colon()); }

    public void varag() { tokens.add(new VaragToken()); }

    public void questionmark() { tokens.add(new QuestionMark()); }

    public void lparen() { tokens.add(new LParen()); }

    public void rparen() { tokens.add(new RParen()); }

    public void comma() { tokens.add(new Comma()); }

    public void and() { tokens.add(new And()); }

    public void semicolon() { tokens.add(new Semicolon()); }

    public interface Token {
        String getString();
    }

    public static class LiteralToken implements Token {
        public final String literal;

        public LiteralToken(String literal) {
            this.literal = literal;
        }

        @Override
        public String getString() {
            return literal;
        }
    }

    public static class KeywordToken implements Token {
        public final String keyword;

        public KeywordToken(String keyword) {
            this.keyword = keyword;
        }

        @Override
        public String getString() {
            return keyword;
        }
    }

    public static class TypeToken implements Token {
        public final String typeLiteral;

        public TypeToken(String typeLiteral) {
            this.typeLiteral = typeLiteral;
        }

        @Override
        public String getString() {
            return typeLiteral;
        }
    }

    public static class LBrace implements Token { public String getString() { return "{"; } }
    public static class RBrace implements Token { public String getString() { return "}"; } }
    public static class Comma implements Token { public String getString() { return ","; } }
    public static class And implements Token { public String getString() { return "&"; } }
    public static class Semicolon implements Token { public String getString() { return ";"; } }
    public static class Colon implements Token { public String getString() { return ":"; } }
    public static class LParen implements Token { public String getString() { return "("; } }
    public static class RParen implements Token { public String getString() { return ")"; } }
    public static class LAngleBracket implements Token { public String getString() { return "<"; } }
    public static class RAngleBracket implements Token { public String getString() { return ">"; } }
    public static class VaragToken implements Token { public String getString() { return "..."; } }
    public static class QuestionMark implements Token { public String getString() { return "?"; } }
    public static class LBracket implements Token { public String getString() { return "["; } }
    public static class RBracket implements Token { public String getString() { return "]"; } }

}

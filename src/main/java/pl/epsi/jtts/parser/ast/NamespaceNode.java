package pl.epsi.jtts.parser.ast;

import pl.epsi.jtts.parser.TokenStream;

import java.util.List;

public class NamespaceNode extends DeclarationNode {

    private final List<DeclarationNode> declarations;

    public NamespaceNode(String name, List<DeclarationNode> declarations) {
        super(name);
        this.declarations = declarations;
    }

    @Override
    public void toTokens(TokenStream writer) {
        writer.keyword("declare");
        writer.keyword("namespace");
        writer.writeName(this.name);
        writer.lbrace();
        declarations.forEach(d -> d.toTokens(writer));
        writer.rbrace();
    }
}

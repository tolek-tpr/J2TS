package pl.epsi.jtts.parser.ast.type;

import pl.epsi.jtts.parser.TokenStream;

public class TypeParameterNode implements TypeNode {

    private final String name;

    public TypeParameterNode(String name) {
        this.name = name;
    }

    @Override
    public void toTokens(TokenStream writer) {
        writer.writeName(name);
    }
}

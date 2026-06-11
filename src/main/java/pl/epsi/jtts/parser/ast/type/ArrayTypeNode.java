package pl.epsi.jtts.parser.ast.type;

import pl.epsi.jtts.parser.TokenStream;

public class ArrayTypeNode implements TypeNode {

    private final TypeNode componentType;

    public ArrayTypeNode(TypeNode componentType) {
        this.componentType = componentType;
    }

    @Override
    public void toTokens(TokenStream writer) {
        componentType.toTokens(writer);
        writer.lbracket();
        writer.rbracket();
    }
}

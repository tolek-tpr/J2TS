package pl.epsi.jtts.parser.ast.type;

import pl.epsi.jtts.parser.TokenStream;

public class WildcardTypeNode implements TypeNode {

    private final TypeNode upperBound, lowerBound;

    public WildcardTypeNode(TypeNode upperBound, TypeNode lowerBound) {
        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
    }

    @Override
    public void toTokens(TokenStream writer) {
        if (upperBound != null) {
            upperBound.toTokens(writer);
            return;
        }

        if (lowerBound != null) {
            lowerBound.toTokens(writer);
            return;
        }

        writer.writeType("unknown");
    }
}

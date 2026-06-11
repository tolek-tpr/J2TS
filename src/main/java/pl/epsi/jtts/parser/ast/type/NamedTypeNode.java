package pl.epsi.jtts.parser.ast.type;

import pl.epsi.jtts.parser.TokenStream;

import java.util.List;

public class NamedTypeNode implements TypeNode {

    private final String name;
    private final List<TypeNode> typeArguments;

    public NamedTypeNode(String name, List<TypeNode> typeArguments) {
        this.name = name;
        this.typeArguments = typeArguments;
    }

    public String getName() { return this.name; }

    @Override
    public void toTokens(TokenStream writer) {
        writer.writeType(name);

        if (typeArguments.isEmpty()) return;

        writer.langlebracket();
        for (int i = 0; i < typeArguments.size(); i++) {
            typeArguments.get(i).toTokens(writer);
            if (i != typeArguments.size() - 1) writer.comma();
        }
        writer.ranglebracket();
    }

}

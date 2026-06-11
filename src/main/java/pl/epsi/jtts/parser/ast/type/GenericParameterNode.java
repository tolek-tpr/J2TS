package pl.epsi.jtts.parser.ast.type;

import pl.epsi.jtts.parser.TokenStream;

import java.util.List;

public class GenericParameterNode implements TypeNode {

    private final String name;
    private final List<TypeNode> bounds;

    public GenericParameterNode(String name, List<TypeNode> bounds) {
        this.name = name;
        this.bounds = bounds;
    }

    @Override
    public void toTokens(TokenStream writer) {
        writer.writeType(name);

        if (!bounds.isEmpty() && !(bounds.size() == 1 && bounds.get(0) instanceof NamedTypeNode n && n.getName().equals("java.lang.Object"))) {
            writer.keyword(" extends");

            for (int i = 0; i < bounds.size(); i++) {
                bounds.get(i).toTokens(writer);

                if (i != bounds.size() - 1) {
                    writer.and();
                }
            }
        }
    }

}

package pl.epsi.jtts.parser.ast.method;

import pl.epsi.jtts.parser.TokenStream;
import pl.epsi.jtts.parser.ast.Node;
import pl.epsi.jtts.parser.ast.type.GenericParameterNode;

import java.util.EnumSet;
import java.util.List;

public class ConstructorNode implements Node {

    private final List<ParameterNode> parameters;
    private final List<GenericParameterNode> generics;
    private final EnumSet<Modifier> modifiers;

    public ConstructorNode(List<ParameterNode> parameters, List<GenericParameterNode> generics, EnumSet<Modifier> modifiers) {
        this.parameters = parameters;
        this.generics = generics;
        this.modifiers = modifiers;
    }

    @Override
    public void toTokens(TokenStream writer) {
        modifiers.forEach(m -> m.toTokens(writer));
        writer.writeName("constructor");
        writer.lparen();

        for (int i = 0; i < parameters.size(); i++) {
            parameters.get(i).toTokens(writer);

            if (i != parameters.size() - 1) writer.comma();
        }

        writer.rparen();
        writer.semicolon();
    }

}

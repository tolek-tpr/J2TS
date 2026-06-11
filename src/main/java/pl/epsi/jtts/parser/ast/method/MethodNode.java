package pl.epsi.jtts.parser.ast.method;

import pl.epsi.jtts.parser.TokenStream;
import pl.epsi.jtts.parser.ast.DeclarationNode;
import pl.epsi.jtts.parser.ast.type.GenericParameterNode;
import pl.epsi.jtts.parser.ast.type.TypeNode;

import java.util.EnumSet;
import java.util.List;

public class MethodNode extends DeclarationNode {

    public final List<GenericParameterNode> genericParameters;

    public final List<ParameterNode> parameters;

    public final TypeNode returnType;

    public final EnumSet<Modifier> modifiers;

    public MethodNode(String name, List<GenericParameterNode> genericParameters, List<ParameterNode> parameters, TypeNode returnType, EnumSet<Modifier> modifiers) {
        super(name);
        this.genericParameters = genericParameters;
        this.parameters = parameters;
        this.returnType = returnType;
        this.modifiers = modifiers;
    }

    public void clearModifiers() { modifiers.clear(); }

    @Override
    public void toTokens(TokenStream writer) {
        modifiers.forEach(m -> m.toTokens(writer));
        writer.writeName(this.name);

        if (!genericParameters.isEmpty()) {
            writer.langlebracket();

            for (int i = 0; i < genericParameters.size(); i++) {
                genericParameters.get(i).toTokens(writer);

                if (i != genericParameters.size() - 1) writer.comma();
            }

            writer.ranglebracket();
        }

        writer.lparen();
        for (int i = 0; i < parameters.size(); i++) {
            parameters.get(i).toTokens(writer);

            if (i != parameters.size() - 1) writer.comma();
        }
        writer.rparen();
        writer.colon();
        returnType.toTokens(writer);
        writer.semicolon();
    }
}

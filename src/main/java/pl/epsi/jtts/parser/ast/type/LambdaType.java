package pl.epsi.jtts.parser.ast.type;

import pl.epsi.jtts.parser.TokenStream;
import pl.epsi.jtts.parser.ast.method.ParameterNode;

import java.util.List;

public class LambdaType implements TypeNode {

    private final TypeNode returnType;
    private final List<ParameterNode> parameters;

    public LambdaType(TypeNode returnType, List<ParameterNode> parameters) {
        this.returnType = returnType;
        this.parameters = parameters;
    }

    @Override
    public void toTokens(TokenStream writer) {
        writer.lparen();
        for (int i = 0; i < parameters.size(); i++) {
            parameters.get(i).toTokens(writer);

            if (i != parameters.size() - 1) writer.comma();
        }
        writer.rparen();
        writer.write(" => ");
        returnType.toTokens(writer);
    }

}

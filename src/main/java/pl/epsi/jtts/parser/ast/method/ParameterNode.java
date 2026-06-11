package pl.epsi.jtts.parser.ast.method;

import pl.epsi.jtts.parser.TokenStream;
import pl.epsi.jtts.parser.ast.DeclarationNode;
import pl.epsi.jtts.parser.ast.type.TypeNode;

public class ParameterNode extends DeclarationNode {

    private final TypeNode type;
    private final boolean optional;
    private final boolean varargs;

    public ParameterNode(String name, TypeNode type, boolean optional, boolean varargs) {
        super(name);
        this.type = type;
        this.optional = optional;
        this.varargs = varargs;
    }

    @Override
    public void toTokens(TokenStream writer) {
        if (varargs) writer.varag();
        writer.write(this.name);
        if (optional) writer.questionmark();
        writer.colon();
        type.toTokens(writer);
    }

}

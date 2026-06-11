package pl.epsi.jtts.parser.ast;

import pl.epsi.jtts.parser.TokenStream;
import pl.epsi.jtts.parser.ast.method.MethodNode;
import pl.epsi.jtts.parser.ast.type.GenericParameterNode;
import pl.epsi.jtts.parser.ast.type.NamedTypeNode;

import java.util.List;

public class InterfaceNode extends DeclarationNode {

    private final List<GenericParameterNode> genericParameters;

    private final List<NamedTypeNode> superInterfaces;

    private final List<MethodNode> methods;

    public InterfaceNode(String name, List<GenericParameterNode> genericParameters, List<NamedTypeNode> superInterfaces, List<MethodNode> methods) {
        super(name);
        methods.forEach(MethodNode::clearModifiers);
        this.genericParameters = genericParameters;
        this.superInterfaces = superInterfaces;
        this.methods = methods;
    }

    @Override
    public void toTokens(TokenStream writer) {
        writer.keyword("export");
        writer.keyword("declare");
        writer.keyword("interface");
        writer.writeName(this.name);

        if (!genericParameters.isEmpty()) {
            writer.langlebracket();

            for (int i = 0; i < genericParameters.size(); i++) {
                GenericParameterNode gp = genericParameters.get(i);
                gp.toTokens(writer);

                if (i != genericParameters.size() - 1) writer.comma();
            }

            writer.ranglebracket();
        }

        if (!superInterfaces.isEmpty()) {
            writer.keyword(" extends");

            for (int i = 0; i < superInterfaces.size(); i++) {
                NamedTypeNode iface = superInterfaces.get(i);
                iface.toTokens(writer);

                if (i != superInterfaces.size() - 1) writer.comma();
            }
        }

        writer.lbrace();
        methods.forEach(m -> m.toTokens(writer));
        writer.rbrace();
    }

}

package pl.epsi.jtts.parser.ast;

import pl.epsi.jtts.parser.TokenStream;
import pl.epsi.jtts.parser.ast.method.ConstructorNode;
import pl.epsi.jtts.parser.ast.method.MethodNode;
import pl.epsi.jtts.parser.ast.type.GenericParameterNode;
import pl.epsi.jtts.parser.ast.type.NamedTypeNode;

import java.util.List;

public class ClassNode extends DeclarationNode {

    private final List<GenericParameterNode> genericParameters;
    private final List<NamedTypeNode> interfaces;
    private final NamedTypeNode superClass;

    private final List<ConstructorNode> constructors;
    private final List<MethodNode> methods;

    public ClassNode(String name, List<GenericParameterNode> genericParameters, List<NamedTypeNode> interfaces, NamedTypeNode superClass, List<ConstructorNode> constructors, List<MethodNode> methods) {
        super(name);
        this.genericParameters = genericParameters;
        this.interfaces = interfaces;
        this.superClass = superClass;
        this.constructors = constructors;
        this.methods = methods;
    }

    @Override
    public void toTokens(TokenStream writer) {
        writer.keyword("export");
        writer.keyword("declare");
        writer.keyword("class");
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

        if (superClass != null) {
            writer.keyword(" extends");
            superClass.toTokens(writer);
        }

        if (!interfaces.isEmpty()) {
            writer.keyword(" implements");

            for (int i = 0; i < interfaces.size(); i++) {
                NamedTypeNode iface = interfaces.get(i);
                iface.toTokens(writer);

                if (i != interfaces.size() - 1) writer.comma();
            }
        }

        writer.lbrace();
        constructors.forEach(c -> c.toTokens(writer));
        methods.forEach(m -> m.toTokens(writer));
        writer.rbrace();
    }

}

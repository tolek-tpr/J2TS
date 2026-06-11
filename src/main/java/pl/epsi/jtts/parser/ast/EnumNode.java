package pl.epsi.jtts.parser.ast;

import pl.epsi.jtts.parser.TokenStream;
import pl.epsi.jtts.parser.ast.method.MethodNode;
import pl.epsi.jtts.parser.ast.type.EnumConstantNode;
import pl.epsi.jtts.parser.ast.type.NamedTypeNode;

import java.util.List;

public class EnumNode extends DeclarationNode {

    private final List<EnumConstantNode> constants;

    private final List<NamedTypeNode> superInterfaces;

    private final List<MethodNode> methods;

    public EnumNode(String name, List<EnumConstantNode> constants, List<NamedTypeNode> superInterfaces, List<MethodNode> methods) {
        super(name);
        this.constants = constants;
        this.superInterfaces = superInterfaces;
        this.methods = methods;
    }

    @Override
    public void toTokens(TokenStream writer) {
        writer.keyword("export");
        writer.keyword("declare");
        writer.keyword("class");
        writer.writeName(this.name);

        if (!superInterfaces.isEmpty()) {
            writer.keyword(" implements");

            for (int i = 0; i < superInterfaces.size(); i++) {
                NamedTypeNode iface = superInterfaces.get(i);
                iface.toTokens(writer);

                if (i != superInterfaces.size() - 1) writer.comma();
            }
        }

        writer.lbrace();
        constants.forEach(c -> c.toTokens(writer));
        methods.forEach(m -> m.toTokens(writer));
        writer.rbrace();
    }

}

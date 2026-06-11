package pl.epsi.jtts.parser.ast.type;

import pl.epsi.jtts.parser.JavaUtils;
import pl.epsi.jtts.parser.TokenStream;
import pl.epsi.jtts.parser.ast.DeclarationNode;

public class EnumConstantNode extends DeclarationNode {

    private final String parentName;

    public EnumConstantNode(String name, String parentName) {
        super(name);
        this.parentName = parentName;
    }

    @Override
    public void toTokens(TokenStream writer) {
        writer.keyword("static");
        writer.keyword("readonly");
        writer.write(this.name);
        writer.colon();
        writer.write(JavaUtils.getMildlySimpleName(parentName));
        writer.semicolon();
    }
}

package pl.epsi.jtts.parser.ast;

public abstract class DeclarationNode implements Node {

    public final String name;

    public DeclarationNode(String name) {
        this.name = name;
    }

}

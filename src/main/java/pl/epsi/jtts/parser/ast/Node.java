package pl.epsi.jtts.parser.ast;

import pl.epsi.jtts.parser.TokenStream;

public interface Node {
    void toTokens(TokenStream writer);
}

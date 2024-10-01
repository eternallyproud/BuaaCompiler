package frontend.node;

import frontend.token.Token;

//<Number> ::= <IntConst>
public class NumberNode extends Node {
    private final Token intConstToken;

    public NumberNode(Token intConstToken) {
        super(NodeType.NUMBER);
        this.intConstToken = intConstToken;
    }

    @Override
    public String toString() {
        return "" + intConstToken + nodeType;
    }
}

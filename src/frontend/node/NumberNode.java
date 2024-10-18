package frontend.node;

import frontend.symbol.DataType;
import frontend.token.Token;

//<Number> ::= <IntConst>
public class NumberNode extends Node {
    private final Token intConstToken;

    public NumberNode(Token intConstToken) {
        super(NodeType.NUMBER);
        this.intConstToken = intConstToken;
    }

    public DataType getDataType() {
        return DataType.INT;
    }

    @Override
    public String toString() {
        return "" + intConstToken + nodeType;
    }
}

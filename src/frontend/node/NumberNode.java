package frontend.node;

import frontend.ir.value.Constant;
import frontend.ir.value.Value;
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

    public int calculateValue() {
        return Integer.parseInt(intConstToken.getContent());
    }

    @Override
    public Value buildIR() {
        return new Constant.Int(Integer.parseInt(intConstToken.getContent()));
    }

    @Override
    public String toString() {
        return "" + intConstToken + nodeType;
    }
}

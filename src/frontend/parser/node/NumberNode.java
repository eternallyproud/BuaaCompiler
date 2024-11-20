package frontend.parser.node;

import frontend.ir.llvm.value.Constant;
import frontend.ir.llvm.value.Value;
import frontend.semantic.symbol.DataType;
import frontend.lexer.token.Token;

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

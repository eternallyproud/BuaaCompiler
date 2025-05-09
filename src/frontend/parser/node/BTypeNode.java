package frontend.parser.node;

import frontend.ir.llvm.value.type.ValueType;
import frontend.semantic.symbol.DataType;
import frontend.lexer.token.Token;

import java.util.Objects;

//<BType> ::= 'int' | 'char'
public class BTypeNode extends Node {
    private final Token intToken;
    private final Token charToken;

    public BTypeNode(Token intToken, Token charToken) {
        super(NodeType.BType);
        this.intToken = intToken;
        this.charToken = charToken;
    }

    public DataType getBType() {
        return intToken == null ? DataType.CHAR : DataType.INT;
    }

    public ValueType getValueType() {
        return getBType().getValueType(1);
    }

    @Override
    public String toString() {
        return Objects.toString(intToken, "") +
                Objects.toString(charToken, "");
    }
}

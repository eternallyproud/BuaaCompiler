package frontend.node;

import frontend.symbol.DataType;
import frontend.token.Token;

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

    @Override
    public String toString() {
        return Objects.toString(intToken, "") +
                Objects.toString(charToken, "");
    }
}

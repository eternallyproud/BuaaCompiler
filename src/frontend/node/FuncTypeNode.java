package frontend.node;

import frontend.token.Token;

import java.util.Objects;

//<FuncType> ::= 'void' | 'int' | 'char'
public class FuncTypeNode extends Node {
    private final Token voidToken;
    private final Token intToken;
    private final Token charToken;

    public FuncTypeNode(Token voidToken, Token intToken, Token charToken) {
        super(NodeType.FUNC_TYPE);
        this.voidToken = voidToken;
        this.intToken = intToken;
        this.charToken = charToken;
    }

    @Override
    public String toString() {
        return Objects.toString(voidToken, "") +
                Objects.toString(intToken, "") +
                Objects.toString(charToken, "") +
                nodeType;
    }
}

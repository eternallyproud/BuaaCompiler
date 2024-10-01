package frontend.node;

import frontend.token.Token;

import java.util.Objects;

//<UnaryOp> ::= '+' | '-' | '!'
public class UnaryOpNode extends Node {
    private final Token plusToken;
    private final Token minuToken;
    private final Token notToken;

    public UnaryOpNode(Token plusToken, Token minuToken, Token notToken) {
        super(NodeType.UNARY_OP);
        this.plusToken = plusToken;
        this.minuToken = minuToken;
        this.notToken = notToken;
    }

    @Override
    public String toString() {
        return Objects.toString(plusToken, "") +
                Objects.toString(minuToken, "") +
                Objects.toString(notToken, "") + nodeType;
    }
}

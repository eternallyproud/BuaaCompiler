package frontend.parser.node;

import frontend.lexer.token.Token;
import frontend.lexer.token.TokenType;

//<UnaryOp> ::= '+' | '-' | '!'
public class UnaryOpNode extends Node {
    private final Token opToken;

    public UnaryOpNode(Token opToken) {
        super(NodeType.UNARY_OP);
        this.opToken = opToken;
    }

    public TokenType getOpTokenType() {
        return opToken.getType();
    }

    @Override
    public String toString() {
        return "" + opToken + nodeType;
    }
}

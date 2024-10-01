package frontend.node;

import frontend.token.Token;

import java.util.Objects;

//<MulExp> ::=  <UnaryExp> | <MulExp> ('*' | '/' | '%') <UnaryExp>
public class MulExpNode extends Node {
    public final MulExpNode mulExpNode;
    public final Token mulToken;
    public final UnaryExpNode unaryExpNode;

    public MulExpNode(MulExpNode mulExpNode, Token mulToken, UnaryExpNode unaryExpNode) {
        super(NodeType.MUL_EXP);
        this.mulExpNode = mulExpNode;
        this.mulToken = mulToken;
        this.unaryExpNode = unaryExpNode;
    }

    @Override
    public String toString() {
        return Objects.toString(mulExpNode, "") +
                Objects.toString(mulToken, "") + unaryExpNode + nodeType;
    }
}

package frontend.node;

import frontend.symbol.DataType;
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

    public DataType getDataType() {
        return unaryExpNode.getDataType();
    }

    @Override
    public void checkSemantic() {
        //<UnaryExp>
        if (mulExpNode == null) {
            unaryExpNode.checkSemantic();
        }
        //<MulExp> ('*' | '/' | '%') <UnaryExp>
        else {
            mulExpNode.checkSemantic();
            unaryExpNode.checkSemantic();
        }
    }

    @Override
    public String toString() {
        return Objects.toString(mulExpNode, "") +
                Objects.toString(mulToken, "") + unaryExpNode + nodeType;
    }
}

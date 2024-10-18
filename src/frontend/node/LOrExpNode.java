package frontend.node;

import frontend.token.Token;

import java.util.Objects;

//<LOrExp> ::= <LAndExp> | <LOrExp> '||' <LAndExp>
public class LOrExpNode extends Node {
    private final LOrExpNode lorExpNode;
    private final Token orToken;
    private final LAndExpNode landExpNode;

    public LOrExpNode(LOrExpNode lorExpNode, Token orToken, LAndExpNode landExpNode) {
        super(NodeType.L_OR_EXP);
        this.lorExpNode = lorExpNode;
        this.orToken = orToken;
        this.landExpNode = landExpNode;
    }

    public void checkSemantic() {
        //<LAndExp>
        if (lorExpNode == null) {
            landExpNode.checkSemantic();
        }
        //<LOrExp> '||' <LAndExp>
        else {
            lorExpNode.checkSemantic();
            landExpNode.checkSemantic();
        }
    }

    @Override
    public String toString() {
        return Objects.toString(lorExpNode, "") +
                Objects.toString(orToken, "") + landExpNode + nodeType;
    }
}

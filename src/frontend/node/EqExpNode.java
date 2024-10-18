package frontend.node;

import frontend.token.Token;

import java.util.Objects;

//<EqExp> ::= <RelExp> | <EqExp> ('==' | '!=') <RelExp>
public class EqExpNode extends Node {
    private final EqExpNode eqExpNode;
    private final Token eqToken;
    private final RelExpNode relExpNode;

    public EqExpNode(EqExpNode eqExpNode, Token eqToken, RelExpNode relExpNode) {
        super(NodeType.EQ_EXP);
        this.eqExpNode = eqExpNode;
        this.eqToken = eqToken;
        this.relExpNode = relExpNode;
    }

    @Override
    public void checkSemantic() {
        //<RelExp>
        if (eqExpNode==null){
            relExpNode.checkSemantic();
        }
        //<EqExp> ('==' | '!=') <RelExp>
        else {
            eqExpNode.checkSemantic();
            relExpNode.checkSemantic();
        }
    }

    @Override
    public String toString() {
        return Objects.toString(eqExpNode, "") +
                Objects.toString(eqToken, "") + relExpNode + nodeType;
    }
}

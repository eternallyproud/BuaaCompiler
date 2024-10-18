package frontend.node;

import frontend.token.Token;

import java.util.Objects;

//<LAndExp> ::= <EqExp> | <LAndExp> '&&' <EqExp>
public class LAndExpNode extends Node {
    public final LAndExpNode landExpNode;
    public final Token andToken;
    public final EqExpNode eqExpNode;

    public LAndExpNode(LAndExpNode landExpNode, Token andToken, EqExpNode eqExpNode) {
        super(NodeType.L_AND_EXP);
        this.landExpNode = landExpNode;
        this.andToken = andToken;
        this.eqExpNode = eqExpNode;
    }

    @Override
    public void checkSemantic(){
        //<EqExp>
        if (landExpNode == null) {
            eqExpNode.checkSemantic();
        }
        //<LAndExp> '&&' <EqExp>
        else {
            landExpNode.checkSemantic();
            eqExpNode.checkSemantic();
        }
    }

    @Override
    public String toString() {
        return Objects.toString(landExpNode, "") +
                Objects.toString(andToken, "") + eqExpNode + nodeType;
    }
}

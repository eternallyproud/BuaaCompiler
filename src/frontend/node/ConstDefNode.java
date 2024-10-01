package frontend.node;

import frontend.token.Token;

import java.util.Objects;

//<ConstDef> ::= <Ident> [ '[' <ConstExp> ']' ] '=' <ConstInitVal>
public class ConstDefNode extends Node {
    private final Token identToken;
    private final Token lbrackToken;
    private final ConstExpNode constExpNode;
    private final Token rbrackToken;
    private final Token assignToken;
    private final ConstInitValNode constInitValNode;

    public ConstDefNode(Token identToken, Token lbrackToken, ConstExpNode constExpNode, Token rbrackToken, Token assignToken, ConstInitValNode constInitValNode) {
        super(NodeType.CONST_DEF);
        this.identToken = identToken;
        this.lbrackToken = lbrackToken;
        this.constExpNode = constExpNode;
        this.rbrackToken = rbrackToken;
        this.assignToken = assignToken;
        this.constInitValNode = constInitValNode;
    }

    @Override
    public String toString() {
        return identToken +
                Objects.toString(lbrackToken, "") +
                Objects.toString(constExpNode, "") +
                Objects.toString(rbrackToken, "") +
                assignToken + constInitValNode + nodeType;
    }
}

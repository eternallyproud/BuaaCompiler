package frontend.node;

import frontend.token.Token;

import java.util.Objects;

//<FuncFParam> ::= <BType> <Ident> [ '[' ']' ]
public class FuncFParamNode extends Node {
    private final BTypeNode bTypeNode;
    private final Token identToken;
    private final Token lbrackToken;
    private final Token rbrackToken;

    public FuncFParamNode(BTypeNode bTypeNode, Token identToken, Token lbrackToken, Token rbrackToken) {
        super(NodeType.FUNC_F_PARAM);
        this.bTypeNode = bTypeNode;
        this.identToken = identToken;
        this.lbrackToken = lbrackToken;
        this.rbrackToken = rbrackToken;
    }

    @Override
    public String toString() {
        return "" + bTypeNode + identToken +
                Objects.toString(lbrackToken, "") +
                Objects.toString(rbrackToken, "") +
                nodeType;
    }
}

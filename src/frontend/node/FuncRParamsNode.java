package frontend.node;

import frontend.token.Token;
import utils.Tools;

import java.util.ArrayList;

//<FuncRParams> ::= <Exp> { ',' <Exp> }
public class FuncRParamsNode extends Node {
    private final ArrayList<ExpNode> expNodes;
    private final ArrayList<Token> commaTokens;

    public FuncRParamsNode(ArrayList<ExpNode> expNodes, ArrayList<Token> commaTokens) {
        super(NodeType.FUNC_R_PARAMS);
        this.expNodes = expNodes;
        this.commaTokens = commaTokens;
    }

    @Override
    public String toString() {
        return Tools.twoArrayListToString(expNodes, commaTokens) + nodeType;
    }
}

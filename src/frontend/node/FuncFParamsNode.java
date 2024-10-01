package frontend.node;

import frontend.token.Token;
import utils.Tools;

import java.util.ArrayList;

//<FuncFParams> ::= <FuncFParam> { ',' <FuncFParam> }
public class FuncFParamsNode extends Node {
    private final ArrayList<FuncFParamNode> funcFParamNodes;
    private final ArrayList<Token> commaTokens;

    public FuncFParamsNode(ArrayList<FuncFParamNode> funcFParamNodes, ArrayList<Token> commaTokens) {
        super(NodeType.FUNC_F_PARAMS);
        this.funcFParamNodes = funcFParamNodes;
        this.commaTokens = commaTokens;
    }

    @Override
    public String toString() {
        return Tools.twoArrayListToString(funcFParamNodes, commaTokens) + nodeType;
    }
}

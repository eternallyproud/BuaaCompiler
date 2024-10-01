package frontend.node;

import utils.Tools;

import java.util.ArrayList;

//<CompUnit> ::= {<Decl>} {<FuncDef>} <MainFuncDef>
public class CompUnitNode extends Node {
    private final ArrayList<DeclNode> declNodes;
    private final ArrayList<FuncDefNode> funcDefNodes;
    private final MainFuncDefNode mainFuncDefNode;

    public CompUnitNode(ArrayList<DeclNode> declNodes, ArrayList<FuncDefNode> funcDefNodes, MainFuncDefNode mainFuncDefNode) {
        super(NodeType.COMP_UNIT);
        this.declNodes = declNodes;
        this.funcDefNodes = funcDefNodes;
        this.mainFuncDefNode = mainFuncDefNode;
    }

    @Override
    public String toString() {
        return Tools.arrayListToString(declNodes) + Tools.arrayListToString(funcDefNodes)
                + mainFuncDefNode + nodeType;
    }
}

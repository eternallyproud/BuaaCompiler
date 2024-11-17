package frontend.node;

import frontend.ir.ValueTable;
import frontend.ir.value.Value;
import frontend.symbol.SymbolTable;
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
    public void checkSemantic() {
        SymbolTable.SYMBOL_TABLE.addScope();
        for (DeclNode declNode : declNodes) {
            declNode.checkSemantic();
        }
        for (FuncDefNode funcDefNode : funcDefNodes) {
            funcDefNode.checkSemantic();
        }
        mainFuncDefNode.checkSemantic();
        SymbolTable.SYMBOL_TABLE.removeScope();
    }

    @Override
    public Value buildIR() {
        ValueTable.VALUE_TABLE.push();
        for (DeclNode declNode : declNodes) {
            declNode.buildIR();
        }
        for (FuncDefNode funcDefNode : funcDefNodes) {
            funcDefNode.buildIR();
        }
        mainFuncDefNode.buildIR();
        ValueTable.VALUE_TABLE.pop();

        return super.buildIR();
    }

    @Override
    public String toString() {
        return Tools.arrayListToString(declNodes) + Tools.arrayListToString(funcDefNodes)
                + mainFuncDefNode + nodeType;
    }
}

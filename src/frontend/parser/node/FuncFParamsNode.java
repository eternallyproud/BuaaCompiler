package frontend.parser.node;

import frontend.ir.llvm.value.Value;
import frontend.semantic.symbol.NumericalSymbol;
import frontend.lexer.token.Token;
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

    public ArrayList<NumericalSymbol> getParameterSymbols() {
        ArrayList<NumericalSymbol> parameterSymbols = new ArrayList<>();
        for (FuncFParamNode funcFParamNode : funcFParamNodes) {
            parameterSymbols.add(funcFParamNode.getSymbol());
        }
        return parameterSymbols;
    }

    public ArrayList<Token> getParameterTokens() {
        ArrayList<Token> parameterTokens = new ArrayList<>();
        for (FuncFParamNode funcFParamNode : funcFParamNodes) {
            parameterTokens.add(funcFParamNode.getIdentToken());
        }
        return parameterTokens;
    }

    @Override
    public Value buildIR() {
        for(FuncFParamNode funcFParamNode : funcFParamNodes) {
            funcFParamNode.buildIR();
        }

        return null;
    }

    @Override
    public String toString() {
        return Tools.twoArrayListToString(funcFParamNodes, commaTokens) + nodeType;
    }
}

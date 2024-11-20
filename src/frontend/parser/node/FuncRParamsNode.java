package frontend.parser.node;

import frontend.ir.llvm.value.Value;
import frontend.semantic.symbol.DataType;
import frontend.lexer.token.Token;
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

    public ArrayList<DataType> getParameterDataTypes() {
        ArrayList<DataType> parameterDataTypes = new ArrayList<>();
        for (ExpNode expNode : expNodes) {
            parameterDataTypes.add(expNode.getDataType().getNonConstantDataType());
        }
        return parameterDataTypes;
    }

    public ArrayList<Value> buildValue(){
        ArrayList<Value> values = new ArrayList<>();
        for (ExpNode expNode : expNodes) {
            values.add(expNode.buildIR());
        }
        return values;
    }

    @Override
    public void checkSemantic() {
        for (ExpNode expNode : expNodes) {
            expNode.checkSemantic();
        }
    }

    @Override
    public String toString() {
        return Tools.twoArrayListToString(expNodes, commaTokens) + nodeType;
    }
}

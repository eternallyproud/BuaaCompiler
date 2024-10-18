package frontend.node;

import frontend.symbol.DataType;
import frontend.symbol.SymbolTable;
import frontend.token.Token;

import java.util.ArrayList;
import java.util.Objects;

//<UnaryExp> ::= <PrimaryExp> | <Ident> '(' [ <FuncRParams> ] ')' | <UnaryOp> <UnaryExp>
public class UnaryExpNode extends Node {
    private final PrimaryExpNode primaryExpNode;
    private final Token identToken;
    private final Token lparenToken;
    private final FuncRParamsNode funcRParamsNode;
    private final Token rparenToken;
    private final UnaryOpNode unaryOpNode;
    private final UnaryExpNode unaryExpNode;

    public UnaryExpNode(PrimaryExpNode primaryExpNode, Token identToken, Token lparenToken, FuncRParamsNode funcRParamsNode, Token rparenToken, UnaryOpNode unaryOpNode, UnaryExpNode unaryExpNode) {
        super(NodeType.UNARY_EXP);
        this.primaryExpNode = primaryExpNode;
        this.identToken = identToken;
        this.lparenToken = lparenToken;
        this.funcRParamsNode = funcRParamsNode;
        this.rparenToken = rparenToken;
        this.unaryOpNode = unaryOpNode;
        this.unaryExpNode = unaryExpNode;
    }

    public DataType getDataType() {
        //<PrimaryExp>
        if (primaryExpNode != null) {
            return primaryExpNode.getDataType();
        }
        //<Ident> '(' [ <FuncRParams> ] ')'
        else if (identToken != null) {
            return SymbolTable.SYMBOL_TABLE.getFunctionReturnDataType(identToken).getNonConstantDataType().getCharToInt();
        }
        //<UnaryOp> <UnaryExp>
        else {
            return unaryExpNode.getDataType();
        }
    }

    @Override
    public void checkSemantic() {
        //<PrimaryExp>
        if (primaryExpNode != null) {
            primaryExpNode.checkSemantic();
        }
        //<Ident> '(' [ <FuncRParams> ] ')'
        else if (identToken != null) {
            ArrayList<DataType> parameterDataTypes;
            if (funcRParamsNode != null) {
                funcRParamsNode.checkSemantic();
                parameterDataTypes = funcRParamsNode.getParameterDataTypes();
            } else {
                parameterDataTypes = new ArrayList<>();
            }
            SymbolTable.SYMBOL_TABLE.tackle(identToken, parameterDataTypes);
        }
        //<UnaryOp> <UnaryExp>
        else {
            unaryOpNode.checkSemantic();
            unaryExpNode.checkSemantic();
        }
    }

    @Override
    public String toString() {
        return Objects.toString(primaryExpNode, "") +
                Objects.toString(identToken, "") +
                Objects.toString(lparenToken, "") +
                Objects.toString(funcRParamsNode, "") +
                Objects.toString(rparenToken, "") +
                Objects.toString(unaryOpNode, "") +
                Objects.toString(unaryExpNode, "") + nodeType;
    }
}

package frontend.parser.node;

import frontend.ir.llvm.value.Constant;
import frontend.ir.llvm.value.Value;
import frontend.semantic.symbol.DataType;
import frontend.lexer.token.Token;
import utils.Tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

//<InitVal> ::= <Exp> | '{' [ <Exp> { ',' <Exp> } ] '}' | <StringConst>
public class InitValNode extends Node {
    private final ExpNode expNode;
    private final Token lbraceToken;
    private final ArrayList<ExpNode> expNodes;
    private final ArrayList<Token> commaTokens;
    private final Token rbraceToken;
    private final Token strconToken;

    public InitValNode(ExpNode expNode, Token lbraceToken, ArrayList<ExpNode> expNodes, ArrayList<Token> commaTokens, Token rbraceToken, Token strconToken) {
        super(NodeType.INIT_VAL);
        this.expNode = expNode;
        this.lbraceToken = lbraceToken;
        this.expNodes = expNodes;
        this.commaTokens = commaTokens;
        this.rbraceToken = rbraceToken;
        this.strconToken = strconToken;
    }

    public ArrayList<Integer> calculateValue(DataType expectedType) {
        ArrayList<Integer> values;
        //<Exp>
        if (expNode != null) {
            values = new ArrayList<>(Collections.singletonList(expNode.tryCalculateValue()));
        }
        //'{' [ <Exp> { ',' <Exp> } ] '}'
        else if (lbraceToken != null) {
            values = expNodes.stream().map(ExpNode::tryCalculateValue).collect(Collectors.toCollection(ArrayList::new));
        }
        //<StringConst>
        else {
            String strcon = strconToken.getContent();
            values = Tools.stringToAscii(strcon.substring(1, strcon.length() - 1));
        }

        //convert
        if (expectedType.isChar()) {
            values.replaceAll(integer -> integer % 256);
        }

        return values;
    }

    public ArrayList<Value> buildValue(DataType expectedType) {
        ArrayList<Value> values;
        //<Exp>
        if (expNode != null) {
            values = new ArrayList<>(Collections.singletonList(expNode.buildIR()));
        }
        //'{' [ <Exp> { ',' <Exp> } ] '}'
        else if (lbraceToken != null) {
            values = expNodes.stream().map(ExpNode::buildIR).collect(Collectors.toCollection(ArrayList::new));
        }
        //<StringConst>
        else {
            String strcon = strconToken.getContent();
            ArrayList<Integer> constValues = Tools.stringToAscii(strcon.substring(1, strcon.length() - 1));
            values = constValues.stream().map(Constant.Char::new).collect(Collectors.toCollection(ArrayList::new));
        }

        //convert
        values.replaceAll(value -> value.convertTo(expectedType.getValueType(1)));

        return values;
    }

    @Override
    public void checkSemantic() {
        //<Exp>
        if (expNode != null) {
            expNode.checkSemantic();
        }
        //'{' [ <Exp> { ',' <Exp> } ] '}'
        else if (lbraceToken != null) {
            for (ExpNode expNode : expNodes)
                expNode.checkSemantic();
        }
        //<StringConst>
    }

    @Override
    public String toString() {
        return Objects.toString(expNode, "") +
                Objects.toString(lbraceToken, "") +
                Tools.twoArrayListToString(expNodes, commaTokens) +
                Objects.toString(rbraceToken, "") +
                Objects.toString(strconToken, "") +
                nodeType;
    }
}

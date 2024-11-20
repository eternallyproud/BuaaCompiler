package frontend.parser.node;

import frontend.semantic.symbol.DataType;
import frontend.lexer.token.Token;
import utils.Tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

//<ConstInitVal> ::= <ConstExp> | '{' [ <ConstExp> { ',' <ConstExp> } ] '}' | <StringConst>
public class ConstInitValNode extends Node {
    private final ConstExpNode constExpNode;
    private final Token lbraceToken;
    private final ArrayList<ConstExpNode> constExpNodes;
    private final ArrayList<Token> commaTokens;
    private final Token rbraceToken;
    private final Token strconToken;

    public ConstInitValNode(ConstExpNode constExpNode, Token lbraceToken, ArrayList<ConstExpNode> constExpNodes, ArrayList<Token> commaTokens, Token rbraceToken, Token strconToken) {
        super(NodeType.CONST_INIT_VAL);
        this.constExpNode = constExpNode;
        this.lbraceToken = lbraceToken;
        this.constExpNodes = constExpNodes;
        this.commaTokens = commaTokens;
        this.rbraceToken = rbraceToken;
        this.strconToken = strconToken;
    }

    public ArrayList<Integer> calculateValue(DataType expectedType) {
        ArrayList<Integer> values;
        //<ConstExp>
        if (constExpNode != null) {
            values = new ArrayList<>(Collections.singletonList(constExpNode.calculateValue()));
        }
        //'{' [ <ConstExp> { ',' <ConstExp> } ] '}'
        else if (lbraceToken != null) {
            values = constExpNodes.stream().map(ConstExpNode::calculateValue).collect(Collectors.toCollection(ArrayList::new));
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

    @Override
    public void checkSemantic() {
        //<ConstExp>
        if (constExpNode != null) {
            constExpNode.checkSemantic();
        }
        //'{' [ <ConstExp> { ',' <ConstExp> } ] '}'
        else if (lbraceToken != null) {
            for (ConstExpNode constExpNode : constExpNodes) {
                constExpNode.checkSemantic();
            }
        }
        //<StringConst>
    }

    @Override
    public String toString() {
        return Objects.toString(constExpNode, "") +
                Objects.toString(lbraceToken, "") +
                Tools.twoArrayListToString(constExpNodes, commaTokens) +
                Objects.toString(rbraceToken, "") +
                Objects.toString(strconToken, "") +
                nodeType;
    }
}

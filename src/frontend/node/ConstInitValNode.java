package frontend.node;

import frontend.token.Token;
import utils.Tools;

import java.util.ArrayList;
import java.util.Objects;

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

    @Override
    public void checkSemantic(){
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

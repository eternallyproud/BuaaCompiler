package frontend.node;

import frontend.token.Token;
import utils.Tools;

import java.util.ArrayList;
import java.util.Objects;

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

    @Override
    public void checkSemantic(){
        //<Exp>
        if(expNode != null){
            expNode.checkSemantic();
        }
        //'{' [ <Exp> { ',' <Exp> } ] '}'
        else if (lbraceToken != null){
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

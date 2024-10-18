package frontend.node;

import frontend.symbol.DataType;
import frontend.token.Token;

import java.util.Objects;

//<PrimaryExp> ::= '(' <Exp> ')' | <LVal> | <Number> | <Character>
public class PrimaryExpNode extends Node {
    private final Token lparenToken;
    private final ExpNode expNode;
    private final Token rparenToken;
    private final LValNode lvalNode;
    private final NumberNode numberNode;
    private final CharacterNode characterNode;

    public PrimaryExpNode(Token lparenToken, ExpNode expNode, Token rparenToken, LValNode lvalNode, NumberNode numberNode, CharacterNode characterNode) {
        super(NodeType.PRIMARY_EXP);
        this.lparenToken = lparenToken;
        this.expNode = expNode;
        this.rparenToken = rparenToken;
        this.lvalNode = lvalNode;
        this.numberNode = numberNode;
        this.characterNode = characterNode;
    }

    public DataType getDataType() {
        //'(' <Exp> ')'
        if (lparenToken != null) {
            return expNode.getDataType();
        }
        //<LVal>
        else if (lvalNode != null) {
            return lvalNode.getDataType();
        }
        //<Number>
        else if (numberNode != null) {
            return numberNode.getDataType();
        }
        //<Character>
        else {
            return characterNode.getDataType();
        }
    }

    @Override
    public void checkSemantic() {
        //'(' <Exp> ')'
        if (lparenToken != null) {
            expNode.checkSemantic();
        }
        //<LVal>
        else if (lvalNode != null) {
            lvalNode.checkSemantic();
        }
        //<Number>
        else if (numberNode != null) {
            numberNode.checkSemantic();
        }
        //<Character>
        else {
            characterNode.checkSemantic();
        }
    }

    @Override
    public String toString() {
        return Objects.toString(lparenToken, "") +
                Objects.toString(expNode, "") +
                Objects.toString(rparenToken, "") +
                Objects.toString(lvalNode, "") +
                Objects.toString(numberNode, "") +
                Objects.toString(characterNode, "") + nodeType;
    }
}

package frontend.parser.node;

import frontend.ir.llvm.value.Value;
import frontend.semantic.symbol.DataType;
import frontend.lexer.token.Token;

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

    public int calculateValue() {
        //'(' <Exp> ')'
        if (lparenToken != null) {
            return expNode.calculateValue();
        }
        //<LVal>
        else if (lvalNode != null) {
            return lvalNode.calculateValue();
        }
        //<Number>
        else if (numberNode != null) {
            return numberNode.calculateValue();
        }
        //<Character>
        else {
            return characterNode.calculateValue();
        }
    }

    @Override
    public Value buildIR() {
        //'(' <Exp> ')'
        if (lparenToken != null) {
            return expNode.buildIR();
        }
        //<LVal>
        else if (lvalNode != null) {
            return lvalNode.buildIR();
        }
        //<Number>
        else if (numberNode != null) {
            return numberNode.buildIR();
        }
        //<Character>
        else {
            return characterNode.buildIR();
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

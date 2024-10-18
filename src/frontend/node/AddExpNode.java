package frontend.node;

import frontend.symbol.DataType;
import frontend.token.Token;

import java.util.Objects;

//<AddExp> ::=  <MulExp> | <AddExp> ('+' | '−') <MulExp>
public class AddExpNode extends Node {
    public final AddExpNode addExpNode;
    public final Token addToken;
    public final MulExpNode mulExpNode;

    public AddExpNode(AddExpNode addExpNode, Token addToken, MulExpNode mulExpNode) {
        super(NodeType.ADD_EXP);
        this.addExpNode = addExpNode;
        this.addToken = addToken;
        this.mulExpNode = mulExpNode;
    }

    public DataType getDataType(){
        return mulExpNode.getDataType();
    }

    @Override
    public void checkSemantic() {
        if(addExpNode == null){
            mulExpNode.checkSemantic();
        }else{
            addExpNode.checkSemantic();
            mulExpNode.checkSemantic();
        }
    }

    @Override
    public String toString() {
        return Objects.toString(addExpNode, "") +
                Objects.toString(addToken, "") + mulExpNode + nodeType;
    }
}

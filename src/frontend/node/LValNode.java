package frontend.node;

import frontend.symbol.DataType;
import frontend.symbol.SymbolTable;
import frontend.token.Token;

import java.util.Objects;

//<LVal> ::= <Ident> [ '[' <Exp> ']' ]
public class LValNode extends Node {
    private final Token identToken;
    private final Token lbrackToken;
    private final ExpNode expNode;
    private final Token rbrackToken;

    public LValNode(Token identToken, Token lbrackToken, ExpNode expNode, Token rbrackToken) {
        super(NodeType.L_VAL);
        this.identToken = identToken;
        this.lbrackToken = lbrackToken;
        this.expNode = expNode;
        this.rbrackToken = rbrackToken;
    }

    public DataType getDataType() {
        return SymbolTable.SYMBOL_TABLE.getNumericalDataType(identToken, lbrackToken != null).getCharToInt();
    }

    public void tryAssignTo() {
        SymbolTable.SYMBOL_TABLE.tackle(identToken, lbrackToken != null);
    }

    @Override
    public void checkSemantic() {
        SymbolTable.SYMBOL_TABLE.tackle(identToken);
        if (expNode != null) {
            expNode.checkSemantic();
        }
    }

    @Override
    public String toString() {
        return identToken +
                Objects.toString(lbrackToken, "") +
                Objects.toString(expNode, "") +
                Objects.toString(rbrackToken, "") + nodeType;
    }
}

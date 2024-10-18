package frontend.node;

import frontend.symbol.DataType;
import frontend.symbol.SymbolTable;
import frontend.token.Token;

import java.util.Objects;

//<ConstDef> ::= <Ident> [ '[' <ConstExp> ']' ] '=' <ConstInitVal>
public class ConstDefNode extends Node {
    private final Token identToken;
    private final Token lbrackToken;
    private final ConstExpNode constExpNode;
    private final Token rbrackToken;
    private final Token assignToken;
    private final ConstInitValNode constInitValNode;
    private DataType bType;

    public ConstDefNode(Token identToken, Token lbrackToken, ConstExpNode constExpNode, Token rbrackToken, Token assignToken, ConstInitValNode constInitValNode) {
        super(NodeType.CONST_DEF);
        this.identToken = identToken;
        this.lbrackToken = lbrackToken;
        this.constExpNode = constExpNode;
        this.rbrackToken = rbrackToken;
        this.assignToken = assignToken;
        this.constInitValNode = constInitValNode;
    }

    public void setBType(DataType bType) {
        this.bType = lbrackToken == null ? bType : bType.getRaisedDataType();
    }

    @Override
    public void checkSemantic() {
        if (constExpNode != null) {
            constExpNode.checkSemantic();
        }
        constInitValNode.checkSemantic();
        SymbolTable.SYMBOL_TABLE.tackle(identToken, bType);
    }

    @Override
    public String toString() {
        return identToken +
                Objects.toString(lbrackToken, "") +
                Objects.toString(constExpNode, "") +
                Objects.toString(rbrackToken, "") +
                assignToken + constInitValNode + nodeType;
    }
}

package frontend.node;

import frontend.symbol.DataType;
import frontend.symbol.SymbolTable;
import frontend.token.Token;

import java.util.Objects;

//<VarDef> ::= <Ident> [ '[' <ConstExp> ']' ] [ '=' <InitVal> ]
public class VarDefNode extends Node {
    private final Token identToken;
    private final Token lbrackToken;
    private final ConstExpNode constExpNode;
    private final Token rbrackToken;
    private final Token assignToken;
    private final InitValNode initValNode;
    private DataType bType;

    public VarDefNode(Token identToken, Token lbrackToken, ConstExpNode constExpNode, Token rbrackToken, Token assignToken, InitValNode initValNode) {
        super(NodeType.VAR_DEF);
        this.identToken = identToken;
        this.lbrackToken = lbrackToken;
        this.constExpNode = constExpNode;
        this.rbrackToken = rbrackToken;
        this.assignToken = assignToken;
        this.initValNode = initValNode;
    }

    public void setBType(DataType bType) {
        this.bType = lbrackToken == null ? bType : bType.getRaisedDataType();
    }

    @Override
    public void checkSemantic() {
        if (constExpNode != null) {
            constExpNode.checkSemantic();
        }
        if (initValNode != null) {
            initValNode.checkSemantic();
        }
        SymbolTable.SYMBOL_TABLE.tackle(identToken, bType);
    }


    @Override
    public String toString() {
        return identToken +
                Objects.toString(lbrackToken, "") +
                Objects.toString(constExpNode, "") +
                Objects.toString(rbrackToken, "") +
                Objects.toString(assignToken, "") +
                Objects.toString(initValNode, "") +
                nodeType;
    }
}

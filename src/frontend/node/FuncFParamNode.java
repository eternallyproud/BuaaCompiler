package frontend.node;

import frontend.symbol.CharArraySymbol;
import frontend.symbol.CharSymbol;
import frontend.symbol.IntArraySymbol;
import frontend.symbol.IntSymbol;
import frontend.symbol.NumericalSymbol;
import frontend.token.Token;

import java.util.Objects;

//<FuncFParam> ::= <BType> <Ident> [ '[' ']' ]
public class FuncFParamNode extends Node {
    private final BTypeNode bTypeNode;
    private final Token identToken;
    private final Token lbrackToken;
    private final Token rbrackToken;

    public FuncFParamNode(BTypeNode bTypeNode, Token identToken, Token lbrackToken, Token rbrackToken) {
        super(NodeType.FUNC_F_PARAM);
        this.bTypeNode = bTypeNode;
        this.identToken = identToken;
        this.lbrackToken = lbrackToken;
        this.rbrackToken = rbrackToken;
    }

    public NumericalSymbol getSymbol() {
        return switch (lbrackToken == null ? bTypeNode.getBType() : bTypeNode.getBType().getRaisedDataType()) {
            case CHAR -> new CharSymbol(identToken.getContent());
            case INT -> new IntSymbol(identToken.getContent());
            case CHAR_ARRAY -> new CharArraySymbol(identToken.getContent());
            case INT_ARRAY -> new IntArraySymbol(identToken.getContent());
            default -> null;
        };
    }

    public Token getIdentToken(){
        return identToken;
    }

    @Override
    public String toString() {
        return "" + bTypeNode + identToken +
                Objects.toString(lbrackToken, "") +
                Objects.toString(rbrackToken, "") +
                nodeType;
    }
}

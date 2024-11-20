package frontend.parser.node;

import frontend.semantic.symbol.DataType;
import frontend.lexer.token.Token;

import java.util.Objects;

//<FuncType> ::= 'void' | 'int' | 'char'
public class FuncTypeNode extends Node {
    private final Token voidToken;
    private final Token intToken;
    private final Token charToken;

    public FuncTypeNode(Token voidToken, Token intToken, Token charToken) {
        super(NodeType.FUNC_TYPE);
        this.voidToken = voidToken;
        this.intToken = intToken;
        this.charToken = charToken;
    }

    public DataType getFuncType() {
        //'int'
        if (intToken != null) {
            return DataType.INT_FUNC;
        }
        //'char'
        if (charToken != null) {
            return DataType.CHAR_FUNC;
        }
        //'void'
        else {
            return DataType.VOID_FUNC;
        }
    }

    public DataType getReturnType() {
        //'int'
        if (intToken != null) {
            return DataType.INT;
        }
        //'char'
        if (charToken != null) {
            return DataType.CHAR;
        }
        //'void'
        else {
            return DataType.VOID;
        }
    }

    @Override
    public String toString() {
        return Objects.toString(voidToken, "") +
                Objects.toString(intToken, "") +
                Objects.toString(charToken, "") +
                nodeType;
    }
}

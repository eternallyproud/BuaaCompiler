package frontend.node;

import frontend.symbol.DataType;
import frontend.token.Token;

//<Character> ::= <CharConst>
public class CharacterNode extends Node {
    private final Token charConstToken;

    public CharacterNode(Token charConstToken) {
        super(NodeType.CHARACTER);
        this.charConstToken = charConstToken;
    }

    public DataType getDataType() {
        return DataType.CHAR.getCharToInt();
    }

    @Override
    public String toString() {
        return "" + charConstToken + nodeType;
    }
}

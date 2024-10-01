package frontend.node;

import frontend.token.Token;

//<Character> ::= <CharConst>
public class CharacterNode extends Node {
    private final Token charConstToken;

    public CharacterNode(Token charConstToken) {
        super(NodeType.CHARACTER);
        this.charConstToken = charConstToken;
    }

    @Override
    public String toString() {
        return "" + charConstToken + nodeType;
    }
}

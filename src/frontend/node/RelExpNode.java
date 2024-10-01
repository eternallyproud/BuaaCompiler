package frontend.node;

import frontend.token.Token;

import java.util.Objects;

//<RelExp> ::= <AddExp> | <RelExp> ('<' | '>' | '<=' | '>=') <AddExp>
public class RelExpNode extends Node {
    private final RelExpNode relExpNode;
    private final Token relToken;
    private final AddExpNode addExpNode;

    public RelExpNode(RelExpNode relExpNode, Token relToken, AddExpNode addExpNode) {
        super(NodeType.REL_EXP);
        this.relExpNode = relExpNode;
        this.relToken = relToken;
        this.addExpNode = addExpNode;
    }

    @Override
    public String toString() {
        return Objects.toString(relExpNode, "") +
                Objects.toString(relToken, "") + addExpNode + nodeType;
    }
}

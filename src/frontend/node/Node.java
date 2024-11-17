package frontend.node;

import frontend.ir.value.Value;

public abstract class Node {
    protected final NodeType nodeType;

    public Node(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void checkSemantic() {
    }

    public Value buildIR() {
        return null;
    }
}

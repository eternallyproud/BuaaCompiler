package frontend.node;

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
}

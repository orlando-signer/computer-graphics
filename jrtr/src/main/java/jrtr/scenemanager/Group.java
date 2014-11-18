package jrtr.scenemanager;

import java.util.LinkedList;
import java.util.List;

public abstract class Group implements Node {

    private List<Node> children = new LinkedList<>();

    public void addChild(Node child) {
        children.add(child);
        // Sort the children, first the groups, then the leafs.
        children.sort((e1, e2) -> e1.getType().ordinal() - e2.getType().ordinal());
    }

    public void removeChild(Node child) {
        children.remove(child);
    }

    @Override
    public List<Node> getChildren() {
        return children;
    }

    @Override
    public final NodeType getType() {
        return NodeType.GROUP;
    }
}

package jrtr.scenemanager;

import java.util.LinkedList;
import java.util.List;

public abstract class Group implements Node {

    private List<Node> children = new LinkedList<>();

    public void addChild(Node child) {
        children.add(child);
    }

    public void removeChild(Node child) {
        children.remove(child);
    }

    @Override
    public List<Node> getChildren() {
        return children;
    }
}

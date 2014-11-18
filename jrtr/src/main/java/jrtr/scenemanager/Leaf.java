package jrtr.scenemanager;

import java.util.Collections;
import java.util.List;

public abstract class Leaf implements Node {
    @Override
    public final List<Node> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public final NodeType getType() {
        return NodeType.LEAF;
    }
}

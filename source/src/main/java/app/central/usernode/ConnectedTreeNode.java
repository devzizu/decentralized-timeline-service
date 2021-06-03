package app.central.usernode;

import java.util.LinkedList;
import java.util.List;

public class ConnectedTreeNode<T> {

    T data;
    ConnectedTreeNode<T> parent;
    List<ConnectedTreeNode<T>> children;

    public ConnectedTreeNode(T data) {
        this.data = data;
        this.children = new LinkedList<ConnectedTreeNode<T>>();
    }

    public ConnectedTreeNode<T> addChild(T child) {
        ConnectedTreeNode<T> childNode = new ConnectedTreeNode<T>(child);
        childNode.parent = this;
        this.children.add(childNode);
        return childNode;
    }
}

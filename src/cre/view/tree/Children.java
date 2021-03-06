package cre.view.tree;

/**
 * Created by HanYizhao on 2017/6/22.
 * One child of a tree node {@link Node}.
 */
public class Children {
    private Node value;
    private String edge;

    public Children(Node value, String edge) {
        this.value = value;
        this.edge = edge;
    }

    public Node getValue() {
        return value;
    }

    public void setValue(Node value) {
        this.value = value;
    }

    public String getEdge() {
        return edge;
    }

    public void setEdge(String edge) {
        this.edge = edge;
    }
}
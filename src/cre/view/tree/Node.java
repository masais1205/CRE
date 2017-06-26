package cre.view.tree;

import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * Created by 16502 on 2017/6/22.
 */
public class Node {
    private String name;
    private String content;

    private Node parent;
    private List<Children> children;

    private Rectangle2D selfRect;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public List<Children> getChildren() {
        return children;
    }

    public void setChildren(List<Children> children) {
        this.children = children;
    }

    public Rectangle2D getSelfRect() {
        return selfRect;
    }

    public void setSelfRect(Rectangle2D selfRect) {
        this.selfRect = selfRect;
    }

    public class Children {
        private Node value;
        private String edge;

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
}

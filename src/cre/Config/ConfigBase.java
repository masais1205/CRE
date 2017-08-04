package cre.Config;

/**
 * Created by HanYizhao on 2017/4/6.
 * A field of the object.
 */
public abstract class ConfigBase {
    protected String name;
    protected String comments;
    protected String shownName;
    protected boolean visible;

    protected ConfigBase(String name, String comments, String shownName) {
        this(name, comments, shownName, true);
    }

    protected ConfigBase(String name, String comments, String shownName, boolean visible) {
        this.name = name;
        this.comments = comments;
        this.shownName = shownName;
        this.visible = visible;
    }

    public String getName() {
        return name;
    }

    public String getComments() {
        return comments;
    }

    public String getShownName() {
        return shownName;
    }

    public boolean isVisible() {
        return visible;
    }
}

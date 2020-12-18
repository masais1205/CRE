package cre;

import com.github.jankroken.commandline.annotations.LongSwitch;
import com.github.jankroken.commandline.annotations.Option;
import com.github.jankroken.commandline.annotations.ShortSwitch;
import com.github.jankroken.commandline.annotations.SingleArgument;

public class DEEPAttributes {

    private String w;
    private String y;
    private String gt;

    @Option
    @ShortSwitch("w")
    @SingleArgument
    public void setW(String w) {
        this.w = w;
    }

    @Option
    @ShortSwitch("y")
    @SingleArgument
    public void setY(String y) {
        this.y = y;
    }

    @Option
    @LongSwitch("gt")
    @SingleArgument
    public void setGt(String gt) {
        this.gt = gt;
    }

    public String getW() {
        return w;
    }

    public String getY() {
        return y;
    }

    public String getGt() {
        return gt;
    }
}

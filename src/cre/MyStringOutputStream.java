package cre;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

/**
 * Created by HanYizhao on 2017/7/23.
 * This class is a buffer of standard output stream and standard error output stream.
 */
public class MyStringOutputStream extends OutputStream {

    private class MyByteArrayOutputStream extends ByteArrayOutputStream {
        String getLastChar() {
            if (count > 0) {
                return new String(Arrays.copyOfRange(buf, count - 1, count));
            } else {
                return null;
            }
        }
    }

    private class Message {
        long time;
        String message;
    }

    private LinkedList<Message> messages = new LinkedList<>();
    private final static long bufferSize = 40 * 1024 * 1024;
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private long nowSize = 0;
    private MyByteArrayOutputStream nowString = new MyByteArrayOutputStream();

    @Override
    public synchronized void write(int b) throws IOException {
        nowString.write(b);
    }

    private int getStringLength(String s) {
        return 25 + s.getBytes().length;
    }

    private boolean moveTop() {
        if (messages.size() > 0) {
            Message m = messages.pollFirst();
            nowSize -= getStringLength(m.message);
            return true;
        }
        return false;
    }

    @Override
    public synchronized String toString() {
        StringBuilder sb = new StringBuilder();
        for (Message m : messages) {
            sb.append(sdf.format(new Date(m.time)));
            sb.append('\t');
            sb.append(m.message);
        }
        String otherString = nowString.toString();
        if (otherString.length() > 0) {
            sb.append(sdf.format(new Date(System.currentTimeMillis())));
            sb.append('\t');
            sb.append(otherString);
        }
        return sb.toString();
    }

    @Override
    public synchronized void flush() throws IOException {
        String lastChar = nowString.getLastChar();
        if (lastChar != null && (lastChar.endsWith("\n") || lastChar.endsWith("\r"))) {
            Message m = new Message();
            m.time = System.currentTimeMillis();
            m.message = nowString.toString();
            messages.offerLast(m);
            nowSize += getStringLength(m.message);
            while (nowSize > bufferSize) {
                if (!moveTop()) {
                    break;
                }
            }
            nowString = new MyByteArrayOutputStream();
        }
    }
}

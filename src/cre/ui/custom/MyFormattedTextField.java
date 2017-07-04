package cre.ui.custom;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.Format;
import java.util.Iterator;

/**
 * Created by 16502 on 2017/6/28.
 */
public class MyFormattedTextField extends JFormattedTextField {

    private Object tag;
    private WeakReferenceQueue<TextFieldChangeListener> listeners = new WeakReferenceQueue<>();
    private PropertyChangeListener doubleListener, intListener;

    /**
     * Using this method may cause error!
     * Please use {@linkplain #setValue(Object)} instead.
     *
     * @param t
     */
    @Deprecated
    @Override
    public void setText(String t) {
        super.setText(t);
    }

    public void setDoubleRange(final double min, final double max) {
        this.removePropertyChangeListener("value", doubleListener);
        this.removePropertyChangeListener("value", intListener);
        doubleListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                try {
                    double newV = Double.parseDouble(evt.getNewValue().toString());
                    if (newV < min) {
                        MyFormattedTextField.this.setValue(min);
                    } else if (newV > max) {
                        MyFormattedTextField.this.setValue(max);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        this.addPropertyChangeListener("value", doubleListener);
    }

    public void setIntRange(final int min, final int max) {
        this.removePropertyChangeListener("value", doubleListener);
        this.removePropertyChangeListener("value", intListener);
        intListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                try {
                    double newV = Integer.parseInt(evt.getNewValue().toString());
                    if (newV < min) {
                        MyFormattedTextField.this.setValue(min);
                    } else if (newV > max) {
                        MyFormattedTextField.this.setValue(max);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        this.addPropertyChangeListener("value", intListener);
    }

    public void addTextFieldChangeListener(TextFieldChangeListener listener) {
        if (listener != null) {
            listeners.remove(listener);
            listeners.add(listener);
        }
    }

    public void removeTextFiledChangeListener(TextFieldChangeListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public interface TextFieldChangeListener {
        void textFieldChange(String newValue);
    }

    private void initValueChangeListener() {
        this.addPropertyChangeListener("value", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String newValue = evt.getNewValue().toString();
                Iterator<? super TextFieldChangeListener> i = listeners.iterator();
                while (i.hasNext()) {
                    TextFieldChangeListener k = (TextFieldChangeListener) i.next();
                    k.textFieldChange(newValue);
                }
            }
        });
    }


    public MyFormattedTextField() {
        initValueChangeListener();
    }

    public MyFormattedTextField(Object value) {
        super(value);
        initValueChangeListener();
    }

    public MyFormattedTextField(Format format) {
        super(format);
        initValueChangeListener();
    }

    public MyFormattedTextField(AbstractFormatter formatter) {
        super(formatter);
        initValueChangeListener();
    }

    public MyFormattedTextField(AbstractFormatterFactory factory) {
        super(factory);
        initValueChangeListener();
    }

    public MyFormattedTextField(AbstractFormatterFactory factory, Object currentValue) {
        super(factory, currentValue);
        initValueChangeListener();
    }
}

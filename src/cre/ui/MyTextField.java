package cre.ui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

/**
 * Created by HanYizhao on 2017/4/7.
 */
public class MyTextField extends JTextField {

    private Object tag;

    public MyTextField() {

    }

    public void setDoubleRange(final double min, final double max) {
        final Document dt = this.getDocument();
        dt.addDocumentListener(new DocumentListener() {
            private void check() {
                final DocumentListener ii = this;
                boolean needChange = false;
                double changeValue = 0;
                String s = MyTextField.this.getText();
                if (s.length() > 0) {
                    try {
                        final double i = Double.parseDouble(s);
                        if (i < min) {
                            needChange = true;
                            changeValue = min;
                        } else if (i > max) {
                            needChange = true;
                            changeValue = max;
                        }
                    } catch (Exception ignored) {
                    }
                    if (needChange) {
                        final double finalChangeValue = changeValue;
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                dt.removeDocumentListener(ii);
                                MyTextField.this.setText(finalChangeValue + "");
                                dt.addDocumentListener(ii);
                            }
                        });
                    }
                }
            }

            public void insertUpdate(DocumentEvent e) {
                check();
            }

            public void removeUpdate(DocumentEvent e) {
                check();
            }

            public void changedUpdate(DocumentEvent e) {
                check();
            }
        });
    }

    public void setIntRange(final int min, final int max) {
        final Document dt = this.getDocument();
        dt.addDocumentListener(new DocumentListener() {
            private void check() {
                final DocumentListener ii = this;
                boolean needChange = false;
                int changeValue = 0;
                String s = MyTextField.this.getText();
                if (s.length() > 0) {
                    try {
                        final Integer i = Integer.parseInt(s);
                        if (i < min) {
                            needChange = true;
                            changeValue = min;
                        } else if (i > max) {
                            needChange = true;
                            changeValue = max;
                        }
                    } catch (Exception ignored) {
                    }
                    if (needChange) {
                        final int finalChangeValue = changeValue;
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                dt.removeDocumentListener(ii);
                                MyTextField.this.setText(finalChangeValue + "");
                                dt.addDocumentListener(ii);
                            }
                        });
                    }
                }
            }

            public void insertUpdate(DocumentEvent e) {
                check();
            }

            public void removeUpdate(DocumentEvent e) {
                check();
            }

            public void changedUpdate(DocumentEvent e) {
                check();
            }
        });
    }


    public MyTextField(String text) {
        super(text);
    }

    public MyTextField(int columns) {
        super(columns);
    }

    public MyTextField(String text, int columns) {
        super(text, columns);
    }

    public MyTextField(Document doc, String text, int columns) {
        super(doc, text, columns);
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }
}

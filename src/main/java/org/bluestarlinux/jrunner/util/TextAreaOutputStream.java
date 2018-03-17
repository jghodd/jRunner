package org.bluestarlinux.jrunner.util;

import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JTextArea;

/**
 *
 * @author jghodd
 */
public class TextAreaOutputStream extends OutputStream
{
    private final JTextArea textArea;
    private final StringBuilder sb = new StringBuilder();
    private final StringBuffer stringBuffer = new StringBuffer();
    
    private String lastEntry = new String();

    public TextAreaOutputStream(final JTextArea textArea)
    {
        this.textArea = textArea;
    }

    @Override
    public void flush()
    {
    }

    @Override
    public void close()
    {
    }

    @Override
    public void write(int b) throws IOException
    {
        /*
        */
        if (b == '\r')
            return;

        if (b == '\n')
        {
            sb.append((char)b);
            
            if (lastEntry.length() > 0)
            {
                if (lastEntry.equals(sb.toString()))
                {
                    sb.setLength(0);
                    return;
                }
            }
                
            lastEntry = sb.toString();
            
            textArea.append(sb.toString());
            sb.setLength(0);
            
            stringBuffer.append((char)b);

            return;
        }

        sb.append((char) b);
        stringBuffer.append((char)b);
    }
    
    @Override
    public String toString()
    {
        return stringBuffer.toString();
    }
}
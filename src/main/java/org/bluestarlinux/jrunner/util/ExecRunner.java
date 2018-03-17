package org.bluestarlinux.jrunner.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 *
 * @author jghodd
 */
public class ExecRunner
{    
    final private ArrayList<ProcessBuilder> procBuilders;
    
    private OutputStream outputStream = null;
    
    public ExecRunner(OutputStream outStream)
    {
        outputStream = outStream;
        procBuilders = new ArrayList<>();
    }
    
    protected OutputStream getOutputStream()
    {
        return outputStream;
    }
    
    public void addExecCommand(ArrayList<String> commandArgs)
    {
        addExecCommand(commandArgs.toArray(new String[commandArgs.size()]));
    }

    public void addExecCommand(String... commandString)
    {
        procBuilders.add(new ProcessBuilder(commandString));
    }
    
    public boolean execCommands()
    {
        boolean returnVal = false;
        
        if (!procBuilders.isEmpty())
        {
            try
            {
                Process proc = null;

                for (ProcessBuilder procBuilder : procBuilders)
                {
                    if (outputStream != null)
                    {
                        procBuilder.redirectErrorStream(true);
                        proc = procBuilder.start();
                        pipeProcOutputToOutputStream(proc.getInputStream());
                    }
                    else
                    {
                        proc = procBuilder.start();
                    }
                }

                if (proc != null)
                {
                    proc.waitFor();
                    returnVal = (proc.exitValue() == 0);
                }
                else
                    returnVal = false;
            }
            catch (IOException | InterruptedException ioe) {}
        }
        
        return returnVal;
    }
    
    private void pipeProcOutputToOutputStream(InputStream inStream)
            throws IOException
    {
        int inputChar = 0;
        do
        {
            try
            {
                inputChar = inStream.read();
                if (inputChar != -1)
                {
                    outputStream.write(inputChar);
                    outputStream.flush();
                }
            }
            catch (IOException ioex)
            {
                if (inputChar == -1)
                {
                    outputStream.flush();
                    return;
                }
            }
        }
        while (inputChar != -1);
        
        outputStream.flush();
    }
}

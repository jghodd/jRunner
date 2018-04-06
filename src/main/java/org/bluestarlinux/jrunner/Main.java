package org.bluestarlinux.jrunner;

import java.util.ArrayList;
import java.util.Arrays;

import org.bluestarlinux.jrunner.gui.MainWindow;
import org.bluestarlinux.jrunner.util.ExecRunner;
import org.bluestarlinux.jrunner.util.GlobalTypes;
import org.bluestarlinux.jrunner.util.TextAreaOutputStream;

/**
 *
 * @author jghodd
 */
public class Main
{
    private static int execReturnValue = 0;
        
    public static void main(String[] args)
    {
        ArrayList<String> commandList = new ArrayList(Arrays.asList(args));
        String windowTitle = null;
        boolean internetRequired = false;
        
        if (commandList.contains("-t"))
        {
            int titleIndex = commandList.indexOf("-t");
            windowTitle = commandList.get(titleIndex + 1);
            
            commandList.remove("-t");
            commandList.remove(windowTitle);
        }

        if (commandList.contains("-i"))
        {
            internetRequired = true;
            commandList.remove("-i");
        }

        MainWindow mainWindow = new MainWindow(windowTitle, internetRequired);

        ExecRunner execRunner = new ExecRunner(new TextAreaOutputStream(mainWindow.getTextArea()));
        execRunner.addExecCommand(commandList);
                
        do
        {
            mainWindow.getCloseButton().setVisible(true);
            mainWindow.getCloseButton().setEnabled(false);
            mainWindow.getRetryButton().setVisible(false);
            mainWindow.getRetryButton().setEnabled(false);

            boolean returnVal = execRunner.execCommands();

            // System.out.println("execRunner returns: " + returnVal);

            setExecReturnValue(returnVal ? 0 : 1);
            
            mainWindow.getCloseButton().setEnabled(true);

            mainWindow.getRetryButton().setVisible(!returnVal);
            mainWindow.getRetryButton().setEnabled(!returnVal);

            try
            {
                synchronized(mainWindow)
                {
                    mainWindow.wait();
                }
            }
            catch (InterruptedException ie) {}
        }
        while (mainWindow.getButtonPressed() != GlobalTypes.ButtonPressed.CloseButton);

        //System.exit(getExecReturnValue());                
        System.exit(0);                
    }
    
    public static int getExecReturnValue()
    {
        return execReturnValue;
    }
    
    public static void setExecReturnValue(int rval)
    {
        execReturnValue = rval;
    }    
}

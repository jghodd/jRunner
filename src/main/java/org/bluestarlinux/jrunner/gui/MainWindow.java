package org.bluestarlinux.jrunner.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.text.DefaultCaret;

import org.bluestarlinux.jrunner.util.GlobalTypes;

/**
 *
 * @author jghodd
 */
public class MainWindow extends JFrame // implements ActionListener
{
    final private MainWindow mainFrame;
    
    private JTextArea textArea;
    private JButton closeButton;
    private JButton retryButton;
    private JScrollPane scrollPane;
    
    private GlobalTypes.ButtonPressed buttonPressed;
    
    public MainWindow(String windowTitle, boolean internetRequired)
    {
        super("Stdout");
        
        mainFrame = this;
        
        if (windowTitle != null)
            mainFrame.setTitle(windowTitle);
        else
            mainFrame.setTitle("Process Output");
        
        initialize(internetRequired);
    }
    
    private void initialize(boolean internetRequired)
    {
        setMinimumSize(new Dimension(400, 375));
        setLocation(50, 50);
        
        URL imgURL = getClass().getResource("/resources/bslx-icon.png");
        if(imgURL != null)
        {
            ImageIcon appIcon = new ImageIcon(imgURL);
            setIconImage(appIcon.getImage());
        }
        
        closeButton = new JButton("Close");
        closeButton.setEnabled(false);
        closeButton.addActionListener((ActionEvent e) -> { close(); });
        
        retryButton = new JButton("Retry");
        retryButton.setEnabled(false);
        retryButton.setVisible(false);
        retryButton.addActionListener((ActionEvent e) -> { retry(); });

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        
        DefaultCaret caret = (DefaultCaret)textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        
        scrollPane = new JScrollPane(getTextArea());
        
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        
        Terminator terminator = new Terminator();
	addWindowListener(terminator);
	addWindowStateListener(terminator);
        
        if (internetRequired)
        {
            while (!checkInternetConnection())
            {
                int option = JOptionPane.showConfirmDialog(
                        null,
                        "OK to try again. Cancel to exit.",
                        "Networking Issue",
                        JOptionPane.OK_CANCEL_OPTION);

                if (option == JOptionPane.CANCEL_OPTION)
                {
                    System.exit(1);
                }
            }
        }
        
        setMainPanelLayout();
        
        setVisible(true);
    }
    
    public void setMainPanelLayout()
    {
        JPanel listPane = new JPanel();
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
        
        listPane.add(Box.createRigidArea(new Dimension(0, 0)));
        listPane.add(getScrollPane());
        
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(getRetryButton());
        buttonPane.add(Box.createRigidArea(new Dimension(5, 0)));
        buttonPane.add(getCloseButton());

        Container contentPane = getContentPane();
        contentPane.add(listPane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.PAGE_END);
    }

    public JTextArea getTextArea()
    {
        return textArea;
    }
    
    public JScrollPane getScrollPane()
    {
        return scrollPane;
    }
    
    public JButton getCloseButton()
    {
        return closeButton;
    }
    
    public JButton getRetryButton()
    {
        return retryButton;
    }
    
    private void setButtonPressed(GlobalTypes.ButtonPressed buttPressed)
    {
        buttonPressed = buttPressed;
    }
    
    public GlobalTypes.ButtonPressed getButtonPressed()
    {
        return buttonPressed;
    }
    
    private boolean checkInternetConnection()
    {
        boolean bIsConnected = false;
        try
        {
            Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
            while (ifaces.hasMoreElements())
            {
                NetworkInterface iface = ifaces.nextElement();
                if (iface.isLoopback())
                    continue;
                
                if (iface.isUp())
                    bIsConnected = true;
            }
        }
        catch (SocketException ex) { }
        
        if (!bIsConnected)
        {
            JOptionPane.showMessageDialog(this,
                    "Unable to establish network connection.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        
        return bIsConnected;
    }
    
    /** Close the application. */
    public void close()
    {
        setButtonPressed(GlobalTypes.ButtonPressed.CloseButton);
        synchronized (this)
        {
            this.notify();
        }
    }
    
    /** Close the application. */
    public void retry()
    {
        setButtonPressed(GlobalTypes.ButtonPressed.RetryButton);
        synchronized(this)
        {
            this.notify();
        }
    }
    
    /** Class managing frame closing. */
    private class Terminator extends WindowAdapter
    {	        
	/** {@inheritDoc} */
        @Override
        public void windowClosing(WindowEvent e)
        {
            System.exit(0);
        }
    }
}

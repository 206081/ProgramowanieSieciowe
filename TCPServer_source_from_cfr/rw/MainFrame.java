/*
 * Decompiled with CFR 0_132.
 */
package rw;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import rw.TCPServer;

public class MainFrame
extends JFrame {
    private static MainFrame fr;
    private static final long serialVersionUID = 1L;
    public TCPServer mojSerwerek;
    public static int clientsCount;
    JLabel label = new JLabel("Listening port");
    JLabel emptylabel = new JLabel("                                       ");
    JTextField textField = new JTextField("7");
    JLabel clientsLabel = new JLabel("Clients count: " + clientsCount);
    JButton startButton = new JButton("Start");
    JButton stopButton = new JButton("Stop");
    JTextArea output = new JTextArea();
    JTextField textField2 = new JTextField();

    MainFrame() {
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        Font font = new Font("Verdana", 1, 11);
        this.output.setFont(font);
        this.output.setForeground(Color.BLUE);
        layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(this.label).addComponent(this.textField).addComponent(this.emptylabel).addComponent(this.startButton).addComponent(this.stopButton)).addComponent(this.clientsLabel).addComponent(this.output)));
        layout.setVerticalGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.label).addComponent(this.textField).addComponent(this.emptylabel).addComponent(this.startButton).addComponent(this.stopButton)).addComponent(this.clientsLabel).addComponent(this.output));
        this.stopButton.setEnabled(false);
        this.setTitle("TCP Server");
        this.setSize(560, 400);
        this.setDefaultCloseOperation(3);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.startButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                int port = 7;
                try {
                    port = Integer.valueOf(MainFrame.this.textField.getText());
                }
                catch (Exception e1) {
                    System.out.println(e1.getStackTrace());
                }
                MainFrame.access$000().mojSerwerek = new TCPServer();
                MainFrame.access$000().mojSerwerek.Start(port);
                MainFrame.this.startButton.setEnabled(false);
                MainFrame.this.stopButton.setEnabled(true);
            }
        });
        this.stopButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.access$000().mojSerwerek.Stop();
                MainFrame.access$000().mojSerwerek = null;
                MainFrame.this.startButton.setEnabled(true);
                MainFrame.this.stopButton.setEnabled(false);
            }
        });
    }

    public static void add2output(String str) {
        try {
            MainFrame.fr.output.getDocument().insertString(0, str + "\n", null);
        }
        catch (BadLocationException e1) {
            e1.printStackTrace();
        }
    }

    public static void refreshClientsCount(int val) {
        MainFrame.fr.clientsLabel.setText("Clients count: " + (clientsCount += val));
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable(){

            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
                fr = new MainFrame();
                MainFrame.access$000().mojSerwerek = new TCPServer();
            }
        });
    }

    static /* synthetic */ MainFrame access$000() {
        return fr;
    }

    static {
        clientsCount = 0;
    }

}


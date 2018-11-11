// Created by Veniamin_arefev
// Date was 10.07.2018

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;

public class Window extends JFrame {
    private Main parent;
    private Properties prop;
    private JTextField ip = new JTextField("ip");
    private JTextField port = new JTextField("port");
    private JTextField timer = new JTextField("timer");
    private JTextArea closeCommand = new JTextArea("closeCommand");
    private JTextArea openCommand = new JTextArea("openCommand");
    public JTextArea serverStatus = new JTextArea("Status is unknown");
    public JTextArea restartTimes = new JTextArea();
    private JTextArea currentTime = new JTextArea();
    private JCheckBox enableAutores;

    public Window(Main parent, Properties prop) throws HeadlessException {
        super("Minecraft_Server_Starter");
        this.parent = parent;
        this.prop = prop;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(new Dimension(640,480));
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);

        JPanel mainPanel = new JPanel(null);
        add(mainPanel);
//        setContentPane(mainPanel);
        JPanel leftPanel = new JPanel();
        leftPanel.setLocation(0,0);
        leftPanel.setSize(new Dimension(310,450));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Main part"));
        mainPanel.add(leftPanel);
        JPanel rightPanelGoLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rightPanelGoLeft.setLocation(315,0);
        rightPanelGoLeft.setSize(new Dimension(320,450));
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel,BoxLayout.Y_AXIS));
        rightPanel.setLocation(315,0);
        rightPanel.setPreferredSize(new Dimension(310,440));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Properties"));
        mainPanel.add(rightPanelGoLeft);
        rightPanelGoLeft.add(rightPanel);

        //right panel init
        JPanel btnsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnsPanel.setMaximumSize(new Dimension(310,40));
        rightPanel.add(btnsPanel);
        JButton reloadbtn = new JButton("Reload configs");
        reloadbtn.setSize(new Dimension(100,50));
        reloadbtn.addActionListener(e -> {
           reloadConfigs();
        });
        btnsPanel.add(reloadbtn);
        JButton savebtn = new JButton("Save configs");
        savebtn.setSize(new Dimension(100,50));
        savebtn.addActionListener(e -> {
            Properties properties = new Properties();
            properties.setProperty("ip",ip.getText());
            properties.setProperty("port",port.getText());
            properties.setProperty("timer",timer.getText());
            properties.setProperty("closeCommand",closeCommand.getText());
            properties.setProperty("openCommand",openCommand.getText());
            Main.saveProperties(properties);
            this.prop= properties;
            parent.reloadProrerties();
        });
        btnsPanel.add(savebtn);
        JPanel propPanel = new JPanel(new GridLayout(5,1));
        propPanel.setMaximumSize(new Dimension(3100,370));
        propPanel.setBorder(BorderFactory.createDashedBorder(Color.BLACK));
        rightPanel.add(propPanel);
        JPanel ipPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        propPanel.add(ipPanel);
        ipPanel.add(new JLabel("<html><strong>Ip = </strong></html>"));
        ip.setPreferredSize(new Dimension(200,40));
        ipPanel.add(ip);
        JPanel portPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        propPanel.add(portPanel);
        portPanel.add(new JLabel("<html><strong>Port = </strong></html>"));
        port.setPreferredSize(new Dimension(200,40));
        portPanel.add(port);
        JPanel timerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        propPanel.add(timerPanel);
        timerPanel.add(new JLabel("<html><strong>Timer = </strong></html>"));
        timer.setPreferredSize(new Dimension(200,40));
        timerPanel.add(timer);
        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        propPanel.add(closePanel);
        closePanel.add(new JLabel("<html><strong>Close = </strong></html>"));
        closeCommand.setPreferredSize(new Dimension(230,65));
        closeCommand.setLineWrap(true);
        closeCommand.setWrapStyleWord(true);
        closePanel.add(closeCommand);
        JPanel openPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        propPanel.add(openPanel);
        openPanel.add(new JLabel("<html><strong>Open = </strong></html>"));
        openCommand.setPreferredSize(new Dimension(230,65));
        openCommand.setLineWrap(true);
        openCommand.setWrapStyleWord(true);
        openPanel.add(openCommand);

//        left panel init
        enableAutores = new JCheckBox("Enable auto restarting");
        enableAutores.setSelected(false);
        JButton restartButton = new JButton("Restart now");
        restartButton.addActionListener(e -> parent.restartNow());
        leftPanel.add(restartButton);
        JPanel statusBar = new JPanel();
        statusBar.setBorder(BorderFactory.createTitledBorder("Status bar"));
        leftPanel.add(enableAutores);
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(BorderFactory.createTitledBorder("Status"));
        leftPanel.add(statusPanel);
        serverStatus.setPreferredSize(new Dimension(250,30));
        serverStatus.setEditable(false);
        serverStatus.setLineWrap(true);
        serverStatus.setWrapStyleWord(true);
        statusPanel.add(serverStatus);

        JPanel restartPanel = new JPanel();
        restartPanel.setPreferredSize(new Dimension(250,250));
        restartPanel.setBorder(BorderFactory.createTitledBorder("Restart Times"));
        leftPanel.add(restartPanel);
        restartTimes.setEditable(false);
        JScrollPane restartPane = new JScrollPane(restartTimes,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        restartPane.setPreferredSize(new Dimension(230,220));
        restartPanel.add(restartPane);


        JPanel timePanel = new JPanel();
        timePanel.setPreferredSize(new Dimension(250,30));
        leftPanel.add(timePanel);
        currentTime.setPreferredSize(new Dimension(250,30));
        currentTime.setForeground(Color.GRAY);
        timePanel.add(currentTime);
        Timer time = new Timer(10, e -> currentTime.setText(new SimpleDateFormat("HH:mm:ss dd.MM.yyyy")
                .format(Calendar.getInstance().getTime())));
        time.setRepeats(true);
        time.start();

        validate(); //without it doesn't work normally
        Timer startUpTimer = new Timer(500, e -> parent.loop());
        startUpTimer.setRepeats(false);
        startUpTimer.start();


        ip.setText(prop.getProperty("ip"));
        port.setText(prop.getProperty("port"));
        timer.setText(prop.getProperty("timer"));
        closeCommand.setText(prop.getProperty("closeCommand"));
        openCommand.setText(prop.getProperty("openCommand"));
        if (prop.getProperty("enableAutoRestart") != null) {
            enableAutores.setSelected(Boolean.parseBoolean(prop.getProperty("enableAutoRestart")));
        }
    }

    private void reloadConfigs(){
        prop = Main.readProperties();
        ip.setText(prop.getProperty("ip"));
        port.setText(prop.getProperty("port"));
        timer.setText(prop.getProperty("timer"));
        closeCommand.setText(prop.getProperty("closeCommand"));
        openCommand.setText(prop.getProperty("openCommand"));
    }
    public boolean isEnableAutoRestart(){
        return enableAutores.isSelected();
    }
}
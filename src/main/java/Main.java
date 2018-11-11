// Created by Veniamin_arefev
// Date was 10.07.2018


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import static java.lang.Integer.parseInt;

public class Main implements ActionListener {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private Window window;
    private Runtime runtime;
    private Properties properties;
    private static Timer timer;
    private boolean serverWasOnline = false;
    private static final String configFileName = "configmss.properties";



    public Main() {
        try {
            LogManager.getLogManager().readConfiguration(Main.class.getResourceAsStream("logging.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        properties = readProperties();
        runtime = Runtime.getRuntime();
        window = new Window(this,properties);
        timer = new Timer((parseInt(properties.getProperty("timer")) * 1000), this);
        timer.setActionCommand("tick");
        timer.setRepeats(true);
        timer.start();
        logger.log(Level.INFO, "MineStarter is on now");
    }

    public static void main(String[] args) {
        Main main = new Main();
    }

    public void loop() {
        try {
            if (ping(properties.getProperty("ip"), Integer.parseInt(properties.getProperty("port")))) {
                if (!serverWasOnline) {
                    logger.log(Level.INFO, "Server online");
                    serverWasOnline = true;
                }
                window.serverStatus.setText("Server was online on " +
                        new SimpleDateFormat("HH:mm:ss dd.MM.yyyy").format(Calendar.getInstance().getTime()));
                window.serverStatus.setForeground(Color.GREEN);
            }
            else {
                logger.log(Level.WARNING, "Server offline");
                window.serverStatus.setText("Server was offline on " +
                        new SimpleDateFormat("HH:mm:ss dd.MM.yyyy").format(Calendar.getInstance().getTime()));
                window.serverStatus.setForeground(Color.RED);
                window.restartTimes.append(new SimpleDateFormat("HH:mm:ss dd.MM.yyyy").format(Calendar.getInstance().getTime())+"\n");
                serverWasOnline = false;
                restartServer();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e, String::new);
        }
    }
    private void restartServer(){
        try {
            if (properties.getProperty("closeCommand") != null && !properties.getProperty("closeCommand").equals("closeCommand")
                    && !properties.getProperty("closeCommand").isEmpty()) {
                runtime.exec(properties.getProperty("closeCommand"));
                logger.log(Level.FINE, "Close command executed");
                Thread.sleep(100L);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e, String::new);
        }
        try {
            if (properties.getProperty("openCommand") != null && !properties.getProperty("openCommand").equals("openCommand")
                    && !properties.getProperty("openCommand").isEmpty()) {
                logger.log(Level.FINE, "Open command executed");
                runtime.exec(properties.getProperty("openCommand"));
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e, String::new);
        }
    }

    public void restartNow(){
        timer.restart();
        logger.log(Level.WARNING, "Server restarted manually");
        window.serverStatus.setText("Server was restarted on " +
                new SimpleDateFormat("HH:mm:ss dd.MM.yyyy").format(Calendar.getInstance().getTime()));
        window.serverStatus.setForeground(Color.ORANGE);
        window.restartTimes.append("M "+new SimpleDateFormat("HH:mm:ss dd.MM.yyyy").format(Calendar.getInstance().getTime())+"\n");
        serverWasOnline = false;
        restartServer();
    }
    public void reloadProrerties() {
        this.properties = readProperties();
        timer.stop();
        timer.setDelay(parseInt(properties.getProperty("timer"))*1000);
        timer.start();
    }

    public static Properties readProperties() {
        Properties properties = new Properties();
        properties.setProperty("ip","localhost");
        properties.setProperty("port","25565");
        properties.setProperty("timer","20");
        try {
            File file = new File(new File(new File("").getAbsolutePath()), configFileName);
            if (file.exists()) {
                properties.load(new FileInputStream(configFileName));
            }
            else {
                logger.log(Level.WARNING, "Config file doesn't exist");
                Properties prop1 = new Properties(properties);
                prop1.setProperty("enableAutoRestart","true"); //only for first time config
                saveProperties(prop1);
                return properties;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e, String::new);
            e.printStackTrace();
        }
        if (parseInt(properties.getProperty("timer")) < 20) {
            properties.setProperty("timer","20");
        }

        return properties;
    }

    public static void saveProperties(Properties properties) {
        try {
            properties.store(new FileOutputStream(new File(new File(new File("").getAbsolutePath()), configFileName)),"");
        } catch (Exception e) {
            logger.log(Level.SEVERE, e, String::new);
            e.printStackTrace();
        }
    }

    private static boolean ping(String ip, int port) { //ip == host
        Socket socket = new Socket();
        try {
            socket.setSoTimeout(3000);
            socket.connect(new InetSocketAddress(ip, port), 3000);
            DataInputStream datainputstream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataoutputstream = new DataOutputStream(socket.getOutputStream());
            dataoutputstream.write(254);
            if (datainputstream.read() != 255) {
                logger.log(Level.FINE,"Bad ping message");
            }

            socket.close();
            return true;
        } catch (Exception e) {
            logger.log(Level.FINE,"Ping to server failed");
            return false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand().equals("tick") && window.isEnableAutoRestart()) {
            loop();
        }
    }
}


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

@SuppressWarnings("deprecation")
public class app {

    private static JFrame frame = new JFrame("EasyServer");
    static String folder = ".";
    static int serverPort = 8080;

    public static void main(String[] args){
        buildFrame();
    }

    private static void buildFrame(){
        frame.setPreferredSize(new Dimension(410,350));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        }catch (Exception e){
            System.err.println("Line 26 Error: " + e.getMessage());
        }
        JPanel content = new JPanel();
        content.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        frame.setContentPane(content);

        JMenuBar menuBar;
        JMenu menu;
        JMenuItem menuItem;

        menuBar = new JMenuBar();

        menu = new JMenu("Options");
        menuBar.add(menu);

        menuItem = new JMenuItem("Force Server Shutdown");
        menuItem.getAccessibleContext().setAccessibleDescription(
                "If the application window won't close, use this.");
        menuItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                System.exit(0);
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Set Port");
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Use this to change the port EasyServer uses.");
        menuItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                JFrame setPort = new JFrame("Set Port");
                setPort.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                setPort.setPreferredSize(new Dimension(300,120));
                try {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                }catch (Exception ex){
                    System.err.println("Line 22 Error: " + ex.getMessage());
                }
                JTextField text = new JTextField(1);
                setPort.getContentPane().add(text, BorderLayout.BEFORE_FIRST_LINE);
                JLabel status = new JLabel("<html>Enter a port to see if it can be used.</html>");
                setPort.getContentPane().add(status, BorderLayout.AFTER_LAST_LINE);
                JButton button = new JButton("Set Port");
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int port = Integer.parseInt(text.getText());
                        if(portIsAvailable(port) && isPortNotCommonlyUsed(port)){
                            serverPort = port;
                            status.setText("<html>Port successfully changed. Remember that this does not guarantee that EasyServer will work on this port.</html>");
                        }else{
                            status.setText("Port invalid. Try again.");
                        }
                    }
                });
                setPort.getContentPane().add(button, BorderLayout.CENTER);
                setPort.pack();
                setPort.setVisible(true);
            }
        });
        menu.add(menuItem);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);

        //JPanel textPanel = new JPanel();
        //textPanel.setMinimumSize(new Dimension(300,150));
        JLabel mainText = new JLabel("<html>Welcome to EasyServer! Simply find the folder containing your website's" +
                                          " files by clicking the button below, then click the 'Start Server' button at " +
                                          "the bottom. To shut down the server, close this window.<br><br/>");
        Font textfont = new Font("Arial", Font.PLAIN, 14);
        mainText.setFont(textfont);
        //textPanel.add(mainText);
        frame.getContentPane().add(mainText);

        JButton startButton = new JButton("Start Server");

        JButton findButton = new JButton("Select Server Files");
        findButton.setFont(textfont);
        findButton.setMaximumSize(new Dimension(300,80));
        findButton.getAccessibleContext().setAccessibleDescription(
                "Make sure you have files in your folder named 'index.html'" +
                "website's home page), '404.html' (for when a user tries to access an non-existent " +
                "webpage), and 'not_supported.html' (for when a user tries to interact with the " +
                "website in a way that EasyServer can't support).");
        findButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);
                if (chooser.showOpenDialog(findButton) == JFileChooser.APPROVE_OPTION) {
                    File[] files = chooser.getSelectedFile().listFiles();
                    if(hasNeededFiles(files)) {
                        setFolder(chooser.getSelectedFile().toString());
                        findButton.setEnabled(false);
                        startButton.setEnabled(true);
                    }else{
                        mainText.setText("<html>NOTE: Make sure you have files in your folder named 'index.html' (your " +
                                "website's home page), '404.html' (for when a user tries to access an non-existent " +
                                "webpage), and 'not_supported.html' (for when a user tries to interact with the " +
                                "website in a way that EasyServer can't support).</html>");
                    }
                }
                else {
                    System.out.println("No Selection ");
                }
            }
        });
        frame.getContentPane().add(findButton);

        startButton.setFont(textfont);
        startButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                serverStarter.main();
                startButton.setText("Server Started!");
                startButton.setEnabled(false);
            }
        });
        frame.getContentPane().add(startButton);

        frame.setLayout(new GridLayout(3,1, 10, 10));
        frame.pack();
        frame.setVisible(true);
        startButton.setEnabled(false);
    }

    static String getDefaultFile(){
        return "index.html";
    }

    private static boolean portIsAvailable(int port) {
        Socket s = null;
        try {
            s = new Socket("localhost", port);
            return false;
        } catch (IOException e) {
            return true;
        } finally {
            if( s != null){
                try {
                    s.close();
                } catch (IOException e) {
                    throw new RuntimeException("Error with figuring out if port is available." , e);
                }
            }
        }
    }

    private static boolean hasNeededFiles(File[] files){
        boolean hasIndex = false;
        boolean hasNot = false;
        boolean has404 = false;
        for(File i : files){
            if(i.getName().equals("index.html")){
                hasIndex = true;
            }
            if(i.getName().equals("not_supported.html")){
                hasNot = true;
            }
            if(i.getName().equals("404.html")){
                has404 = true;
            }
        }
        if(hasIndex && hasNot && has404){
            return true;
        }else{
            return false;
        }
    }

    private static boolean isPortNotCommonlyUsed(int serverPort){
        int[] wellKnownPorts = new int[] {0,1,5,7,9,11,13,15,17,18,19,20,21,22,23,25,37,42,43,47,49,51,52,53,54,56,58,
                                          61,67,68,69,70,71,72,73,74,79,81,82,88,90,101,102,104,105,107,108,109,110,111,
                                          113,115,117,118,119,123,126,135,137,138,139,143,152,153,156,158,161,162,170,
                                          177,179,194,201,209,210,213,218,220,225,226,227,228,229,230,231,232,233,234,
                                          235,236,237,238,239,240,241,249,250,251,252,253,254,255,259,262,264,280,300,
                                          308,311,318,319,320,350,351,356,366,369,370,371,383,384,387,388,389,399,401,
                                          427,433,434,443,444,445,464,465,475,491,497,500,502,504,510,512,513,514,515,
                                          517,518,520,521,524,525,530,532,533,540,542,543,544,546,547,548,550,554,556,
                                          560,561,563,564,585,993,587,591,593,601,604,623,625,631,635,636,639,641,643,
                                          646,647,648,651,653,654,655,657,660,666,674,688,690,691,694,695,698,700,701,
                                          702,706,711,712,749,750,751,752,753,754,760,782,783,800,808,829,830,831,832,
                                          833,843,847,848,853,860,861,862,873,888,897,898,902,903,953,981,987,989,990,
                                          991,992,993,994,995,996,997,998,999,1000,1001,1002,1003,1004,1005,1006,1007,
                                          1008,1009,1010,1011,1012,1013,1014,1015,1016,1017,1018,1019,1020,1021,1022,1023};
        boolean works = true;
        for(int i : wellKnownPorts){
            if(i==serverPort){
                works = false;
            }
        }
        return works;
    }

    private static void setFolder(String folder1){
        folder = folder1;
    }
}

class serverStarter implements Runnable{
    public void run(){
        server.main();
    }
    static void main(){
        (new Thread(new serverStarter())).start();
    }
}
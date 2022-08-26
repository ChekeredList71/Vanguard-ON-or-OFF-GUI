package src;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;

public class App {
    static final JFrame f = new JFrame();
    static JLabel vanguardStatus;
    public static void main(String[] args) {
        startGUI();
    }

    public static void startGUI() {

        f.setTitle("Enable or disable Vanguard startup");
        ImageIcon img = new ImageIcon("lib/logo.png");
        f.setIconImage(img.getImage());
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton startV = new JButton("Enable startup");
        JButton stopV = new JButton("Disable startup");
        JButton killV = new JButton("Kill Vanguard process");
        vanguardStatus = new JLabel("<html>"+"Vanguard status: " + getVanguardStatus() + "</html>");

        startV.setBounds(10, 10, 120, 40);
        stopV.setBounds(140, 10, 120, 40);
        killV.setBounds(270, 10, 160, 40);
        vanguardStatus.setBounds(10, 70, 380, 55);

        f.add(startV);
        f.add(stopV);
        f.add(killV);
        f.add(vanguardStatus);

        f.setSize(455, 200);
        f.setLayout(null);
        f.setLocationRelativeTo(null);
        f.setVisible(true);

        // on press
        startV.addActionListener(e -> startVanguard());

        stopV.addActionListener(e -> stopVanguard());

        killV.addActionListener(e -> killVanguard());
    }

    public static void startVanguard() {

        // check for admin
        if (WindowsAdminUtil.isUserWindowsAdmin()) {
            try {
                String[] commands = {"sc config vgc start= demand", "sc config vgk start= system"};
                cmdRunner.run(commands);

            } catch (IOException e) {

                JOptionPane.showMessageDialog(f, "IOException occurred lmao", "Error",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
            //Ask if user wants to restart
            if (JOptionPane.showConfirmDialog(f, "Vanguard will start on next powerup. Restart now?",
                    "Success",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try {
                    Process proc = Runtime.getRuntime().exec("shutdown -r -t 0");
                    proc.destroy();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(f, "IOException occurred lmao", "Error", JOptionPane.
                            ERROR_MESSAGE);
                    System.exit(0);
                }
            }
        } else {
            JOptionPane.showMessageDialog(f, "This command cannot run without admin rights. Restart app as admin.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        vanguardStatus.setText("<html>" + "Vanguard status: " + getVanguardStatus() + "</html>");
    }

    public static void stopVanguard() {
        if (WindowsAdminUtil.isUserWindowsAdmin()) {
            try {
                String[] commands = {"sc config vgc start= disabled", "sc config vgk start= disabled", "net stop vgc",
                        "net stop vgk", "taskkill /IM vgtray.exe"};
                cmdRunner.run(commands); //TODO: add if statement to check if vanguard died. Use: getVanguardStatus()
                JOptionPane.showMessageDialog(f, "Vanguard service is terminated, and it won't start next time." +
                                "\nNote: the tray icon will still appear.", "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {

                JOptionPane.showMessageDialog(f, "IOException occurred lmao", "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }

        } else {
            JOptionPane.showMessageDialog(f, "This command cannot run without admin rights. Restart app as admin.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        vanguardStatus.setText("<html>" + "Vanguard status: " + getVanguardStatus() + "</html>");
    }

    public static void killVanguard() {

        if (WindowsAdminUtil.isUserWindowsAdmin()) {
            try {
                String[] commands = {"net stop vgc",
                        "net stop vgk", "taskkill /IM vgtray.exe"};

                cmdRunner.run(commands);
                JOptionPane.showMessageDialog(f, "Vanguard process is terminated. It will still start on next " +
                                "powerup.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {

                JOptionPane.showMessageDialog(f, "IOException occurred lmao", "Error", JOptionPane.
                        ERROR_MESSAGE);
                System.exit(0);
            }
        } else {
            JOptionPane.showMessageDialog(f, "This command cannot run without admin rights. Restart app as admin.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        vanguardStatus.setText("<html>" + "Vanguard status: " + getVanguardStatus() + "</html>");
    }

    public static String getVanguardStatus() {

        try {
            ArrayList<String> outputVgk = cmdRunner.run("sc query vgk");
            ArrayList<String> outputVgc = cmdRunner.run("sc query vgc");

            String vgkStatus =
                outputVgk.get(3).contains("RUNNING") ? "RUNNING"
                : outputVgk.get(3).contains("STOPPED") ? "STOPPED"
                : outputVgk.get(3).contains("START_PENDING") ? "START_PENDING"
                : "fail";

            String vgcStatus =
                outputVgc.get(3).contains("RUNNING") ? "RUNNING"
                : outputVgc.get(3).contains("STOPPED") ? "STOPPED"
                : outputVgc.get(3).contains("PAUSED") ? "PAUSED"
                : outputVgc.get(3).contains("START_PENDING") ? "START_PENDING"
                : "fail";

            if (vgkStatus.equals("RUNNING") && vgcStatus.equals("RUNNING")) {
                return "RUNNING";
            } else if (vgkStatus.equals("STOPPED") && vgcStatus.equals("STOPPED")) {
                return "STOPPED";
            } else if (vgkStatus.equals("START_PENDING") && vgcStatus.equals("START_PENDING")) {
                return "START_PENDING";
            } else if (!(vgkStatus.equals("fail") && vgcStatus.equals("fail"))) {
                return String.format("NOT RUNNING PROPERLY<br/>vgk service: %s<br/>vgc service: %s", vgkStatus, vgcStatus);
            }

        } catch (IOException e) {
            return "failed to run command";
        } catch (IndexOutOfBoundsException e) {
            //noinspection UnnecessarySemicolon
            ;
        }
        return "failed to check, probably due to non english cmd";
    }
}

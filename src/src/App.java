package src;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class App {
    public static void main(String[] args) {

        startGUI();
    }

    static JFrame f = new JFrame();

    public static void startGUI() {

        f.setTitle("Enable or disable Vanguard startup");
        ImageIcon img = new ImageIcon("lib/logo.png");
        f.setIconImage(img.getImage());
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton startV = new JButton("Enable startup");
        JButton stopV = new JButton("Disable startup");
        JButton killV = new JButton("Kill Vanguard process");
        JLabel vanguardStatus = new JLabel("Vanguard status: " + getVanguardStatus());

        startV.setBounds(10, 10, 120, 40);
        stopV.setBounds(140, 10, 120, 40);
        killV.setBounds(270, 10, 160, 40);
        vanguardStatus.setBounds(10, 60, 300, 40);

        f.add(startV);
        f.add(stopV);
        f.add(killV);
        f.add(vanguardStatus);

        f.setSize(455, 200);
        f.setLayout(null);
        f.setVisible(true);

        // on press
        startV.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                startVanguard();
            }
        });

        stopV.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                stopVanguard();
            }
        });

        killV.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                killVanguard();
            }
        });
    }

    public static void startVanguard() {

        // check for admin
        if (!WindowsAdminUtil.isUserWindowsAdmin()) { // TODO: Test startVanguard() with admin rights
            try {
                String[] commands = { "sc config vgc start= demand", "sc config vgk start= system" };
                cmdRunner.run(commands);

            } catch (IOException e) {

                JOptionPane.showMessageDialog(f, "IOException occurred lmao", "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
            if (JOptionPane.showConfirmDialog(f, "Vanguard will start on next powerup. Restart now?", "Done",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try {
                    Process proc = Runtime.getRuntime().exec("shutdown -r -t 0");
                    proc.destroy();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(f, "IOException occurred lmao", "Error", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }

            }
        } else {
            JOptionPane.showMessageDialog(f, "This command cannot run without admin rights. Restart app as admin.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    public static void stopVanguard() {
        if (WindowsAdminUtil.isUserWindowsAdmin()) { // TODO: Test stopVanguard() with admin rights
            try {
                String[] commands = { "sc config vgc start= disabled", "sc config vgk start= disabled", "net stop vgc",
                        "net stop vgk", "taskkill /IM vgtray.exe" };
                cmdRunner.run(commands);

            } catch (IOException e) {

                JOptionPane.showMessageDialog(f, "IOException occurred lmao", "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }

        } else {
            JOptionPane.showMessageDialog(f, "This command cannot run without admin rights. Restart app as admin.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void killVanguard() {
        if (WindowsAdminUtil.isUserWindowsAdmin()) {
            try {
                String[] commands = { "net stop vgc",
                        "net stop vgk", "taskkill /IM vgtray.exe" };

                cmdRunner.run(commands);
            } catch (IOException e) {

                JOptionPane.showMessageDialog(f, "IOException occurred lmao", "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        } else {
            JOptionPane.showMessageDialog(f, "This command cannot run without admin rights. Restart app as admin.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    public static String getVanguardStatus() {

        ArrayList<String> output = new ArrayList<String>();
        try {
            Runtime rt = Runtime.getRuntime();
            String[] commands = { "sc", "query", "vgk" };
            Process proc = rt.exec(commands);

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            // Read the output from the command
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                output.add(s);
            }
        } catch (IOException e) {

            JOptionPane.showMessageDialog(f, "IOException occurred lmao", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        if (Locale.getDefault().toString().contains("EN")) {
            return "failed to check due to non english OS";
        }

        try {
            if (Integer.parseInt(output.get(3).substring(29, 30)) == 4) // get the number from cmd output example: " STATE :
                // 1 STOPPED"
                return "RUNNING";
            else if (Integer.parseInt(output.get(3).substring(29, 30)) == 1)
                return "STOPPED";
        } catch (StringIndexOutOfBoundsException e) {
            ;
        }

        return "failed to check aaaaaaaaaa";
    }
}
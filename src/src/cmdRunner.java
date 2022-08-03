package src;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class cmdRunner {
    public static ArrayList<String> run(String[] commands) throws IOException {
        ArrayList<String> output = new ArrayList<String>();
        for (String i : commands) {
            Process proc = Runtime.getRuntime().exec(i);
            // Read the output from the command
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            String s = null;
            while ((s = stdInput.readLine()) != null) {
                output.add(s);
            }
        }
        return output;
    }

    public static ArrayList<String> run(String command) throws IOException {
        ArrayList<String> output = new ArrayList<String>();

        Process proc = Runtime.getRuntime().exec(command);
        // Read the output from the command
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

        String s = null;
        while ((s = stdInput.readLine()) != null) {
            output.add(s);
        }

        return output;
    }
}
package org.vitrivr.cineast.explorative;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CommandLineUsing {

    public static void main(String[] args) {

        CommandLineUsing obj = new CommandLineUsing();

        //in mac oxs
        String testPath = "/Users/Raphael/Desktop/DeepSpeech/models/";
        String command = "deepspeech " + testPath + "output_graph.pbmm " +
               testPath + "1.wav " +
                testPath + "alphabet.txt " +
                testPath + "lm.binary " + testPath + "trie";



        //in windows
        //String command = "ping -n 3 " + domainName;

        String output = obj.executeCommand(command);

        System.out.println(output);

    }

    private String executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();

    }

}
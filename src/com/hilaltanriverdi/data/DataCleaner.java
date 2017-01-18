package com.hilaltanriverdi.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class DataCleaner {

    public static void main(String[] args) throws Exception {
        String dataFolder="D:\\Hadoop\\bin";
        String sourcePath=dataFolder+"\\2008_All_With_NA.csv";
        String targetPath=dataFolder+"\\2008_All_Without_NA.csv";
        FileReader fr = new FileReader(sourcePath);
        BufferedReader br = new BufferedReader(fr);
        FileWriter fw = new FileWriter(targetPath);
        BufferedWriter bw = new BufferedWriter(fw);

        String line;
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split(";");
            if((!tokens[6].contains("NA") && !tokens[7].contains("NA")) ){
                bw.write(line+"\r\n");
            }
        }
        br.close();
        bw.close();
        System.out.println("Done.");
    }
}

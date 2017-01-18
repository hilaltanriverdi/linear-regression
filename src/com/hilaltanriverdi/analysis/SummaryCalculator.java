package com.hilaltanriverdi.analysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SummaryCalculator {

    public Summary summarize(String filePath, int fieldIndex) throws IOException {
        Summary s = new Summary();
        FileReader fr = new FileReader(filePath);
        BufferedReader br = new BufferedReader(fr);
        String line;
     
        double x;
        double y;
    
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split("\t");
            x = Double.parseDouble(tokens[0]);
            y = Double.parseDouble(tokens[fieldIndex]);
            s.setSx(s.getSx()+x);
            s.setSy(s.getSy()+y);
            s.setSxx(s.getSxx()+x*x);
            s.setSxy(s.getSxy()+x*y);
            s.setSyy(s.getSyy()+y*y);
            s.setN(s.getN()+1);
        }
        br.close();
        return s;
    }
}

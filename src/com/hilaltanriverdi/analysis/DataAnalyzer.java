package com.hilaltanriverdi.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.Math.*;

public class DataAnalyzer {

    public void findErrors(String dataPath, int fieldIndex, Summary s) throws IOException {
        FileReader fr = new FileReader(dataPath);
        BufferedReader br = new BufferedReader(fr);
        String line;

        double x;
        double y;
        s.setSse(0);
        s.setSst(0);
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split("\t");
            x = Double.parseDouble(tokens[0]);
            y = Double.parseDouble(tokens[fieldIndex]);
            double ye = findYEstimate(s.getLa(), s.getLb(), x);
            s.setSse(s.getSse() + (y - ye) * (y - ye));
            s.setSst(s.getSst() + (y - s.getYm()) * (y - s.getYm()));
        }
        br.close();
    }

   

    public double findYEstimate(double a, double b, double x) {
        return a + b * x;
    }

    public void findComparisons(String dataPath,String comparisonPath,
            int departureIndex,int arrivalIndex,
            Summary sd,Summary sa)
        throws IOException
    {
        FileReader fr = new FileReader(dataPath);
        BufferedReader br = new BufferedReader(fr);
        FileWriter fw=new FileWriter(comparisonPath);
        BufferedWriter bw=new BufferedWriter(fw);
        
        bw.write("DISTANCE\tDEPARTURE DELAY\tARRIVAL DELAY\tESTIMATED DEPARTURE DELAY\tESTIMATED ARRIVAL DELAY\r\n");
          
        String line;
        double x;
        double yd;
        double ya;
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split("\t");
            x = Double.parseDouble(tokens[0]);
            yd= Double.parseDouble(tokens[departureIndex]);
            ya = Double.parseDouble(tokens[arrivalIndex]);
            double yed = findYEstimate(sd.getLa(), sd.getLb(), x);
            double yea = findYEstimate(sa.getLa(), sa.getLb(), x);
              bw.write(x+"\t "
                +yd+"\t "
                +ya+"\t "
                +yed+"\t "
                +yea+"\r\n");
        
        }
        br.close(); 
        bw.close();
    }

    public static void main(String[] args) throws Exception {

        String fileDirectory = "D:\\Hadoop\\bin";

        String dataPath = fileDirectory + "\\averages_separate.txt";
        //String dataPath = fileDirectory + "\\averages_separate_test.txt";
        String summaryPath = fileDirectory + "\\summary_separate.txt";
        String comparisonPath = fileDirectory + "\\comparison_separate.txt";

        int arrivalIndex = 1;
        int departureIndex = 2;

        // Summaries of Data
        SummaryCalculator calculator = new SummaryCalculator();
        Summary summaryArrival = calculator.summarize(dataPath, arrivalIndex);
        Summary summaryDeparture = calculator.summarize(dataPath, departureIndex);

        // Line Coefficients
        DataAnalyzer analyzer = new DataAnalyzer();
   

        // Find Errors
        analyzer.findErrors(dataPath, arrivalIndex, summaryArrival);
        //System.out.println("Arrival SSE  =" + summaryArrival.getSse());
        //System.out.println("Arrival SST  =" + summaryArrival.getSst());
        //System.out.println("Departure SSE/SST  =" + summaryArrival.getSse() / summaryArrival.getSst());
        analyzer.findErrors(dataPath, departureIndex, summaryDeparture);
        //System.out.println("Departure SSE  =" + summaryDeparture.getSse());
        //System.out.println("Departure SST  =" + summaryDeparture.getSst());
        //System.out.println("Departure SSE/SST  =" + summaryDeparture.getSse() / summaryDeparture.getSst());

        // Check Errors & Corelation
        //System.out.printf("Arrival r^2=         %20.9f\r\n", summaryArrival.getRSquare());
        //System.out.printf("Arrival 1-SSE/SST=   %20.9f\r\n", (summaryArrival.getSst() - summaryArrival.getSse()) / summaryArrival.getSst());
        //System.out.printf("Departure r^2=       %20.9f\r\n", summaryDeparture.getRSquare());
        //System.out.printf("Departure 1-SSE/SST= %20.9f\r\n", (summaryDeparture.getSst() - summaryDeparture.getSse()) / summaryDeparture.getSst());
    

        Summary.save(summaryPath, summaryDeparture, summaryArrival);

        // Find Data & Estimate Comparisons  
        
        analyzer.findComparisons(dataPath, comparisonPath,departureIndex,arrivalIndex,summaryDeparture,summaryArrival);
        
             
        
        System.out.println("ARRIVAL");
        System.out.println("\t R SQUARE "+String.format("%9.10f",summaryArrival.getRSquare()));
        System.out.println("\t INTERCEPT "+String.format("%9.10f",summaryArrival.getLa()));
        System.out.println("\t SLOPE "+String.format("%9.10f",summaryArrival.getLb()));
        
        System.out.println("");
        
        System.out.println("DEPARTURE");
        System.out.println("\t R SQUARE "+String.format("%9.10f",summaryDeparture.getRSquare()));
        System.out.println("\t INTERCEPT "+String.format("%9.10f",summaryDeparture.getLa()));
        System.out.println("\t SLOPE "+String.format("%9.10f",summaryDeparture.getLb()));
            
    }

}

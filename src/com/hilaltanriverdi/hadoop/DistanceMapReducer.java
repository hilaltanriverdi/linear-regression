package com.hilaltanriverdi.hadoop;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class DistanceMapReducer {
   
    private final static int INDEX_DELAY = 8;
    private final static int INDEX_ARRIVAL_DELAY = 6;
    private final static int INDEX_DEPARTURE_DELAY = 7;

    public static class DistanceDelayMapper
            extends Mapper<Object, Text, Text, Text> {

        private Text distance = new Text();
        private Text delays = new Text();

        public void map(Object key, Text value, Context context
        ) throws IOException, InterruptedException {
            StringTokenizer tokenizer = new StringTokenizer(value.toString(), "\r\n");
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();

                String[] tokens = token.split(";");

                distance.set(tokens[INDEX_DELAY]);
                int arrivalDelay = 0;
                try {
                    arrivalDelay = Integer.parseInt(tokens[INDEX_ARRIVAL_DELAY]);
                } catch (Exception e) {
                }

                int departureDelay = 0;
                try {
                    departureDelay = Integer.parseInt(tokens[INDEX_DEPARTURE_DELAY]);
                } catch (Exception e) {
                }

                delays.set(arrivalDelay + ";" + departureDelay);
                context.write(distance, delays);

            }
        }
    }

    public static class AverageDelayReducer
            extends Reducer<Text, Text, Text, Text> {

        private Text result = new Text();

        public void reduce(Text distance, Iterable<Text> delays,
                Context context) throws IOException, InterruptedException {
            try {
                Integer.parseInt(distance.toString());
            } catch (Exception e) {
                return;
            }
            int delayDeparture;
            int delayArrival;
            
            for (Text delay : delays) {

                String delayString = delay.toString();
                String[] tokens;
                if (delayString.contains(";")) {
                    tokens = delayString.split(";");
                } else {
                    tokens = delayString.split("\\s+");
                }
                delayArrival = Integer.parseInt(tokens[0].trim());
                delayDeparture = Integer.parseInt(tokens[1].trim());
                result.set(delayArrival + "\t" + delayDeparture);
                context.write(distance, result);

                //delayTotalDeparture += delayDeparture;
                //delayTotalArrival += delayArrival;                
                //delayCount++;
            }
            /*
             int delayAverageDeparture = delayTotalDeparture / delayCount;
             int delayAverageArrival = delayTotalArrival / delayCount;
             result.set(delayAverageDeparture+"\t"+delayAverageArrival);          
             context.write(distance, result);
             */
        }
    }

    public static void main(String[] args) throws Exception {

        long timeBefore = System.currentTimeMillis();
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length < 2) {
            System.err.println("Usage: averagedelay <in> [<in>...] <out>");
            System.exit(2);
        }
        Job job = Job.getInstance(conf, "average delay");
        job.setJarByClass(DistanceMapReducer.class);
        job.setMapperClass(DistanceDelayMapper.class);
        job.setCombinerClass(AverageDelayReducer.class);
        job.setReducerClass(AverageDelayReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        for (int i = 0; i < otherArgs.length - 1; ++i) {
            FileInputFormat.addInputPath(job, new Path(otherArgs[i]));
        }
        FileOutputFormat.setOutputPath(job,
                new Path(otherArgs[otherArgs.length - 1]));

        long timeAfter = System.currentTimeMillis();
        System.out.println("---- Time ms " + (timeAfter - timeBefore));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}

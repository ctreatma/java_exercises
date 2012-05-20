package edu.upenn.cis555.mustang.rank;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class SendingPageRankMapper extends MapReduceBase implements Mapper<Text, Text, Text, Text> {
    int sigDigits;
    
    @Override
    public void configure(JobConf job) {
        sigDigits = Integer.parseInt(job.get("sigDigits"));
    }    
    
    @Override
    public void map(Text key, Text value, OutputCollector<Text,Text> output, Reporter reporter)
            throws IOException {
        String[] rankAndInlinks = value.toString().split(" ");
        double rank = Double.parseDouble(rankAndInlinks[1]);
        
        rank = round(rank, sigDigits);
        output.collect(new Text(Double.toString(rank)), key);
    }

    private double round(double rank, int sigDigits) {
        BigDecimal rounder = new BigDecimal(rank);
        rounder = rounder.setScale(sigDigits, RoundingMode.HALF_UP);
        return rounder.doubleValue();
    }
}

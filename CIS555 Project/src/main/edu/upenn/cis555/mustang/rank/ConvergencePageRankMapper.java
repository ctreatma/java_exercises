package edu.upenn.cis555.mustang.rank;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class ConvergencePageRankMapper extends MapReduceBase implements Mapper<Text, Text, Text, Text> {
    double epsilon;
    
    @Override
    public void configure(JobConf job) {
        epsilon = Double.parseDouble(job.get("epsilon"));
    }

    @Override
    public void map(Text key, Text value, OutputCollector<Text,Text> output, Reporter reporter)
            throws IOException {
        String[] ranksAndLinks = value.toString().split(" ");
        
        double oldPageRank = Double.parseDouble(ranksAndLinks[0]);
        double newPageRank = Double.parseDouble(ranksAndLinks[1]);
        
        if (epsilon < Math.abs(newPageRank - oldPageRank)) {
            output.collect(new Text(Boolean.FALSE.toString()), key);
        }
        else {
            output.collect(new Text(Boolean.TRUE.toString()), key);
        }
    }
}

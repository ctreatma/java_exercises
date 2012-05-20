package edu.upenn.cis555.mustang.rank;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class IterativePageRankMapper extends MapReduceBase implements Mapper<Text, Text, Text, Text> {

    @Override
    public void map(Text key, Text value, OutputCollector<Text,Text> output, Reporter reporter)
            throws IOException {
        String[] ranksAndLinks = value.toString().split(" ");

        output.collect(key, new Text("old " + value));
        
        for (int i = 2; i < ranksAndLinks.length; ++i) {
            output.collect(new Text(ranksAndLinks[i]),
                    new Text(key + " " + ranksAndLinks[1] + " " + (ranksAndLinks.length - 2)));
        }
    }
}

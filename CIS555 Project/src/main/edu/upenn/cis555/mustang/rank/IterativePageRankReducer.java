package edu.upenn.cis555.mustang.rank;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class IterativePageRankReducer extends MapReduceBase implements Reducer<Text,Text,Text,Text> {
    double damping;
    
    @Override
    public void configure(JobConf job) {
        damping = Double.parseDouble(job.get("dampingFactor"));
    }
    
    @Override
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text,Text> output, Reporter reporter)
            throws IOException {
        
        StringBuffer links = new StringBuffer();
        double newPageRank = 0;
        double oldPageRank = 0;
        
        while (values.hasNext()) {
            String[] ranksAndLinks = values.next().toString().split(" ");
            
            if (ranksAndLinks[0].compareTo("old") == 0) {
                oldPageRank = Double.parseDouble(ranksAndLinks[2]);
                for (int i = 3; i < ranksAndLinks.length; ++i) {
                    links.append(" " + ranksAndLinks[i]);
                }
            }
            else {
                double linkRank = Double.parseDouble(ranksAndLinks[1]);
                double linkCount = Double.parseDouble(ranksAndLinks[2]);
                newPageRank += linkRank / linkCount;
            }
        }

        newPageRank = (damping * newPageRank) + (1 - damping);
        
        String pageRankAndInlinks = oldPageRank + " " + newPageRank + links.toString();
        output.collect(key, new Text(pageRankAndInlinks));
    }
}

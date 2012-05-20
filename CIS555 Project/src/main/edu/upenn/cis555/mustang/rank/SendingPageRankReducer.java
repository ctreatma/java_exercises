package edu.upenn.cis555.mustang.rank;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class SendingPageRankReducer extends MapReduceBase implements Reducer<Text,Text,Text,Text> {
    String dataStoreUrl;
    
    @Override
    public void configure(JobConf job) {
        dataStoreUrl = job.get("dataStoreUrl");
    }
    
    @Override
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text,Text> output, Reporter reporter)
    throws IOException {
        
        if (dataStoreUrl != null) {
            String content = marshalHttpRequest(key, values);

            URL dataStore = new URL(dataStoreUrl + PageRank.REST_PATH);
            HttpURLConnection conn = (HttpURLConnection) dataStore.openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "text/xml");
            conn.setRequestProperty("Content-Length", Integer.toString(content.getBytes().length));
            conn.connect();
            OutputStream os = conn.getOutputStream();
            os.write(content.getBytes());
            os.flush();
            os.close();
            conn.getResponseCode();
        }
    }

    private String marshalHttpRequest(Text key, Iterator<Text> values) {
        // Just using a StringBuffer rather than DOM objects, because we need
        // to be able to send the length of output as a header before sending
        // output, and that doesn't seem possible w/ DOM/Transformer.
        StringBuffer pageRanks = new StringBuffer();
        pageRanks.append("<pageRanks>");
        
        while (values.hasNext()) {
            pageRanks.append("<pageRank>");
            pageRanks.append("<docId>");
            pageRanks.append(values.next());
            pageRanks.append("</docId>");
            pageRanks.append("<rank>");
            pageRanks.append(key);
            pageRanks.append("</rank>");
            pageRanks.append("</pageRank>");
        }
        
        pageRanks.append("</pageRanks>");
        
        return pageRanks.toString();
    }
}

package edu.upenn.cis555.mustang.rank;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class PageRank extends Configured implements Tool {
    public static final String REST_PATH = "/PageRank";
    public static final String REST_BODY_PATTERN = "<\\?xml\\s+.+\\?>"; 

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = getConf();
        conf.addResource(new Path(args[0]));
        Path inputPath = new Path(conf.get("inputPath"));
        Path outputPath = new Path(conf.get("outputPath"));

        FileSystem fs = FileSystem.get(conf);
        if (fs.exists(outputPath)) {
            fs.delete(outputPath, true);
        }


        if (args.length >= 2) {
            Path localInputPath = new Path(args[1]);
            fs.copyFromLocalFile(localInputPath, inputPath);
        }

        initializePageRanks(inputPath, outputPath, conf, fs);

        updatePageRanks(inputPath, outputPath, conf, fs);

        while (!checkPageRanksConverged(inputPath, new Path(conf.get("convergedPath")), conf, fs)) {
            updatePageRanks(inputPath, outputPath, conf, fs);
        }

        sendPageRanks(inputPath, outputPath, conf, fs);

        if (args.length >= 3) {
            Path localOutputPath = new Path(args[2]);
            fs.copyToLocalFile(outputPath, localOutputPath);
        }

        return 0;
    }

    private boolean checkPageRanksConverged(Path inputPath, Path outputPath, Configuration conf, FileSystem fs)
    throws Exception {
        JobConf job = new JobConf(conf);

        job.setJarByClass(PageRank.class);
        job.setMapperClass(ConvergencePageRankMapper.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setInputFormat(SequenceFileInputFormat.class);

        SequenceFileInputFormat.addInputPath(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);

        JobClient.runJob(job);

        boolean converged = true;

        for (FileStatus status : fs.listStatus(outputPath)) {
            Path path = status.getPath();
            if (!path.getName().startsWith("part-")) {
                continue;
            }
            FSDataInputStream fis = fs.open(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = reader.readLine()) != null) {
                converged = converged && Boolean.parseBoolean(line.split("\t")[0]);
            }
            reader.close();
        }

        // Remove outputPath
        if (fs.exists(outputPath)) {
            fs.delete(outputPath, true);
        }

        return converged; 
    }

    private void sendPageRanks(Path inputPath, Path outputPath, Configuration conf, FileSystem fs)
    throws Exception {
        JobConf job = new JobConf(conf);

        job.setJarByClass(PageRank.class);
        job.setMapperClass(SendingPageRankMapper.class);
        job.setReducerClass(SendingPageRankReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setInputFormat(SequenceFileInputFormat.class);

        SequenceFileInputFormat.addInputPath(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);

        JobClient.runJob(job);
    }

    private void updatePageRanks(Path inputPath, Path outputPath, Configuration conf, FileSystem fs)
    throws Exception {
        JobConf job = new JobConf(conf);

        job.setJarByClass(PageRank.class);
        job.setMapperClass(IterativePageRankMapper.class);
        job.setReducerClass(IterativePageRankReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setInputFormat(SequenceFileInputFormat.class);
        job.setOutputFormat(SequenceFileOutputFormat.class);

        SequenceFileInputFormat.addInputPath(job, inputPath);
        SequenceFileOutputFormat.setOutputPath(job, outputPath);

        JobClient.runJob(job);

        // Remove inputPath, move outputPath to inputPath
        if (fs.exists(inputPath)) {
            fs.delete(inputPath, true);
            fs.rename(outputPath, inputPath);
        }
    }

    private void initializePageRanks(Path inputPath, Path outputPath, Configuration conf, FileSystem fs)
    throws Exception {
        JobConf job = new JobConf(conf);

        job.setJarByClass(PageRank.class);
        job.setMapperClass(InitialPageRankMapper.class);
        job.setReducerClass(InitialPageRankReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setOutputFormat(SequenceFileOutputFormat.class);

        FileInputFormat.addInputPath(job, inputPath);
        SequenceFileOutputFormat.setOutputPath(job, outputPath);

        JobClient.runJob(job);

        // Remove inputPath, move outputPath to inputPath
        if (fs.exists(inputPath)) {
            fs.delete(inputPath, true);
            fs.rename(outputPath, inputPath);
        }
    }

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new PageRank(), args);
        System.exit(res);
    }
}

package com.hadooptest.mr.hlnum;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.log4j.BasicConfigurator;

import java.io.IOException;

public class HlnumLogDriver {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        //1.创建 Configuration
        Configuration configuration=new Configuration();
        configuration.set("fs.defaultFS", "hdfs://###.186.240:8020");
        configuration.set("dfs.client.use.datanode.hostname", "true");//重点配置 否则本地调试返回阿里云内网IP

        //打印日志
        BasicConfigurator.configure();

        //创建Job之前，准备清理已经存在的输出目录
        Path outputPath= new Path("hdfs://###.186.240:8020/hlnumlog/output/");
        FileSystem fileSystem = FileSystem.get(configuration);
        if(fileSystem.exists(outputPath)){
            fileSystem.delete(outputPath,true);
            System.out.println("输出文件夹存在且已被删除");
        }

        //2.创建job，通过getInstance 拿到一个实例
        Job job= Job.getInstance(configuration,"hlnumlog");
        //3.设置Job的处理类
        job.setJarByClass(HlnumLogDriver.class);
        //4.设置作业处理的输入路径
        FileInputFormat.setInputPaths(job,new Path("hdfs://###.186.240:8020/hlnumlog/input/"));

        //************5.设置map相关参数
        //设置map的处理类
        job.setMapperClass(HlnumMapper.class);
        //设置map输出参数的类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        //************6.设置reduce相关参数
        //设置reduce的处理类
        job.setReducerClass(HlnumReducer.class);
        //设置reduce输出参数的类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        //7.设置作业的输出路径
        FileOutputFormat.setOutputPath(job,new Path("hdfs://###.186.240:8020/hlnumlog/output/"));
        //8.提交结果
        //参数true表示将运行进度等信息及时输出给用户，false的话只是等待作业结束
        boolean result=job.waitForCompletion(true);
        System.exit(result? 0: 1);
    }
}

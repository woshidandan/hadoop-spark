package mapreduce.example;

import hadoopmap.MyWordCount;

import java.io.IOException;

import junit.framework.Test;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class DataMapreduce {

	// Mapper 类 key ,value 手机号码,自定义数据类型
	static class DataMapper extends
			Mapper<LongWritable, Text, Text, DataWritable> {

		private Text mapOutputKey = new Text();
		private DataWritable dataWritable = new DataWritable();

		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			// 获取数据
			String lineValue = value.toString();
			// 数据分割
			String[] strs = lineValue.split("\t");
			// 得到数据
			String phoneNum = strs[1];
			int upPackNum = Integer.valueOf(strs[6]);
			int downPackNum = Integer.valueOf(strs[7]);
			int upPayLoad = Integer.valueOf(strs[8]);
			int downPayLoad = Integer.valueOf(strs[9]);

			// 设置map的输出key/value
			mapOutputKey.set(phoneNum);
			dataWritable.set(upPackNum, upPayLoad, downPackNum, downPayLoad);

			// 设置map输出
			context.write(mapOutputKey, dataWritable);
		};
	}

	// Reduce类 手机号码,自定义数据类型,统计好的手机号码,自定义数据类型
	static class DataReducer extends
			Reducer<Text, DataWritable, Text, DataWritable> {

		private DataWritable dataWritable = new DataWritable();

		public void reduce(Text key, Iterable<DataWritable> values,
				Context context) throws IOException, InterruptedException {
			int upPackNum = 0;
			int downPackNum = 0;
			int upPayLoad = 0;
			int downPayLoad = 0;

			// 循环
			for (DataWritable data : values) {
				upPackNum += data.getUpPackNum();
				downPackNum += data.getDownPackNum();
				upPayLoad += data.getUpPayLoad();
				downPayLoad += data.getDownPayLoad();
			}
			// 设置输出的dataWritable
			dataWritable.set(upPackNum, upPayLoad, downPackNum, downPayLoad);

			// 设置reduce/job的输出
			context.write(key, dataWritable);

		};
	}

	// 驱动
	public int run(String[] args) throws Exception {
		// 获取配置
		Configuration conf = new Configuration();

		// 创建Job,设置配置和Job名称
		Job job = new Job(conf, DataMapreduce.class.getSimpleName());

		// 1：设置Job运行的类
		job.setJarByClass(DataMapreduce.class);

		// 2:设置Mapper和Reducer类
		job.setMapperClass(DataMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(DataWritable.class);
		
		job.setReducerClass(DataReducer.class);
		job.setOutputKeyClass(Test.class);
		job.setOutputValueClass(DataWritable.class);
	

		// 3：设置输入文件的目录，和输出文件的目录
        Path intputDir = new Path(args[0]);
        FileInputFormat.addInputPath(job, intputDir);
        
        Path outputDir = new Path(args[1]);
        FileOutputFormat.setOutputPath(job, outputDir);
        
		
		// 4：设置输出结果key和value类型

		// 5：提交Job，等待运行结果，并在客户端显示运行信息
		boolean isSuccess = job.waitForCompletion(true);
		return isSuccess ? 0 : 1;

	}

	// 跑
	public static void main(String[] args) throws Exception {
		// set args
		args = new String[] {
				"hdfs://hadoop-master.dragon.org:9000/opt/wc/input/",
				"hdfs://hadoop-master.dragon.org:9000/opt/wc/output/", };

		// run job
		int status = new DataMapreduce().run(args);
		// exit
		System.exit(status);
	}

}

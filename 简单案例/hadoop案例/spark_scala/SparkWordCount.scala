import org.apache.spark.{SparkConf, SparkContext}
object SparkWordCount {
  def main(args:Array[String]){

    val conf = new SparkConf().setAppName("Spark Exercise:Spark Version Word Count Progran").setMaster("local");
    val sc = new SparkContext(conf);
    val textFile = sc.textFile("D:\\data\\come.txt");
    val wordCounts = textFile.flatMap(line => line.split(" ")).map(word => (word,1)).reduceByKey((a,b) => a+b);
    val SortWord = wordCounts.map(c => (c._1, " "+c._2)).foreach(println);

  }
}

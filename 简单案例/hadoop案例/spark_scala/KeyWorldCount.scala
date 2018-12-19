import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by 2281444815 on 2016/11/15.
  */
object KeyWorldCount {
  def main(args: Array[String]): Unit = {
    val args = "D:\\data\\keyworddata.txt";
    if (args.length < 1) {
      println("Please some useful words");
      System.exit(1)
    }
    val conf = new SparkConf().setAppName("Key World Count").setMaster("local");
    val sc = new SparkContext(conf);
    val textFile = sc.textFile(args, 1);
    //将句子分割为(key,value)对
    val wordCounts = textFile.flatMap(line => line.split(" "))
      .map(word => (word, 1)).reduceByKey((a, b) => (a + b));
    //将(key,value)转化为(value,key),用value值进行排序，
    // 此时集合已变为(value,key)，若要想变回原来的集合，可在后面加上
    // .map(c => (c._2, c._1)).sortByKey(false)
    //其中map、sortByKey都是懒操作，如果没有触发计算的foreach（action），将不会对集合产生影响
    val SortWord = wordCounts.map(c => (c._2, c._1))
      .sortByKey(false).map(c => (c._2, c._1)).foreach(println);
  }
}

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.annotation.{DeveloperApi, Experimental}
/**
  * Computes the PageRank of URLs from an input file. Input file should
  * be in format of:
  * URL         neighbor URL
  * URL         neighbor URL
  * URL         neighbor URL
  * ...
  * where URL and their neighbors are separated by space(s).
  *
  * This is an example implementation for learning how to use Spark. For more conventional use,
  * please refer to org.apache.spark.graphx.lib.PageRank
  */
/**
  * Created by 2281444815 on 2016/11/21.
  */
object PageRank {
  org.apache.spark.graphx.lib.PageRank
  def main(args: Array[String]) {
    val args  = "D:\\pagerankdata.txt";
    if (args.length < 1) {
      System.err.println("Usage: SparkPageRank <file> <iter>")
      System.exit(1)
    }

//    7 has rank: 1.0000402965342512.
//    2 has rank: 1.0000805930685026.
//    3 has rank: 1.0000402965342512.
//    1 has rank: 1.0.

//    7 has rank: 1.2982456140350873.
//    6 has rank: 0.7017543859649121.
//    2 has rank: 1.0000805930685026.
//    3 has rank: 0.9999999999999998.
//    1 has rank: 1.0.
    val sparkConf = new SparkConf().setAppName("PageRank").setMaster("local");

    val ctx = new SparkContext(sparkConf)
    val lines = ctx.textFile(args, 1)

    //初始化迭代次数，迭代次数参考《数学之美》中提到：“一般来讲，只要10次左右的迭代基本上就收敛了”
    //这里的args(1).toInt值为58，至于为什么取args(1)中的值，目前没有发现其实质的意义。
    val iters = if (args.length > 1) args(1).toInt else 10

    /*根据边关系数据生成 邻接表 如：(1,(2,3,4,5)) (2,(1,5))..
    注意此句中有个groupByKey方法，如果我们的数据是
    7 3
    7 6                     7 6 3
    6 7 这种格式，是和我们用6 7   是不同的，在这个案例中，后者无法得到正确的答案*/
    val links = lines.map{ s =>
      val parts = s.split("\\s+")
      (parts(0), parts(1))
    }.distinct().groupByKey()
    //.cache()

    //将每个页面的排序值初始化为1.0
    var ranks = links.mapValues(v => 1.0)

    /**
      * Return an RDD containing all pairs of elements with matching keys in `this` and `other`. Each
      * pair of elements will be returned as a (k, (v1, v2)) tuple, where (k, v1) is in `this` and
      * (k, v2) is in `other`. Uses the given Partitioner to partition the output RDD.
      */
    for (i <- 1 to 10) {
      val contribs = links.join(ranks).values.flatMap{ case (urls, rank) =>
        val size = urls.size
        urls.map(url => (url, rank / size))
      }
      ranks = contribs.reduceByKey(_ + _).mapValues(0.15 + 0.85 * _)//
      // .cache()
    }

    val output = ranks.collect()
    output.foreach(tup => println(tup._1 + " has rank: " + tup._2 + "."))

    ctx.stop()
  }
}

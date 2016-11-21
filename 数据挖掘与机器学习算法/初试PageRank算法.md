   <pre>PageRank
众所周知，Google浏览器之所以在很久以前，就闻名世界的一个重要原因，就是它的用来体现网页的相关性和重要性，
在搜索引擎优化操作中，用来评估网页优化的成效因素之一的PageRank算法，很早就已经了解过该算法，现在结合Spark，
来真正的解决实际中案例。
首先，必须得从纯粹的数学理论方面来了解一下PageRank的核心计算，我从网上找了一篇前辈们写的很好的博客（[原博客链接](http://blog.csdn.net/ZCF1002797280/article/details/50254069) ），
来解释PageRank算法中的数学思想：
![](https://github.com/woshidandan/hadoop-spark/blob/master/picture/pagerank2.png)
![](https://github.com/woshidandan/hadoop-spark/blob/master/picture/pagerank3.png)
![](https://github.com/woshidandan/hadoop-spark/blob/master/picture/pagerank4.png)
![](https://github.com/woshidandan/hadoop-spark/blob/master/picture/pagerank5.png)
![](https://github.com/woshidandan/hadoop-spark/blob/master/picture/pagerank6.png)
</pre>
相信看完了这些，你肯定对这其中的数学方法，已经有一个清晰的了解了，但是我们的算法，是一个不同于数学的一门学科，
特别是结合了我们的工具Spark以后，会发生很大的变化，我会在后面给出分析，首先，不管怎么样，得看看算法的源码，
Spark的源码包目录下，即有该算法的源码：
```scala
import org.apache.spark.{SparkConf, SparkContext}
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
    val args  = "D:\\pagerankdata.txt";//为了方便，我们先跑在本地
    if (args.length < 1) {
      System.err.println("Usage: SparkPageRank <file> <iter>")
      System.exit(1)
    }
    val sparkConf = new SparkConf().setAppName("PageRank").setMaster("local");

    val ctx = new SparkContext(sparkConf)
    val lines = ctx.textFile(args, 1)

    //初始化迭代次数，迭代次数参考《数学之美》中提到：“一般来讲，只要10次左右的迭代基本上就收敛了”
    //这里的args(1).toInt值为58，至于为什么取args(1)中的值，目前没有发现其实质的意义。
    val iters = if (args.length > 1) args(1).toInt else 10
    println("the args(1) is:"+iters);

    /*根据边关系数据生成 邻接表 如：(1,(2,3,4,5)) (2,(1,5))..
    注意此句中有个groupByKey方法，如果我们的数据是
    7 3
    7 6                   7 6 3
    6 7 这种格式，是和我们用6 7   是不同的，在这个案例中，后者无法得到正确的答案*/
    val links = lines.map{ s =>
      val parts = s.split("\\s+")
      (parts(0), parts(1))
    }.distinct().groupByKey()
    //.cache()

    //将每个页面的排序值初始化为1.0
    var ranks = links.mapValues(v => 1.0)

    /**join方法的解释
      * Return an RDD containing all pairs of elements with matching keys in `this` and `other`. Each
      * pair of elements will be returned as a (k, (v1, v2)) tuple, where (k, v1) is in `this` and
      * (k, v2) is in `other`. Uses the given Partitioner to partition the output RDD.
      */
    for (i <- 1 to 1) {
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
```
源码中有两个注意点：
（1）首先是我们的输入数据，这个是一个非常让我们值得注意的一点，相信大家如果去看网上那些杂七杂八的博客，
他们给出的算法案例




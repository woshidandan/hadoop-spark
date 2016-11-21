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
    for (i <- 1 to iters) {
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
<pre>
源码中有两个注意点：
（1）首先是我们的输入数据，这个是一个非常让我们值得注意的一点，相信大家如果去看网上那些杂七杂八的博客，
他们给出的算法案例中，大家应该能发现，他们给出的测试数据都是在程序中直接跳过groupByKey这个方法所在的程序段，
原因何在？因为他们一开始是想从文件中读取测试数据，然后发现，随着迭代次数的增加，怎么出现越来越多的数据
的（网页）rank值无法给出来，甚至有的完全没法得到最后的rank排名结果。如下图中所示：
迭代一次：
![](https://github.com/woshidandan/hadoop-spark/blob/master/picture/pagerank7.png)
迭代十次：
![](https://github.com/woshidandan/hadoop-spark/blob/master/picture/pagerank8.png)
此时我们已经完全无法得到结果了，而且没有任何的提示
那么这个是什么原因造成的呢，我们可以通过下面这个图来看一下：
![](https://github.com/woshidandan/hadoop-spark/blob/master/picture/pagerank9.png)
从这个图中，对比我们执行后的结果，可以发现，我们的A是没有rank值的，原因在于，它相当于一个叶子节点，虽然我们的前辈们
给出了上文博客中"5. 完整PageRank算法"里的解决终止点问题和陷阱问题办法（当时我被这种解决方法都快感动哭了，太佩服他们了），
但是这种方法也是有缺点的，比如在我们的这个测试案例中，我们的叶子节点A，通过这个方法，不断的将自己的影响力扩大到整个数据的
每一个点的rank值中，简单来说，就是本来如果迭代到A，它迭代后没有rank值，理所当然，因为它不指向任何网页，也就是rank值最终会为0，
但是它通过这个公式v′=αMv+(1−α)e，使得每次都削弱其他点的rank值，最终，随着迭代次数的增多，将会没有rank的输出结果。
当然，没有输出结果必须满足下面两个要求：
<strong>一是不存在一个强连通图
二是有像A这样的叶子节点 </strong>
如果满足了这两点，那么我们前辈们好不容易想出来的解决终止点问题和陷阱问题办法，随着迭代次数的增多，反而起到了相反的效果。
那么我们怎么改我们的测试数据呢？
很简单，我们只要破坏其中的一个要求就行，可以增加一条从A到E的链接，即可
![](https://github.com/woshidandan/hadoop-spark/blob/master/picture/pagerank10.png) 
即使我们迭代100次，我们也能得到正确的rank结果：
![](https://github.com/woshidandan/hadoop-spark/blob/master/picture/pagerank11.png) 
即使我们的数据中还存在一个叶子节点，我们增加的这个B->A->E->B连通图，也能彻底的消除它对整体的影响，转而把它的值置为0，也即不显示
所以，我们的测试数据并不是随意的。

（2）然后是我们源码中另一个值得注意的方法join方法，这个方法的主要目的是实现合并，我们的测试数据中，如果我们想表达这种意思，B链接向A，
C链接向A，然后我们在测试数据中写成 A B C，如果是这种写法，该算法得到的结果不是正确结果，因为它只识别A B，A C 这样的输入，而我们的join
方法，则帮助我们把A B，A C转化成A B C，也即((A,B)(A,C))转化为(A,(B,C))，这样的话，我们的程序才能知道urls.size得到的数据，即为2,也就
是A的两个分支，然后通过rank / size，来划分下一步的分支的权重。

最后，我们从Spark官网的github下，拿到一项官方的测试数据：
2 1
4 1
1 2
6 3
7 3
7 6
6 7
3 7
![](https://github.com/woshidandan/hadoop-spark/blob/master/picture/pagerank1.png) 
当然，你也可以自己来按照测试数据的原则，自己给出，迭代十次，得出我们的rank结果:
7 has rank: 1.298188273285468.
6 has rank: 0.7018117267145316.
2 has rank: 1.196874404340722.
3 has rank: 0.9999999999999998.
1 has rank: 1.0.
至于这里面为什么没有2，相信大家也能想的明白了。</pre>
                                 2016/11/21
   
                          








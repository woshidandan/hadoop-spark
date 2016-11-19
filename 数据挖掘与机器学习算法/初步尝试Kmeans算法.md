   经典聚类算法K-Means
俗话说的好， 物以类聚，人以群分，我们的数据也应如此，作为我的第一个真正进入到，
spark与数据挖掘和机器学习结合的领域的算法，很高兴，它没有让我失望，那一行行浓缩的代码，
无不是历代程序员心血的结晶。
K-means算法是最为经典的基于划分的聚类方法，是十大经典数据挖掘算法之一。K-means算法的

基本思想：
以空间中k个点为中心进行聚类，对最靠近他们的对象归类。通过迭代的方法，逐次更新各聚类中心的值，
直至得到最好的聚类结果。该算法接受参数 k ；然后将事先输入的n个数据对象划分为 k个聚类以便使得
所获得的聚类满足：同一聚类中的对象相似度较高；而不同聚类中的对象相似度较小。聚类相似度是利用
各聚类中对象的均值所获得一个“中心对象”（引力中心）来进行计算的。

 

算法描述:
假设要把样本集分为c个类别，算法描述如下：
（1）适当选择c个类的初始中心；
（2）在第k次迭代中，对任意一个样本，求其到c个中心的距离，将该样本归到距离最短的中心所在的类；
（3）利用均值等方法更新该类的中心值；
（4）对于所有的c个聚类中心，如果利用（2）（3）的迭代法更新后，值保持不变（经过实际的实践，值的不变
与所选的数据和迭代的次数有很大关系），则迭代结束，否则继续迭代。</br>
![](https://github.com/woshidandan/hadoop-spark/blob/master/picture/kmeans1.jpg)</br>
该算法的最大优势在于简洁和快速。算法的关键在于<strong>初始中心的选择和距离公式</strong>。

俗话说的好，实践是检验真理的唯一标准，我们先来跑一下这个算法，看看它的厉害之处。
 /* 测试数据如下：
  *0.0 0.0 0.0
  *0.1 0.1 0.1
  *0.2 0.2 0.2
  *9.0 9.0 9.0
  *9.1 9.1 9.1
  *9.2 9.2 9.2
  */
  这是一个6X3的矩阵，我们希望这个算法，能给出我们一个聚类的结果，使这6行数据以两个聚类中心，划分为两类
  算法具体代码如下：
  ```scala
import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.{SparkConf, SparkContext}

  * 执行方式：./spark-submit --master=spark://cloud25:7077 --class com.eric.spark.mllib.KMeansSample
  * --executor-memory=2g /opt/cloud/spark-1.4.1-bin-hadoop2.6/lib/spark_scala.jar
  * Created by xiaohe on 2015/11/12.本例中我采取本地运行。

  * 测试数据如下：
  *0.0 0.0 0.0
  *0.1 0.1 0.1
  *0.2 0.2 0.2
  *9.0 9.0 9.0
  *9.1 9.1 9.1
  *9.2 9.2 9.2
  */
object KMeansSample {
  def main(args: Array[String]) {

    //设置环境
    val sparkConconf = new SparkConf().setAppName("KMeansSample").setMaster("local");//本地跑，测试
    val sparkContext = new SparkContext(sparkConconf)

    //装载数据
    val fileData = sparkContext.textFile("D:\\kmeansdata.txt", 1)
    //创建一个稠密向量,将fileData中的数据，转化为一个double型向量，用空格分割
    val parseData = fileData.map(record => Vectors.dense(record.split(" ").map(_.toDouble)))

    //模型训练,将数据集聚类，2个类，20次迭代，进行模型训练形成数据模型
    val dataModelNumber = 2;
    val dataModelTrainTimes = 20
    val model = KMeans.train(parseData, dataModelNumber, dataModelTrainTimes)

    println("Cluster centers:")
    for (c <- model.clusterCenters) {
      println("  " + c.toString)
    }
    //运行结果
  /*
  Cluster centers:
  [9.1,9.1,9.1]
  [0.1,0.1,0.1]
    Vectors 0.2 0.2 0.2 is belongs to clusters:1
    Vectors 0.25 0.25 0.25 is belongs to clusters:1
    Vectors 8 8 8 is belongs to clusters:0
   */

    //使用模型测试单点数据
    println("Vectors 0.2 0.2 0.2 is belongs to clusters:" + model.predict(Vectors.dense("0.2 0.2 0.2"
    .split(' ').map(_.toDouble))))
    println("Vectors 0.25 0.25 0.25 is belongs to clusters:" + model.predict(Vectors.dense("0.25 0.25 0.25"
    .split(' ').map(_.toDouble))))
    println("Vectors 8 8 8 is belongs to clusters:" + model.predict(Vectors.dense("8 8 8".split(' ')
    .map(_.toDouble))))

    // 使用误差平方之和来评估数据模型
    val cost = model.computeCost(parseData)
    println("Within Set Sum of Squared Errors = " + cost)
   //结果：Within Set Sum of Squared Errors = 0.11999999999994547

    //交叉评估1，只返回结果
    val testdata = fileData.map(s => Vectors.dense(s.split(' ').map(_.toDouble)))
    //model.predict:Return the cluster index that a given point belongs to||
    //Maps given points to their cluster indices(聚类指标)
    val result1 = model.predict(testdata)
    result1.foreach(println)
    println("-----------------------")
    // result1.saveAsTextFile("/data/mllib/result1")
    /*
    结果如下：
    1
    1
    1
    0
    0
    0
     */
    //交叉评估2，返回数据集和结果
    val result2 = fileData.map {
      line =>
        val linevectore = Vectors.dense(line.split(' ').map(_.toDouble))
        val prediction = model.predict(linevectore)
        line + " " + prediction
    }
    //.saveAsTextFile("/data/mllib/result2")
    result2.foreach(println)
    sparkContext.stop()
    /*
    结果如下：
    0.0 0.0 0.0 1
    0.1 0.1 0.1 1
    0.2 0.2 0.2 1
    9.0 9.0 9.0 0
    9.1 9.1 9.1 0
    9.2 9.2 9.2 0
     */

  }
}
```
通过运行的结果，我们很容易看出来，已经实现了当初的想法，其中第1、2、3行为一类，4、5、6行为一类。

进一步研究我们的算法可以发现，整个算法的精髓在于这段代码：
val model = KMeans.train(parseData, dataModelNumber, dataModelTrainTimes)
我们通过代码的调试以及源码的查看，发现程序的主体是在进入KMeans.scala中开始执行主要代码。

进入KMeans中，这个类即为整个算法的核心所在。

正如我们上文中所说的那样，核心是初始中心的选择和距离公式，那么这个算法的核心即为初始中心的选择和距离公式，
观察整个KMeans.scala的代码中，我们可以找到下面的这个方法，也即是初始中心的选择和确定方法：</br>
![](https://github.com/woshidandan/hadoop-spark/blob/master/picture/kmeans2.png)</br>
观察这个方法，我们发现初始中心的选择是随机给出几个点，然后不断的调整优化，找到一个近似最优聚类，看了很久，
表示香菇难受，源码写的太精炼了，以我现在的水平无法参悟全部内容。

之后就是距离公式，我们找到这个实现的方法：</br>
![](https://github.com/woshidandan/hadoop-spark/blob/master/picture/kmeans3.png)</br>
```scala
//findClosest方法：找到点与所有聚类中心最近的一个中心
  private[mllib] def findClosest(
      centers: TraversableOnce[VectorWithNorm],
      point: VectorWithNorm): (Int, Double) = {
    var bestDistance = Double.PositiveInfinity   //初始化最优距离为正无穷
    var bestIndex = 0    //最佳指标
    var i = 0
    centers.foreach { center =>
      // Since `\|a - b\| \geq |\|a\| - \|b\||`, we can use this lower bound to avoid unnecessary
      // distance computation. |a - b|大于等于|a| - |b|
      var lowerBoundOfSqDist = center.norm - point.norm  //向量的长度
      lowerBoundOfSqDist = lowerBoundOfSqDist * lowerBoundOfSqDist  //中心点到数据点最大的距离的平方
      if (lowerBoundOfSqDist < bestDistance) { 
      //如果最小都大于最佳路径了，没必要算欧式距离了（欧式距离，二维和三维空间中的欧氏距离就是两点之间的实际距离）
        val distance: Double = fastSquaredDistance(center, point)
        //计算欧式距离，看了fastSquaredDistance的源码，其中有个变量norm1=涉及到L2范数，因为知识有限，目前无法参透，
        //但是我们知道这个方法是返回真正的欧式距离，对本身算法的理解没有阻碍
        if (distance < bestDistance) {
          bestDistance = distance
          bestIndex = i
        }
      }
      i += 1
    }
    (bestIndex, bestDistance)
  }
```
至于里面的findClosest的数学逻辑，贴上下面这个图片，就很容易理解了</br>
![](https://github.com/woshidandan/hadoop-spark/blob/master/picture/kmeans4.png)</br>













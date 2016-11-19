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
<pre>
 /* 测试数据如下：
  *0.0 0.0 0.0
  *0.1 0.1 0.1
  *0.2 0.2 0.2
  *9.0 9.0 9.0
  *9.1 9.1 9.1
  *9.2 9.2 9.2
  */
  </pre>
  这是一个6X3的矩阵，我们希望这个算法，能给出我们一个聚类的结果，使这6行数据以两个聚类中心，划分为两类
  算法具体代码如下：
  ```scala
import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.{SparkConf, SparkContext}

  * 执行方式：./spark-submit --master=spark://cloud25:7077 --class com.eric.spark.mllib.KMeansSample
  * --executor-memory=2g /opt/cloud/spark-1.4.1-bin-hadoop2.6/lib/spark_scala.jar
  * Created by 2281444815 on 2015/11/12.本例中我采取本地运行。

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

正如我们上文中所说的那样，核心是模型训练中初始中心的选择和距离公式，那么这个算法的核心即为初始中心的选择和距离公式，
观察整个KMeans.scala的代码中，我们可以找到下面的这个方法，也即是初始中心的选择和确定方法：</br>
![](https://github.com/woshidandan/hadoop-spark/blob/master/picture/kmeans2.png)</br>
观察这个方法，我们发现初始中心的选择是随机给出几个点，然后不断的调整优化，找到一个近似最优聚类，看了很久，
表示香菇难受，源码写的太精炼了，以我现在的水平无法参悟全部内容。所以我觉得，scala语言，是一个简单的又复杂的
语言，表面上各种方法和api的调用都非常方面，但如果真正深入进去，是很难参悟的。

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
      //从这里我们也能看出各位前辈们为了优化算法，从一点一滴来降低复杂度，呕心沥血
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

接下来，我们再分析一个实际的案例</br>
![](https://github.com/woshidandan/hadoop-spark/blob/master/picture/kmeans5.png)</br>
对于世界杯，进入决赛圈则取其最终排名，没有进入决赛圈的，打入预选赛十强赛赋予40，预选赛小组未出线的赋予50。
对于亚洲杯，前四名取其排名，八强赋予5，十六强赋予9，预选赛没出现的赋予17。这样做是为了使得所有数据变为标量，便于后续聚类。
这是网上的一个真实数据，在这个案例中，我们想通过聚类，来将这些国家的足球数据排个档次，当然，作为检测
我们聚类结果的最强真理--中国的足球，如果它不是在排名最差的那一类中，显然我们的算法是没用意义的。

拿到数据后，显然我们不能直接的就开始进行模型的训练，先得对数据进行归一化处理，那样才有可比较的依据。
本想用scala进行数据的加工处理，无奈scala从文件中读取文本内容并转化为Int数组的算法无法实现，网上也
没有相关的Idea，只能放弃，采用matlab来对我们的数据进行处理。
```scala
%对行进行数据归一化处理
x0=[50 50 9;28 9 4;17 15 3;25 40 5;28 40 2;50 50 1;50 40 9;50 40 9;40 40 5;50 50 9;50 50 5;
50 50 9;40 40 9;40 32 17;50 50 9];
n=size(x0,1);
x1=[];
for i=1:n
xx1=x0(i,:)./x0(1,:);
x1(i,:)=xx1;
end
x1
```
最后得到我们的归一化矩阵（这里如果采用[0,1]归一化会更好）:
<pre>
1.0000 1.0000 1.0000
0.5600 0.1800 0.4444
0.3400 0.3000 0.3333
0.5000 0.8000 0.5556
0.5600 0.8000 0.2222
1.0000 1.0000 0.1111
1.0000 0.8000 1.0000
1.0000 0.8000 1.0000
0.8000 0.8000 0.5556
1.0000 1.0000 1.0000
1.0000 1.0000 0.5556
1.0000 1.0000 1.0000
0.8000 0.8000 1.0000
0.8000 0.6400 1.0000
1.0000 1.0000 1.0000
</pre>
具体算法的实现代码如下：
```scala
import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.{SparkConf, SparkContext}
/**
  * Created by 2281444815 on 2016/11/19.
  */
object BallKmeans {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("BallKmeans").setMaster("local");
    val sc = new SparkContext(conf);
    val fileData = sc.textFile("D:\\bolldata.txt");
    //原数据中分隔符用"/t"会报错NumberFormatException: empty String，相当于把null=>double
    val parseData = fileData.map(record => Vectors.dense(record.split(" ").map(_.toDouble)));

    val dataModelNumber = 3;
    val dataModelTrainTimes = 2000;
    val model = KMeans.train(parseData, dataModelNumber, dataModelTrainTimes);
    //    println("Cluster centers:")
    //    for (c <- model.clusterCenters){
    //      println(" " + c.toString);
    //    }
    
    //计算误差
    val cost = model.computeCost(parseData)
    println("Within Set Sum of Squared Errors = " + cost)
    
   //打印结果
    val result = fileData.map {
      line =>
        val linevectore = Vectors.dense(line.split(" ").map(_.toDouble));
        val prediction = model.predict(linevectore);
        line + " " + prediction;
    }
    result.foreach(println)
    sc.stop();
  }
}
```
k-means作为无监督学习算法中的一种，我们进行模型训练时，中心点的数量以及随机的中心点的选择，对我们的结果，
会产生直接影响，另外，该算法对部分异常数据及其敏感。
在我们的这个案例中，朝鲜2007年亚洲杯的数据，会造成最后结果偏差过大，我们将进行特殊赋值处理；
另外，我们的中心点数量的选择，即dataModelNumber的取值，也会直接影响到最后结果，经过我多次的对
模型的训练调整各个簇的中心点，发现在进行2000次迭代之后，会近似出现最小误差:</br>
Within Set Sum of Squared Errors = 0.691361732999999
但是以同样的次数迭代时，会出现另一个误差(还有几个几率小的误差)：</br>
Within Set Sum of Squared Errors = 0.9448397610000014
这是什么原因造成的呢，考虑之后，我认为是与中心点的随机有关选取。

在最小误差时，我们得到聚类的结果如下：
1.0000 1.0000 1.0000 2
0.5600 0.1800 0.4444 1
0.3400 0.3000 0.3333 1
0.5000 0.8000 0.5556 0
0.5600 0.8000 0.2222 0
1.0000 1.0000 0.1111 0
1.0000 0.8000 1.0000 2
1.0000 0.8000 1.0000 2
0.8000 0.8000 0.5556 0
1.0000 1.0000 1.0000 2
1.0000 1.0000 0.5556 0
1.0000 1.0000 1.0000 2
0.8000 0.8000 1.0000 2
0.8000 0.6400 1.0000 2
1.0000 1.0000 1.0000 2

观察数据，我们可以对比原来的战绩表，果然中国没有让我们失望，成功的进入的最差成绩的一类，再对比其他数据，我们
可以初步得出我们的聚类结果是可信的</br>
![](https://github.com/woshidandan/hadoop-spark/blob/master/picture/kmeans6.png)</br>
其中足球水平一流的国家为：日本、韩国
水平二流的为：伊朗、沙特、伊拉克、乌兹别克斯坦、越南
水平三流的为：中国、卡塔尔、阿联酋、泰国、阿曼、巴林、朝鲜、印尼








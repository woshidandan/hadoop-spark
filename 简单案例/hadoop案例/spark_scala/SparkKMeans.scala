import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.{SparkConf, SparkContext}

/**
  * 该程序主要通过kmeans算法对数据进行分类
  * K-means算法是最为经典的基于划分的聚类方法，是十大经典数据挖掘算法之一。
  * K-means算法的基本思想是：以空间中k个点为中心进行聚类，对最靠近他们的对象归类。
  * 通过迭代的方法，逐次更新各聚类中心的值，直至得到最好的聚类结果。该算法接受参数 k ；
  * 然后将事先输入的n个数据对象划分为 k个聚类以便使得所获得的聚类满足：同一聚类中的对象相似度较高；
  * 而不同聚类中的对象相似度较小。聚类相似度是利用各聚类中对象的均值所获得一个“中心对象”（引力中心）来进行计算的。
算法描述:
假设要把样本集分为c个类别，算法描述如下：
（1）适当选择c个类的初始中心；
（2）在第k次迭代中，对任意一个样本，求其到c个中心的距离，将该样本归到距离最短的中心所在的类；
（3）利用均值等方法更新该类的中心值；
（4）对于所有的c个聚类中心，如果利用（2）（3）的迭代法更新后，值保持不变，则迭代结束，否则继续迭代。
该算法的最大优势在于简洁和快速。算法的关键在于初始中心的选择和距离公式。

  * 执行方式：./spark-submit --master=spark://cloud25:7077 --class com.eric.spark.mllib.KMeansSample
  * --executor-memory=2g /opt/cloud/spark-1.4.1-bin-hadoop2.6/lib/spark_scala.jar
  * Created by Eric on 2015/11/12.

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
    println("Vectors 0.2 0.2 0.2 is belongs to clusters:" + model.predict(Vectors.dense("0.2 0.2 0.2".split(' ').map(_.toDouble))))
    println("Vectors 0.25 0.25 0.25 is belongs to clusters:" + model.predict(Vectors.dense("0.25 0.25 0.25".split(' ').map(_.toDouble))))
    println("Vectors 8 8 8 is belongs to clusters:" + model.predict(Vectors.dense("8 8 8".split(' ').map(_.toDouble))))

    // 使用误差平方之和来评估数据模型
    val cost = model.computeCost(parseData)
    println("Within Set Sum of Squared Errors = " + cost)
   //结果：Within Set Sum of Squared Errors = 0.11999999999994547

    //交叉评估1，只返回结果
    val testdata = fileData.map(s => Vectors.dense(s.split(' ').map(_.toDouble)))
    //model.predict:Return the cluster index that a given point belongs to||Maps given points to their cluster indices(聚类指标)
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
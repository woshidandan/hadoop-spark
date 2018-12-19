import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by 2281444815 on 2018/4/18.
  */
object irisDecisionTree {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setMaster("local").setAppName("irisdecisiontree")
    val sc = new SparkContext(conf)
    val data = MLUtils.loadLibSVMFile(sc,"d://data/decisiontree.txt")
    val numClasses = 4
    val categoricalFeaturesInfo = Map[Int,Int]()
    val impurity = "entropy"
    val maxDepth = 5
    val maxBins = 3
    val model = DecisionTree.trainClassifier(data,numClasses,categoricalFeaturesInfo,impurity,maxDepth,maxBins)
    val test = Vectors.dense(Array(7.7,2.6,6.9,2.3))
    val result = model.predict(test)
    println("该组数据的预测结果为："+ result)
  }
}

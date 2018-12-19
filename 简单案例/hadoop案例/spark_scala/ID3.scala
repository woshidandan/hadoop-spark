//import org.apache.spark.mllib.linalg.Vectors
//import org.apache.spark.mllib.tree.DecisionTree
//import org.apache.spark.mllib.util.MLUtils
//import org.apache.spark.{SparkConf, SparkContext}
//
///**
//  * Created by 2281444815 on 2017/2/12.
//  */
//object ID3 {
//  def main(args: Array[String]): Unit = {
//    val conf = new SparkConf().setAppName("ID3").setMaster("local");
//    val sc = new SparkContext(conf);
//    val data = MLUtils.loadLibSVMFile(sc, "D://data//jueceshu.txt");
//    //这里的数据形式我们在之前也已提到过，不再说明，也可不采用这种方式
//    /** 数据说明
//      * checkpointInterval:
//      * 类型：整数型。
//      * 含义：设置检查点间隔（>=1），或不设置检查点（-1）。
//      * featuresCol:
//      * 类型：字符串型。
//      * 含义：特征列名。
//      * impurity:
//      * 类型：字符串型。
//      * 含义：计算信息增益的准则（不区分大小写）。
//      * labelCol:
//      * 类型：字符串型。
//      * 含义：标签列名。
//      * maxBins:
//      * 类型：整数型。
//      * 含义：连续特征离散化的最大数量，以及选择每个节点分裂特征的方式。
//      * maxDepth:
//      * 类型：整数型。
//      * 含义：树的最大深度（>=0）。
//      *
//      * Method to train a decision tree model for binary or multiclass classification.
//      *
//      * input Training dataset: RDD of [[org.apache.spark.mllib.regression.LabeledPoint]].
//      * Labels should take values {0, 1, ..., numClasses-1}.
//      * numClasses number of classes for classification.分类的数量
//      * categoricalFeaturesInfo Map storing arity of categorical features.
//      *                                E.g., an entry (n -> k) indicates that feature n is categorical
//      * with k categories indexed from 0: {0, 1, ..., k-1}.信息的输入格式
//      * impurity Criterion used for information gain calculation.
//      * Supported values: "gini" (recommended) or "entropy".设定信息的增益计算方式
//      * maxDepth Maximum depth of the tree.
//      *                 E.g., depth 0 means 1 leaf node; depth 1 means 1 internal node + 2 leaf nodes.
//      * (suggested value: 5)树的最大高度
//      * maxBins maximum number of bins used for splitting features
//      * (suggested value: 32) 能够分裂的数据集合数量
//      * DecisionTreeModel that can be used for prediction
//      */
//    val numClasses = 2 //这里的案例账号只有真或不真，分为两类
//    val categoricalFeaturesInfo = Map[Int, Int]()
//    val impurity = "entropy"
//    val maxDepth = 5
//    val maxBins = 3
//    val model = irisDecisionTree.trainClassifier(data, numClasses, categoricalFeaturesInfo,
//      impurity, maxDepth, maxBins);
//    println(model.topNode);
//    val patient = Vectors.dense(Array(1.0, 1.0, 1.0));
//    //对该组数据进行预测，注意第数据必须为double型，否则报错
//    val result = model.predict(patient);
//    println("结果是：" + result);
//  }
//}

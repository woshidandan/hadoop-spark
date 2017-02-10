  SVM，Support Vector Machine，支持向量机，也是我目前遇到的看起来最复杂，理解起来也最复杂的方法了，作为
一种监督式学习方法，支持向量机是一种在90年代中期发展起来的基于统计学习理论的一种机器学习方法，通过寻求结
构化风险最小来提高学习机泛化能力，实现经验风险和置信范围的最小化，从而达到在统计样本量较少的情况下，亦能
获得良好统计规律的目的。

以下是SVM中的一些关键概念的解读，希望可以对大家在理解SVM起到帮助作用。

首先，涉及到SVM必然涉及到函数间隔和几何间隔，并且最大间隔分割器即为SVM的前身，可想而知，理解它们的重要性。

其中，这么一段话，对我们理解为什么要计算间隔，起到至关重要的作用：
支持向量机的目的是划分最优的超平面从而使不同的类别分开，划分最优的超平面涉及到间隔。

对一个数据点进行分类，当它的间隔越大的时候，分类正确的把握越大。对于一个包含 n 个点的数据集，我们可以很自
然地定义它的间隔为所有这 n 个点的间隔中最小的那个。于是，为了使得分类的把握尽量大，我们希望所选择的超平面能
够最大化这个间隔值。

通过最大化间隔，我们使得该分类器对数据进行分类时具有了最大的把握。但，这个最大分类间隔器到底是用来干嘛的呢？
很简单，支持向量机通过使用最大分类间隔来设计决策最优分类超平面，而为何是最大间隔，却不是最小间隔呢？因为最大
间隔能获得最大稳定性与区分的确信度，从而得到良好的推广能力（超平面之间的距离越大，分离器的推广能力越好，也就
是预测精度越高，不过对于训练数据的误差不一定是最小的） 。

其中的超平面，通过我们的设置函数w T x + b=0来得到。

继而问题转化为求出其中的w和b的值，经过一系列推导，最后几何间隔的大小简化为对下面这个公式中参数w求值（函数间隔
设为1，分子可以置为1，不影响）：

![](https://github.com/woshidandan/hadoop-spark/blob/master/picture/svm10.jpg)

在这个过程中，我们涉及到更多复杂的问题：原始问题和对偶问题，对偶变量的优化问题，拉格朗日算子，最后得到下面这个公式：

![](https://github.com/woshidandan/hadoop-spark/blob/master/picture/svm11.jpg)

我们将减号的左右分为两部分来解释，左边部分相当于我们要求的间隔，右边部分相当于函数间隔距离1的大小，我们可以将其视为“误差”

求偏导左右等于0得到w和b的约束条件。通过对偶问题求出w，a  带入原始优化问题中求b。
总的来说，就是求公式中w和b的大小，从而确定公式，得到分类面，和线性回归求极值的方法类似。
草草的说过，还有更多更深层次的内容，留给后续的学习。

![](https://github.com/woshidandan/hadoop-spark/blob/master/picture/biaoqin.jpg)

为了让大家，也为了自己今后继续学习的方面，我特地从网络上摘下一位前辈对SVM较好的总结：

![](https://github.com/woshidandan/hadoop-spark/blob/master/picture/svm1.jpg)
![](https://github.com/woshidandan/hadoop-spark/blob/master/picture/svm2.jpg)
![](https://github.com/woshidandan/hadoop-spark/blob/master/picture/svm3.jpg)
![](https://github.com/woshidandan/hadoop-spark/blob/master/picture/svm4.jpg)
![](https://github.com/woshidandan/hadoop-spark/blob/master/picture/svm5.jpg)
![](https://github.com/woshidandan/hadoop-spark/blob/master/picture/svm6.jpg)
![](https://github.com/woshidandan/hadoop-spark/blob/master/picture/svm7.jpg)
![](https://github.com/woshidandan/hadoop-spark/blob/master/picture/svm8.jpg)
![](https://github.com/woshidandan/hadoop-spark/blob/master/picture/svm9.jpg)

下面我们用一个spark案例来说明SVM的具体应用，该案例用于判断胃癌细胞的转移：

![](https://github.com/woshidandan/hadoop-spark/blob/master/picture/svmdata.jpg)

```scala
import org.apache.spark.mllib.classification.SVMWithSGD
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by 2281444815 on 2017/2/9.
  */
object SVMDemo {
  val conf = new SparkConf().setAppName("SVM").setMaster("local");
  val sc = new SparkContext(conf);

  def main(args: Array[String]): Unit = {

    val data = sc.textFile("D:\\data\\svmdata.txt"); 
//注意不能少数据，否则val model = SVMWithSGD.train(parsedData, 50);报错

    val splits = data.randomSplit(Array(0.7, 0.3));
    val parsedData = splits(0).map { line =>
      val parts = line.split(",");
      LabeledPoint(parts(0).toDouble, Vectors.dense(parts(1).split(" ").map(_.toDouble)));
    }.cache();
    val parsedTest = splits(1).map { line =>
      val parts1 = line.split(",");
      LabeledPoint(parts1(0).toDouble, Vectors.dense(parts1(1).split(" ").map(_.toDouble)));
    }.cache();
    /*我们的数据也可以采取下面这种系统默认提供的一种录入数据的方式：
      val data = MLUtils.loadLibSVMFile(sc,"D://data//svmdata.txt");
        val predictionLabel = parsedTest.map { point =>{
          val prediction = model.predict(point.features)
          (point.label,prediction)
        }
        }
        这种方式下，我们的数据格式要变为：
        0 1:59 2:0 3:43.4 4:2 5:1
        具体数据格式我们可以参考spark源码目录下data文件中：sample_libsvm_data.txt文件的格式
        因为我怕这种方式的数据比较麻烦，所以换了一种。
     */

    val model = SVMWithSGD.train(parsedData, 50);
    val predictionLabel = parsedTest.map {
      case LabeledPoint(label, features) =>
        val prediction = model.predict(features)
        (prediction, label)
    }


    val metrics = new MulticlassMetrics(predictionLabel);
    val precision = metrics.precision;
    println("验证值为：" + precision);
    //评估我们模型的准确度，越接近1，则越好

    val patient = Vectors.dense(Array(50, 1, 74.0, 1, 1));
    //对该组数据进行预测，注意第三个数据必须为double型，否则报错
    val result = model.predict(patient);
    if (result == 1)
      println("患者的胃癌有几率转移");
    else
      println("患者的胃癌不会转移");
    //    验证值为：0.18181818181818182
//    患者的胃癌有几率转移
//    1.0

  }
}
```
至此，我们对于SVM支持向量机的初期学习就到这里结束了，当然，它还有很多很多衍生的，更深层次的内容需要我们去学习，
作为一个魅力很大的算法，必然需要我们掌握更多的东西来研读它，我期待那天的到来！

如果你觉得我的文章对你有帮助，欢迎到我的博客留言区留言交流，我会虚心听取大家的意见和建议，为进一
步的学习做调整。更多的算法解析，我也会根据自己的学习在我的博客发布，欢迎来访www.xiaohegithub.cn
    
    2017/2/9

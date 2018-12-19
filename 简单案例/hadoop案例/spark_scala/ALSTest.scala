
import org.apache.spark.mllib.recommendation.{ALS, Rating}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by 2281444815 on 2016/12/2.
  */
object ALSTest {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("ALSTest");
    val sc = new SparkContext(conf);
    val data = sc.textFile("D:\\data\\alstest.txt");
    val ratings = data.map(_.split(" ") match {
      case Array(user,item,rate) =>    //转化数据集
        Rating(user.toInt,item.toInt,rate.toDouble)  //将数据转化为专用Rating
    })
    val rank = 4;//秩，这部分会在之后解释
    val numIterations = 4;
    val model = ALS.train(ratings,rank,numIterations,0.01); //进行模型训练，0.01为正则化参数，防止过拟合

    //为用户1推荐一个物品
    val rs = model.recommendProducts(1,1);
    rs.foreach(println)

   //预测结果
    //从 ratings 中获得只包含用户和商品的数据集
    val usersProducts = ratings.map{
      case Rating(user,product,rate)=>
        (user,product)
    }

    /**使用推荐模型对用户商品进行预测评分，得到预测评分的数据集
      * 这里面的predict方法，输入： 获得一组已知的用户和物品的数据对，返回: 数据对和预测的评分组成的对应关系
      * Predict the rating of many users for many products.
      * The output RDD has an element per each element in the input RDD (including all duplicates)
      * unless a user or product is missing in the training set.
      *
      * @param usersProducts  RDD of (user, product) pairs.
      * @return RDD of Ratings.
      */
    val predictions = model.predict(usersProducts).map{
      case Rating(user,product,rate) =>
        ((user,product),rate)
    }

    /**join方法，将真实评分数据集与预测评分数据集进行合并
      * 即将原本数据中用户物品数据对与评分组成的对应关系，加入到预测的对应关系中，相当于最终的数据集中
      * 用两个部分，一个是真实的用户物品与评分数据，一个是预测的用户物品与评分数据
      * Return an RDD containing all pairs of elements with matching keys in `this` and `other`. Each
      * pair of elements will be returned as a (k, (v1, v2)) tuple, where (k, v1) is in `this` and
      * (k, v2) is in `other`. Uses the given Partitioner to partition the output RDD.
      */
    val ratesAndPres = ratings.map{
      case Rating(user,product,rate)=>
        ((user,product),rate)
    }.join(predictions)

   //计算均方差
    val MSE = ratesAndPres.map{
      case ((user,product),(r1,r2))=>
        val err = (r1-r2)
        err*err
    }.mean()
    println("Mean Squared Error = "+MSE)

//    //5.保存模型
//    val ModelPath = "/user/model/ALS_Model"
//    model.save(sc,ModelPath)
//    val sameModel = MatrixFactorizationModel.load(sc,ModelPath)
    /**
      * Rating(1,14,0.9243941809475156)
Mean Squared Error = 0.30299670467583506

      Rating(1,15,1.1868446679098792)
Mean Squared Error = 0.19951470222129394

      Rating(1,14,0.915524562685281)
Mean Squared Error = 0.2885260944460462

      */
  }
}

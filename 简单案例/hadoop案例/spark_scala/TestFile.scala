import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by 2281444815 on 2016/11/25.
  */
object TestFile {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("testFile")
    val sc = new SparkContext(conf);
    val data = sc.textFile("D:\\data\\testfile.txt")
    val array = Array.ofDim[Double](3,6)
    for(i<-0 to 2) {
      for(j<-0 to 5 ) {
        val exam = data.map({ line =>
          val parts = line.split(" ")
          Vectors.dense(parts(i).split(' ').map(_.toDouble))//Vectors.dense创建稠密向量
        }).cache()
        array(i)(j) = exam.collect()(j).toArray(0)
      }
    }
    for(i<-0 to 2) {
      for(j<-0 to 5 ) {
        println(array(i)(j))
      }
    }

  }

}

// val parts = data.map( line =>line.split(" "))
//    val examples =data.map(_.split(' ').map(_.toDouble)).map(line => Vectors.dense(line)).cache();

//    val examples = data.map({ line =>{
//
//  val parts = line.split(" ")
//  Vectors.dense(parts(1).split(" ").map(_.toDouble))
//}
//
//}).cache()
////    examples.foreach(println)
//    val rm = new RowMatrix(rdd)
//    println(rm.numRows())
//    println(rm.numCols())
import java.io.FileReader
import java.io.File
import java.io.FileWriter

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.io.Source
import scala.util.Random

/**
  * Created by 2281444815 on 2016/11/19.
  */
object FormatData {
  def main(args: Array[String]): Unit = {
    val args = "D:\\bolldata.txt";
    if (args.length < 1) {
      println("Please some useful words");
      System.exit(1)
    }
    val conf = new SparkConf().setAppName("Key World Count").setMaster("local");
    val sc = new SparkContext(conf);
    val textFile = sc.textFile(args, 1);
    val wordCounts = textFile.flatMap(line => line.split(" "))


//
//     val read =Source.fromFile("D:\\bolldata.txt").getLines();//getline通过换行符来读取，并以原本数据格式输出
//    //read.foreach {println _ }
//
//
//    val a = read.toArray.filter()
//
//println(a(1))
//
  //  a.map(_.toDouble)
//    a.foreach{
//      println _
//    }
 }

}

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by 2281444815 on 2016/11/9.
  */
object AverageCount {
  def main(args: Array[String]): Unit = {
    if(args.length < 1){
      println("Usage:AvgAgeCalculator datafile")
      System.exit(1)
    }
    val conf  = new SparkConf().setAppName("SparkAverageCount");
    val sc = new SparkContext(conf);
    val dataFile = sc.textFile(args(0),5);
    val count = dataFile.count();
    /*
flatMap与split截取字符串
val str = "1,122,xxx,shandongyin"
val file=sc.textFile(logFile)
file.flatMap(line=>line.split(",")(3))
上面代码本意是根据","分隔，取”3“位置上的字符，即”shandongyin"，但是实际取到的值是's'
很郁闷吧。。。仔细一想与flatMap有关，”扁平化“有关
解决方法：
file.map(line=>line.split(",")(3))
   */
    val ageData = dataFile.map(line => line.split(" ")(1));//val ageData = dataFile.map(line => line.split(" ")(1))
    val totalAge = ageData.map(age => Integer.parseInt(
    String.valueOf(age))).collect().reduce((a,b) => a+b);
    println("Total Age:" + totalAge + ";Number of People:"+count);
    val avgAge :Double = totalAge.toDouble / count.toDouble;
    println("Average Age is" + avgAge)
  }

}

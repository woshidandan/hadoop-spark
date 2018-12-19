import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by 2281444815 on 2016/11/15.
  */
object PeopleInfoCacular {


  def main(args: Array[String]): Unit = {
    val logFile  = "D:\\peopledata.txt";
    if (logFile .length < 1) {
      println("Usage:PeopleInfoCalculator datafile");
      System.exit(1)
    }
    val conf = new SparkConf().setAppName("People Info Calculator").setMaster("local");
    val sc = new SparkContext(conf);
    val dataFile = sc.textFile(logFile , 1);
    //过滤
    val maleData = dataFile.filter(line => line.contains("M"))
      .map(line => (line.split(" ")(1) + " " + line.split(" ")(2)));
    val femaleData = dataFile.filter(line => line.contains("F"))
      .map(line => (line.split(" ")(1) + " " + line.split(" ")(2)));
    //取得身高
    val maleHeightData = maleData.map(line => line.split(" ")(1).toInt);
    val femaleHeightData = femaleData.map(line => line.split(" ")(1).toInt);
    //排序
    val lowestMale = maleHeightData.sortBy(x => x, true).first();
    val lowestFeMale = femaleHeightData.sortBy(x => x, true).first();
    val highestMale = maleHeightData.sortBy(x => x, false).first();
    val highestFeMale = femaleHeightData.sortBy(x => x, false).first();

    println("Number of Male People:" + maleData.count());
    println("Number of Female People:" + femaleData.count());
    println("Lowest Male:" + lowestMale)
    println("Lowest Female:" + lowestFeMale)
    println("Highest Male:" + highestMale)
    println("Highest Female:" + highestFeMale)
  }
}

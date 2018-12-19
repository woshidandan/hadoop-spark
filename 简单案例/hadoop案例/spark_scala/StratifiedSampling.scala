import org.apache.spark.{SparkConf, SparkContext}
/**
  * 分层抽样
  * Created by 2281444815 on 2016/11/27.
  */
object StratifiedSampling {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("stratified sampling").setMaster("local");
    val sc = new SparkContext(conf);
    /**
      * 在MLlib中，使用Map作为分层抽样的数据标记，Map的构成是[key,value]格式，key作为数据组，
      * 而value作为数据标签进行处理.
      */
    val data = sc.textFile("D:\\data\\samplebykey.txt")
      .map(row =>{       //因为这里的数据已经是一行一行的，所以无需flatMap
        if(row.length == 3){
          (row,1)       //对数据加标签分类建立对应map
        }
        else{ (row,2)}
      }).map(each => (each._2, each._1)) //將[key,value]转化为[value,key]

    println("The data of test:")
    data.foreach(println)
    /**
      * 设置抽样格式，及在标签中选择，以及抽样比例
      *
      * 这里调用的toMap方法的原因是，(List((1, 0.6), (2, 0.8)))并不是Map[Int, Double]需要该方法转化。
      * toMap把由键/值对组成的容器转换为一个映射表（map）。如果该容器并不是以键/值对作为元素的，
      * 那么调用这个操作将会导致一个静态类型的错误。
      */
    val fractions: Map[Int, Double] = (List((1, 0.6), (2, 0.8))).toMap
    val approxSample = data.sampleByKey(withReplacement = false, fractions, 0) //计算抽样样本
    println("First:")
    approxSample.foreach(println)

    println("Second:")

    /**
      * 通过parallelize方法从集合中创建RDD，并给与标签
      * （1）、从集合中创建RDD；（2）、从外部存储创建RDD；（3）、从其他RDD创建。
      * Return a subset of this RDD sampled by key (via stratified sampling).
      *
      * Create a sample of this RDD using variable sampling rates for different keys as specified by
      * `fractions`, a key to sampling rate map, via simple random sampling with one pass over the
      * RDD, to produce a sample of size that's approximately equal to the sum of
      * math.ceil(numItems * samplingRate) over all key values.
      *
      *  withReplacement whether to sample with or without replacement
      *  fractions map of specific keys to sampling rates
      *  seed seed for the random number generator
      * @return RDD containing the sampled subset
      */
    val randRDD = sc.parallelize(List((7, "cat"), (6, "mouse"), (7, "cup"), (6, "book"), (7, "tv"), (6, "screen"), (7, "heater")))
    val sampleMap = List((7, 1.0), (6, 0.8)).toMap
    val sample2 = randRDD.sampleByKey(false, sampleMap, 0).collect
    sample2.foreach(println)

    println("Third:")
    /*
     withReplacement 为 true 的时候 ，返回的子集会有重复 ， 为false ， 返回的子集不会有重复
     并且两者得到的子集大小( 去重的话 ) 都是 20 * 0.8 左右,withReplacement是指是否有放回的抽样
     为true为放回，为false为不放回
     */
    val a = sc.parallelize(1 to 20, 3)
    val b = a.sample(true, 0.8, 0)
    val c = a.sample(false, 0.8, 0)

    println("RDD a : " + a.collect().mkString(" , "))
    println("RDD b : " + b.collect().mkString(" , "))
    println("RDD c : " + c.collect().mkString(" , "))
    sc.stop
  }
}


//    val fractions:Map[String,Double] = Map("aa" -> 2)
//    val approxSample = data.sampleByKey(withReplacement = false,fractions,0)
//    approxSample.foreach(println)


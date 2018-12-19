/**
  * Created by 2281444815 on 2018/9/8.
  */
object Statistics {
  object Statistics {
    def main(args: Array[String]): Unit = {
      val data_path = "D://data//sparklern//2.2.1.txt"
      val data = sc.textFile(data_path).map(_.split("\t")).map(f => f.map(f.toDouble))

    }

  }
}

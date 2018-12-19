

import java.io.File
import java.io.FileWriter
import scala.util.Random

/**
  * Created by 2281444815 on 2016/11/9.
  */
object DataMake {
  def main(args: Array[String]) {
    val write = new FileWriter(new File("D:\\peopledata.txt"),false)
    val rand = new Random()
    for(i <- 1 to 1000){
      write.write(i + " " + rand.nextInt(100));
      write.write(System.getProperty("line.separator"))//平台独立换行符
    }
    write.flush()
    write.close()
  }

}

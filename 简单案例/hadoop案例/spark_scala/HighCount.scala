import java.io.{File, FileWriter}

import scala.util.Random

/**
  * Created by 2281444815 on 2016/11/14.
  */
object HighCount {
  def main(args: Array[String]): Unit = {
    val writer = new FileWriter(new File("D:\\peopledata.txt"), false);
    val rand = new Random()
    for (i <- 1 to 1000) {
      var height = rand.nextInt(220)
      if (height < 50) {
        height = height + 50;
      }
      var gender = getRandomGender;
      if (height < 100 && gender == "M")
        height = height + 100
      if (height < 100 && gender == "F")
        height = height + 50
      writer.write(i + " " + getRandomGender() + " " + height)
      writer.write(System.getProperty("line.separator"))
    }
    writer.flush()
    writer.close()
    println("People Information File generated successfully.")
  }

  def getRandomGender(): String = {
    val rand = new Random()
    val randNum = rand.nextInt(2) + 1
    if (randNum % 2 == 0) {
      "M"
    } else {
      "F"
    }
  }

}

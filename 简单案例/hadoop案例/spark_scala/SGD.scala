import scala.collection.mutable

/**
  * Created by 2281444815 on 2016/12/7.
  */
object SGD {
  val data = mutable.HashMap[Int,Int]()
  def getData():mutable.HashMap[Int,Int] = {
    for(i <- 1 to 10){
      data += (i -> (12*i))
    }
    data
  }
  var Θ:Double = 0
  var α:Double = 0.1
  def sgd(x:Double ,y:Double) = {
    Θ = Θ-α*((Θ*x)-y)
  }

  def main(args: Array[String]): Unit = {
    val dataSource = getData()
    dataSource.foreach(myMap =>{
      sgd(myMap._1,myMap._2)
      println("最终结果Θ值为"+Θ)
    })
    println("最终结果Θ值为"+Θ)
  }
}

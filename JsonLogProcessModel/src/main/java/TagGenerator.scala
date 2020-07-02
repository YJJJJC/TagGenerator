import com.taggen.ReviewTags
import org.apache.spark.{SparkConf, SparkContext}

/**
  *
  * @author:yjc
  * @Date: 2019/9/2 21:07
  * @Description:
  *
  */
object TagGenerator {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf();
    conf.setAppName("tagGen")
    conf.setMaster("local[4]")
    val sc = new SparkContext(conf)
//    val sqlContext = new HiveContext(sc)
//    import sqlContext.implicits._
    val poi_tags = sc.textFile("file:///f:/taggen/temptags.txt")

    val poi_taglist = poi_tags.map(e => e.split("\t"))
      .filter(e => e.length == 2)
      //(77287793,"a,b,c...“) eg: 77287793 -> 回头客,上菜快,环境优雅,性价比高,菜品不错
      .map(e => e(0) -> ReviewTags.extractTags(e(1)))
      //过滤评论串不为0
      .filter(e => e._2.length > 0)
      //e._1 商家ID eg:77287793  |||  e._2 切割value字符串 eg:切割”回头客,上菜快,环境优雅,性价比高,菜品不错“
      .map(e => e._1 -> e._2.split(","))
      //压扁 将77287793与【回头客,上菜快,环境优雅,性价比高,菜品不错】每一个结合 得到 77287793->回头客,77287793->上菜快 ....
      //与flatMap 略不同
      .flatMapValues(e => e)
      //得出 （77287793，回头客）-> 1 等  前面的元组为key
      .map(e => (e._1,e._2) -> 1)
      //通过key聚合  得出 （77287793，回头客）-> 280 等
      .reduceByKey(_+_)
      //将上面得出的重新映射成 77287793->list(回头客,280) 这句之后List中只有一个元组 不把它作为list的话没办法在value间聚合 元组没办法聚合 列表是可以的
      .map(e => e._1._1 -> List((e._1._2,e._2)))
      //77287793->list[(回头客,280),(上菜快,240),......]将所有list 放到一个List中
      .reduceByKey(_:::_)
      //_._2:通配 每个元素的第二个 即280，240等   e._2中的每个元素的第二个排序（升序）  reverse:倒序 take(10):提取前十个
      //之后再将->之后的e._2(针对的list)映射为: list[(回头客:280),(上菜快:240),....]  现在（）里已不是元组  已经是拼接起来的字符串
      .map(e => e._1 -> e._2.sortBy(_._2).reverse.take(10).map(a => a._1 + ":" + a._2.toString)
      //将list转化为字符串由“,”分隔  ===> 77287793->回头客:280,上菜快:240,......
        .mkString(","))
    //再次映射加保存
    poi_taglist.map(e => e._1 + "\t" + e._2).saveAsTextFile("file:///f:/taggen/res.txt")
  }
}

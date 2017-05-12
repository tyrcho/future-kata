import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object FindAsync extends App {

  val fut = FileIo.read("C:\\dev\\workspaces\\katas\\future-kata\\scala\\pom.xml")
  val result = Await.result(fut, 3.seconds)
  println(new String(result))
}

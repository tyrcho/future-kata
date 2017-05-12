import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

object FindAsync extends App {

  // TODO : trouver comment fournir une API non bloquante pour lire le fichier. Rapture.io ?

  // step 1 : récupérer une future d'une API
  // 2 : block & utiliser le result => handleResNow
  // 3 : utiliser en non bloquant
  // 4 : traiter les erreurs
  // montrer isCompleted et value
  // 5 : transformer Future[Array[Byte]] en Future[String]
  // 6  : combiner le resultats de 2 future
  // 7 : idem avec Future.sequence


  // montrer qu'on peut wrapper un code bloquant dans un Future { ... }
  // UC : callback vers Future :
  // utiliser promise.future pour wrapper un code async mais qui n'a pas la bonne API => écrire FileIo.read
  // expliquer l'ExecutionContext et l'implicit


  //  handleResNow(FileIo.read("pom.xml"), debug)
  //  handleResLater(FileIo.read("pom.xml"), debug)


  val fut = FileIo.read("pom.xml")
  //  val converted = fut.map { b => new String(b) }
  val converted = for {bytes <- fut} yield new String(bytes)

  //  for {res <- converted} println(res)

  val fut1 = FileIo.read("pom.xml")
  val fut2 = FileIo.read("src/main/scala/FileIo.scala")

  //  val futCount1 = fut1.map(countWords)
  //  val futCount2 = fut2.map(countWords)
  //  val total = for {
  //    c1 <- futCount1
  //    c2 <- futCount2
  //  } yield c1 + c2
  //  total.foreach(println)

  // attention  a ne pas inliner futCount1 et futCount2 sinon ce n'est plus fait en //

  for {
    seq <- Future.sequence(Seq(fut1.map(countWords), fut2.map(countWords)))
  } println(seq.sum)

  Thread.sleep(1000)

  def handleResLater(fut: Future[Array[Byte]], action: Array[Byte] => Unit): Unit = {
    fut.onComplete {
      case Success(bytes) => action(bytes)
      case Failure(e) => e.printStackTrace()
    }
  }

  def handleResNow(fut: Future[Array[Byte]], action: Array[Byte] => Unit): Unit = {
    val result = Await.result(fut, 3.seconds)
    action(result)
  }

  def countWords(s: Array[Byte]): Int = {
    println("counting")
    val size = new String(s).split("""\W+""").size
    println(s"size : $size")
    size
  }

  def debug(result: Array[Byte]): Unit = {
    println(new String(result))
  }
}

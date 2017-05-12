
import java.nio.ByteBuffer
import java.nio.channels.{AsynchronousFileChannel, CompletionHandler}
import java.nio.file.Paths
import java.nio.file.StandardOpenOption._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

object FileIoToComplete {
  def read(file: String)(implicit ec: ExecutionContext): Future[Array[Byte]] = {
//    val promise = Promise[Array[Byte]]()
    println(s"open file $file")
    try {
      val channel = AsynchronousFileChannel.open(Paths.get(file), READ)
      val buffer = ByteBuffer.allocate(channel.size().toInt)
      val handler =     new CompletionHandler[Integer, ByteBuffer]() {
        def completed(res: Integer, buffer: ByteBuffer): Unit = {
//          promise.success(buffer.array())
           println(s"file $file read OK")

            channel.close()
        }

        def failed(t: Throwable, buffer: ByteBuffer): Unit = {
//          promise.failure(t)
          println(s"file $file KO : $t")
            channel.close()
        }
      }

      channel.read(buffer, 0L, buffer, handler)
    }
    catch {
      case NonFatal(t) =>
//        promise.failure(t)
    }
//    promise.future
    ???
  }

}

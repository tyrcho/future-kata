package akka;

import akka.dispatch.ExecutionContexts;
import akka.dispatch.Futures;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import static java.nio.file.StandardOpenOption.READ;

/**
 * Created by a501768 on 22/05/2017.
 */
public class AkkaFileIO {

    private ExecutionContext executionContext;

    public AkkaFileIO(ExecutionContext ec){
        this.executionContext = ec;
    }

    public Future<byte[]> readAsync(String fileName)  {
        return Futures.future(() -> {
            try (FileChannel channel = FileChannel.open(Paths.get(fileName), READ)) {

                ByteBuffer buffer = ByteBuffer.allocate(4096);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream((int) channel.size());
                int readBytes = 0;
                while ((readBytes = channel.read(buffer)) != -1) {
                    outputStream.write(buffer.array(), 0, readBytes);
                }
                Thread.sleep(1000L);
                return outputStream.toByteArray();
            }
        }, executionContext);

    }
}

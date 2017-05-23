package vavr;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static java.nio.file.StandardOpenOption.READ;

public class FileIO {
    final Executor executor;
    public FileIO(Executor executor) {this.executor = executor;}

    public CompletableFuture<byte[]> readAsync(String fileName) throws IOException {
        CompletableFuture<byte[]> future = new CompletableFuture<>();
        try {
            AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get(fileName), READ);
            ByteBuffer buffer = ByteBuffer.allocate((int) channel.size());
            channel.read(buffer, 0L, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    try {
                        byte[] fileData = attachment.array();
                        future.complete(fileData);
                    } catch (Exception e) {
                        future.completeExceptionally(e);
                    } finally {
                        close(channel);
                    }
                }
                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    future.completeExceptionally(exc);
                    close(channel);
                }
            });
        } catch (IOException e) {
            future.completeExceptionally(e);
        }
        return future;
    }

    private void close(AsynchronousFileChannel channel) {
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

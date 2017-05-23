import akka.dispatch.*;
import scala.Function1;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by a501768 on 22/05/2017.
 */
public class ReadFileAkka {
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newCachedThreadPool();
        ExecutionContext ec = ExecutionContexts.fromExecutor(executor);

        AkkaFileIO fileIO = new AkkaFileIO(ec);

        //1 get future
        Future<byte[]> future1 = fileIO.readAsync("pom.xml");
        Future<byte[]> future2 = fileIO.readAsync("src/main/java/FileIO.java");


        Future<Integer> futureCount1 = future1.map(ReadFile::countWords, ec);
        Future<Integer> futureCount2 = future2.map(ReadFile::countWords, ec);


        // 2) blocking result
//        int length = Await.result(futureCount1, Duration.apply(2, TimeUnit.SECONDS));
//        int length2 = Await.result(futureCount2, Duration.apply(2, TimeUnit.SECONDS));
//        System.out.println("blocking length = "+length);
//        System.out.println("blocking length 2 = "+length2);

        //3) non blocking
        futureCount1.onSuccess(new OnSuccess<Integer>() {
            @Override
            public void onSuccess(Integer result) throws Throwable {
                System.out.println("non blocking length = " + result);
            }

        }, ec);


        //4) errorManagement
        futureCount2.onFailure(new OnFailure() {
            @Override
            public void onFailure(Throwable failure) throws Throwable {
                failure.printStackTrace();
            }

        }, ec);

        //5 map to string
        future1.map(new Mapper<byte[], String>() {
            @Override
            public String apply(byte[] buffer) {
                return new String(buffer, StandardCharsets.UTF_8);
            }
        }, ec).onSuccess(new OnSuccess<String>() {
            @Override
            public void onSuccess(String result) throws Throwable {
                System.out.println("File Content");
                System.out.printf(result);
            }

        }, ec);


        //7 combine future.sequance
        Future<Iterable<Integer>> sequence = Futures.sequence(Arrays.asList(futureCount1, futureCount2), ec);
//        sequence.map(new Mapper<Iterable<Integer>,Integer>() {
//            @Override
//            public Integer apply(Iterable<Integer> parameter) {
//                return StreamSupport.stream(parameter.spliterator(),false).mapToInt(i-> i).sum();
//            }
//        },ec).onSuccess(new OnSuccess<Integer>() {
//            @Override
//            public void onSuccess(Integer result) throws Throwable {
//                System.out.println("total length = "+result);
//
//                //if executor is not shutdown, program won't exit
//                executor.shutdown();
//            }
//        },ec);


        executor.awaitTermination(5, TimeUnit.SECONDS);
        executor.shutdown();
    }
}

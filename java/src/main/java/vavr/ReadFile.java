package vavr;

import io.vavr.collection.Seq;
import io.vavr.concurrent.Future;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class ReadFile {
    // step 1 : récupérer une future d'une API
    // 2 : block & utiliser le result => handleResNow
    // 3 : utiliser en non bloquant
    // 4 : traiter les erreurs
    // 5 : transformer Future[Array[Byte]] en Future[String]
    // 6  : combiner le resultats de 2 future
    // 7 : idem avec Future.sequence

    public static void main(String[] args) throws Exception {
        FileIO fileIO = new FileIO(Executors.newCachedThreadPool());
        //1
        CompletableFuture<byte[]> future = fileIO.readAsync("pom.xml");
        CompletableFuture<byte[]> future2 = fileIO.readAsync("src/main/java/java8/FileIO.java");

        Future<byte[]> f1 = Future.fromJavaFuture(future);
        Future<byte[]> f2 = Future.fromJavaFuture(future2);

        //2
//        System.out.println(new String(f1.get()));

        //3
//        f1.onSuccess(bytes -> System.out.println(new String(bytes)));

        //4
        Future<String> fs = f1.map(bytes -> new String(bytes));
//        fs.onSuccess(System.out::println);

//6

        Future<Integer> lengthFuture = f1.map(ReadFile::countWords);
//        lengthFuture.onSuccess(System.out::println);

        Future<Integer> lengthFuture2 = f2.map(ReadFile::countWords);

//        Future<Integer> sumFuture = lengthFuture.zip(lengthFuture2).map(tuple -> tuple._1 + tuple._2);
//        sumFuture.onSuccess(System.out::println);

        //7
        Future<Seq<Integer>> countsFuture = Future.sequence(Arrays.asList(lengthFuture, lengthFuture2));
        Future<Number> sumFuture = countsFuture.map(counts -> counts.sum());
        sumFuture.onSuccess(System.out::println);
    }

    public static int countWords(byte[] bytes) {
        System.out.println("counting");
        int length = new String(bytes).split("\\W+").length;
        System.out.println("Length: " + length);
        return length;
    }
}

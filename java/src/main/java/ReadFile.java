import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

import static java.util.stream.Collectors.toList;

public class ReadFile {
    // step 1 : récupérer une future d'une API
    // 2 : block & utiliser le result => handleResNow
    // 3 : utiliser en non bloquant
    // 4 : traiter les erreurs
    // montrer isCompleted et value
    // 5 : transformer Future[Array[Byte]] en Future[String]
    // 6  : combiner le resultats de 2 future
    // 7 : idem avec Future.sequence

    public static void main(String[] args) throws Exception {
        FileIO fileIO = new FileIO(Executors.newCachedThreadPool());
        //1
        CompletableFuture<byte[]> future = fileIO.readAsync("pom.xml");
        CompletableFuture<byte[]> future2 = fileIO.readAsync("src/main/java/FileIO.java");

        //2
        //System.out.println(new String(future.get()));
//        System.out.println(new String(future.get(2, TimeUnit.SECONDS)));

        //3
        //future.thenAccept(bytes -> System.out.println(new String(bytes)));

        //4
//        future.exceptionally(e -> {
//            e.printStackTrace();
//            return null;
//        });


//        CompletableFuture<Integer> code = future.handle((ok, ex) -> {
//            if (ok != null) {
//                System.out.println(new String(ok));
//                return 0;
//            } else {
//                ex.printStackTrace();
//                return 1;
//            }
//        });
//        System.exit(code.get());

        //CompletableFuture<String> stringFuture = future.thenApply(String::new);
        //System.out.println(stringFuture.get());
//6
        CompletableFuture<Integer> lengthFuture = future.thenApply(ReadFile::countWords);
        System.out.println(lengthFuture.get());

        CompletableFuture<Integer> lengthFuture2 = future2.thenApply(ReadFile::countWords);

//        CompletableFuture<Integer> sumFuture = lengthFuture.thenCombine(lengthFuture2, (l1, l2) -> l1 + l2);


        //7
        CompletableFuture<List<Integer>> countsFuture = sequence(Arrays.asList(lengthFuture, lengthFuture2));
        CompletableFuture<Integer> sumFuture = countsFuture.thenApply(counts -> counts.stream().mapToInt(i -> i).sum());
        System.out.println(sumFuture.get());


    }

    public static int countWords(byte[] bytes) {
        System.out.println("counting");
        int length = new String(bytes).split("\\W+").length;
        System.out.println("Length: " + length);
        return length;
    }


    static <T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> futures) {
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(toList())
                );
    }
}

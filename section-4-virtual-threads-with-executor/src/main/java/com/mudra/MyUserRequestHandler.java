package com.mudra;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyUserRequestHandler implements Callable<String> {
    @Override
    public String call() throws Exception {
        ExecutorService service = Executors.newVirtualThreadPerTaskExecutor();
        String result = CompletableFuture.supplyAsync(() -> dbCall1(), service)
                .thenApply(val -> {
                    String val2 = dbCall2();
                    return "[" + val + "," + val2 + "]";
                })
                .thenCombine(CompletableFuture.supplyAsync(this::restCall1, service),
                        (r1, r2) -> {
                            return "[" + r1 + "," + r2 + "]";
                        })
                .thenCombine(CompletableFuture.supplyAsync(this::restCall2, service),
                        (r1, r2) -> {
                            return "[" + r1 + "," + r2 + "]";
                        })
                .join();

        System.out.println(result);
        return result;
    }

    private String dbCall1() {
        return doCall("dbCall1", 2);
    }
    private String dbCall2() {
        return doCall("dbCall2", 3);
    }
    private String restCall1() {
        return doCall("restCall1", 4);
    }
    private String restCall2() {
        return doCall("restCall2", 5);
    }

    private String doCall(String name, int secs) {
        NetworkCaller caller = new NetworkCaller(name);
        try {
            return caller.makeCall(secs);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

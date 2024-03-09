package irish.bla;

import java.time.Duration;
import java.util.concurrent.*;

import irish.bla.LongRunningTask.TaskResponse;

public class LongRunningTask implements Callable<TaskResponse> {
    private final String name;
    private final int time;
    private final String output;
    private final boolean fail;

    public LongRunningTask(String name, int time, String output, boolean fail) {
        this.name = name;
        this.time = time;
        this.output = output;
        this.fail = fail;
    }


    public static void main(String[] args) throws InterruptedException {
        System.out.println("> Main started");
        LongRunningTask task = new LongRunningTask("LongTask1", 10, "json-response1", false);
        try (ExecutorService service = Executors.newFixedThreadPool(2)) {
            Future<TaskResponse> taskFuture = service.submit(task);
            Thread.sleep(Duration.ofSeconds(5));
            taskFuture.cancel(true);

        };
        System.out.println("> Main ended");

    }
    private void print(String message) {
        System.out.printf("> %s : %s\n", name, message);
    }

    @Override
    public TaskResponse call() throws Exception {
        long start = System.currentTimeMillis();
        print("Started");

        int numSecs = 0;
        while (numSecs++ < this.time) {
            if (Thread.interrupted()) {
                throwExceptionOnInterrupted();
            }
            print("Working " + numSecs);
            try {
                Thread.sleep(Duration.ofSeconds(1));
            } catch (InterruptedException e) {
                throwExceptionOnInterrupted();
            }
        }

        if (this.fail) {
            throwExceptionOnFailure();
        }
        print("Ended");
        return new TaskResponse(this.name, this.output, (System.currentTimeMillis() - start));
    }

    private void throwExceptionOnInterrupted()throws InterruptedException {
        print("Interrupted");
        throw new InterruptedException();
    }

    private void throwExceptionOnFailure() {
        print("Failure");
        throw new RuntimeException(name + " failed");

    }

    public record TaskResponse(String name, String response, long timeTaken) {

    }
}

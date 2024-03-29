package irish.bla;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.StructuredTaskScope;



public class STaskSimpleExamples {

    public static void main(String[] args)throws Exception {
        shutdownWhenFirstChildSucceeds();
    }

    private static void interruptMain() {
        Thread mainThread = Thread.currentThread();
        Thread.ofPlatform().start(() -> {
            try {
                Thread.sleep(Duration.ofSeconds(2));
                mainThread.interrupt();
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
    private static void shutdownWhenFirstChildSucceeds() throws Exception {
        try(var scope = new StructuredTaskScope.ShutdownOnSuccess<LongRunningTask.TaskResponse>() ) {
            LongRunningTask weather1= new LongRunningTask("weather1", 3, "30", false);
            LongRunningTask weather2= new LongRunningTask("weather2", 10, "34", false);
            StructuredTaskScope.Subtask<LongRunningTask.TaskResponse> weather1SubTask = scope.fork(weather1);
            StructuredTaskScope.Subtask<LongRunningTask.TaskResponse> weather2SubTask = scope.fork(weather2);

            scope.join();

            LongRunningTask.TaskResponse result = scope.result();
            System.out.println(result);

        }
    }

    private static void shutdownWhenFirstChildFails() throws Exception {
        try(var scope = new StructuredTaskScope.ShutdownOnFailure() ) {
            LongRunningTask dataTask = new LongRunningTask("data-task", 3, "row1j", true);
            LongRunningTask restTask = new LongRunningTask("rest-task", 10, "json1", false);
            StructuredTaskScope.Subtask<LongRunningTask.TaskResponse> dataSubTask = scope.fork(dataTask);
            StructuredTaskScope.Subtask<LongRunningTask.TaskResponse> restSubTask = scope.fork(restTask);

            scope.join();
            scope.throwIfFailed();

            System.out.println(dataSubTask.get());
            System.out.println(restSubTask.get());

        }
    }

    private static void exampleCompleteAllTasks() throws Exception {

        try (var scope = new StructuredTaskScope<LongRunningTask.TaskResponse>()) {
            LongRunningTask expediaTask = new LongRunningTask("expedia-task", 3, "100$", true);
            LongRunningTask hotwireTask = new LongRunningTask("hotwire-task", 10, "110$", true);

            StructuredTaskScope.Subtask<LongRunningTask.TaskResponse> expSubTask = scope.fork(expediaTask);
            StructuredTaskScope.Subtask<LongRunningTask.TaskResponse> hotSubTask = scope.fork(hotwireTask);

            scope.join();
//            scope.joinUntil(Instant.now().plus(Duration.ofSeconds(2)));

            handleOutcome(expSubTask);
            handleOutcome(hotSubTask);
        }
    }

    private static void handleOutcome(StructuredTaskScope.Subtask<LongRunningTask.TaskResponse> task) {
        StructuredTaskScope.Subtask.State state = task.state();
        if (state == StructuredTaskScope.Subtask.State.SUCCESS) {
            System.out.println(task.get());
        }
        else  if (state == StructuredTaskScope.Subtask.State.FAILED) {
            System.err.println(task.exception());
        }
    }
}

package irish.bla;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.StructuredTaskScope;



public class STaskSimpleExamples {

    public static void main(String[] args)throws Exception {
        interruptMain();
        exampleCompleteAllTasks();
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

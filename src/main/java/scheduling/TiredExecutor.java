package scheduling;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import spl.lae.Main;

public class TiredExecutor {

    private final TiredThread[] workers;
    private final PriorityBlockingQueue<TiredThread> idleMinHeap = new PriorityBlockingQueue<>();
    private final AtomicInteger inFlight = new AtomicInteger(0);

    public TiredExecutor(int numThreads) {
        // TODO
        workers = new TiredThread[numThreads];
        for (int index = 0; index < workers.length; index++) {
            Random random = new Random();
            double fatigueFactor = 0.5 + (1.5 - 0.5) * random.nextDouble();
            workers[index] = new TiredThread(index, fatigueFactor);
            workers[index].start();
            idleMinHeap.add(workers[index]);
        }

    }

    public void submit(Runnable task) {
        // TODO

        try {

            inFlight.incrementAndGet();
            final TiredThread worker = idleMinHeap.take();
            synchronized (worker) {
                Runnable newTask = () -> {
                    try {
                        task.run();

                    } catch (Exception e) {
    try {
        parser.OutputWriter.write(e.getMessage(), Main.getOutputPath());
    } catch (Exception ignored) {}
    System.exit(1);
} finally {

                        this.idleMinHeap.add(worker);
                        if (inFlight.decrementAndGet() == 0) {
                            synchronized (inFlight) {
                                inFlight.notifyAll();
                            }

                        }
                    }
                };

                worker.newTask(newTask);
            }

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
            if (inFlight.decrementAndGet() == 0) {
                synchronized (inFlight) {
                    inFlight.notifyAll();
                }

            }
        }
    }

    public void submitAll(Iterable<Runnable> tasks) {
        // TODO: submit tasks one by one and wait until all finish
        for (Runnable runnable : tasks) {
            submit(runnable);
        }
        synchronized (inFlight) {
            try {

                while (inFlight.get() != 0) {
                    inFlight.wait();
                }
            } catch (InterruptedException exception) {

                Thread.currentThread().interrupt();
                try {
                    this.shutdown();
                } catch (InterruptedException e) {
                }

            }
        }

    }

    public void shutdown() throws InterruptedException {
        // TODO
        for (TiredThread worker : workers) {
            if (worker != null)
                worker.shutdown();
        }
        for (TiredThread worker : workers) {
            if (worker != null)
                worker.join();
        }
    }

    public synchronized String getWorkerReport() {
        int n = workers.length;
        double[] fatigueSnapshot = new double[n];
        long[] usedSnapshot = new long[n];
        double sumFatigue = 0;

        for (int i = 0; i < n; i++) {
            synchronized (workers[i]) {
                fatigueSnapshot[i] = workers[i].getFatigue();
                usedSnapshot[i] = workers[i].getTimeUsed();
                sumFatigue += fatigueSnapshot[i];
            }
        }

        double averageFatigue = sumFatigue / n;
        double fairnessFactor = 0;
        String str = "";

        for (int i = 0; i < n; i++) {
            str += "Worker " + i + " Work: " + usedSnapshot[i] + " Fatigue: " + fatigueSnapshot[i] + "\n";
            fairnessFactor += Math.pow(fatigueSnapshot[i] - averageFatigue, 2);
        }

        str += "Fairness Factor: " + fairnessFactor + "\n";
        return str;
    }
}

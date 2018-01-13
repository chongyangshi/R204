// To compile:
//
// javac Q4.java

import java.util.concurrent.atomic.*;
import java.util.Random;


class Q4 {

  class SharedArr {

    public volatile int[] integers;
    public volatile boolean lock = false;
    private final Object internalLock = new Object();

    public boolean testAndSet(boolean lock) {
      boolean result;
      synchronized (internalLock) {
        result = lock;
        lock = true;
      }
      return result;
    }

    public void acquireLock(boolean lock) {
      do {
        while (lock) {}
      } while (testAndSet(lock));
    }

    public void releaseLock(boolean lock) {
      lock = false;
    }

    SharedArr(int X) {
      this.integers = new int[X];
      Random rand = new Random();
      for (int n = 0; n < X; n++) {
        this.integers[n] = rand.nextInt(999) + 1;
      }
    }

  }

  class Runner implements Runnable {

    int threadN;
    int reps;
    boolean multithreading;
    SharedArr array;
    volatile int s;

    Runner(SharedArr array, int threadN, int reps) {
      this.array = array;
      this.threadN = threadN;
      this.reps = reps;
    }

    public void sum() {
      this.s = 0;
      for (int n : this.array.integers) {
        this.s += n;
      }
    }

    @Override
    public void run() {

      boolean isFirstThread = (this.threadN == 0) ? true : false;

      if (isFirstThread) {
        for (int r = 0; r < reps; r++) {
          this.array.acquireLock(this.array.lock);
          this.sum();
          this.array.releaseLock(this.array.lock);
        }
      } else {
        while (!Thread.currentThread().isInterrupted()) {
          this.array.acquireLock(this.array.lock);
          this.sum();
          this.array.releaseLock(this.array.lock);
        }
      }
    }

  }

  Q4() {

    Long startTime, endTime;

    SharedArr testArray5 = new SharedArr(5);
    SharedArr testArray5000 = new SharedArr(5000);
    int reps = 50000;
    float[] results;
    Thread[] threads;
    Runner[] runners;
    int cores = Runtime.getRuntime().availableProcessors();
    int maxThreads = cores * 2;
    long duration;

    // X = 5, built-in lock.
    System.out.println("Average with X = 5, TATAS mutex, multithread: ");
    for (int x = 0; x < 10; x++) {
      results = new float[maxThreads];
      for (int ts = 1; ts < (maxThreads + 1); ts++) {
        startTime = System.nanoTime();
        try {
          threads = new Thread[ts];
          runners = new Runner[ts];
          for (int p = 0; p < ts; p++) {
            runners[p] = new Runner(testArray5, p, reps);
            threads[p] = new Thread(runners[p]);
            threads[p].start();
          }
          threads[0].join();
          for (int p = 1; p < ts; p++) {
            threads[p].interrupt();
          }
        } catch (InterruptedException ie) {
          System.out.println("Caught " + ie);
        }
        endTime = System.nanoTime();
        duration = (endTime - startTime);
        results[ts - 1] = (float) duration / 1000000000;
      }
      for (int n = 0; n < maxThreads; n++) {
        System.out.printf("%.9f", results[n]);
        if (n < (maxThreads - 1)) {
          System.out.print(", ");
        }
      }
      System.out.println("");
    }

    // X = 5000, built-in lock.
    System.out.println("Average with X = 5000, TATAS mutex, multithread: ");
    for (int x = 0; x < 10; x++) {
      results = new float[maxThreads];
      for (int ts = 1; ts < (maxThreads + 1); ts++) {
        startTime = System.nanoTime();
        try {
          threads = new Thread[ts];
          runners = new Runner[ts];
          for (int p = 0; p < ts; p++) {
            runners[p] = new Runner(testArray5000, p, reps);
            threads[p] = new Thread(runners[p]);
            threads[p].start();
          }
          threads[0].join();
          for (int p = 1; p < ts; p++) {
            threads[p].interrupt();
          }
        } catch (InterruptedException ie) {
          System.out.println("Caught " + ie);
        }
        endTime = System.nanoTime();
        duration = (endTime - startTime);
        results[ts - 1] = (float) duration / 1000000000;
      }
      for (int n = 0; n < maxThreads; n++) {
        System.out.printf("%.9f", results[n]);
        if (n < (maxThreads - 1)) {
          System.out.print(", ");
        }
      }
      System.out.println("");
    }

  }

  public static void main(String args[]) {
    Q4 obj = new Q4();
  }
}

// To compile:
//
// javac Q6.java

import java.util.concurrent.atomic.*;
import java.util.Random;


class Q6 {

  class SharedArr {

    public volatile int[] integers;
    public AtomicInteger owner = new AtomicInteger(0);
    public boolean[] threadFlags;

    public void acquireRead(int threadN) {
      do {
        this.threadFlags[threadN] = true;
        if (owner.get() == 0) {
          break;
        } else {
          this.threadFlags[threadN] = false;
        }
      } while (true);
    }

    public void releaseRead(int threadN) {
      this.threadFlags[threadN] = false;
    }

    SharedArr(int X, int noFlags) {
      this.integers = new int[X];
      Random rand = new Random();
      for (int n = 0; n < X; n++) {
        this.integers[n] = rand.nextInt(999) + 1;
      }
      this.threadFlags = new boolean[noFlags];
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
          this.array.acquireRead(this.threadN);
          this.sum();
          this.array.releaseRead(this.threadN);
        }
      } else {
        while (!Thread.currentThread().isInterrupted()) {
          this.array.acquireRead(this.threadN);
          this.sum();
          this.array.releaseRead(this.threadN);
        }
      }
    }

  }

  Q6() {

    Long startTime, endTime;
    SharedArr testArray5, testArray5000;
    int reps = 20000;
    float[] results;
    Thread[] threads;
    Runner[] runners;
    int cores = Runtime.getRuntime().availableProcessors();
    int maxThreads = cores * 2;
    long duration;

    // X = 5, built-in lock.
    System.out.println("Average with X = 5, built-in lock, multithread: ");
    for (int x = 0; x < 10; x++) {
      results = new float[maxThreads];
      for (int ts = 1; ts < (maxThreads + 1); ts++) {
        testArray5 = new SharedArr(5, ts);
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
        System.out.print(results[n]);
        if (n < (maxThreads - 1)) {
          System.out.print(", ");
        }
      }
      System.out.println("");
    }

    // X = 5000, built-in lock.
    System.out.println("Average with X = 5000, built-in lock, multithread: ");
    for (int x = 0; x < 10; x++) {
      results = new float[maxThreads];
      for (int ts = 1; ts < (maxThreads + 1); ts++) {
        testArray5000 = new SharedArr(5000, ts);
        startTime = System.currentTimeMillis();
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
        endTime = System.currentTimeMillis();
        duration = (endTime - startTime);
        results[ts - 1] = (float) duration / 1000;
      }
      for (int n = 0; n < maxThreads; n++) {
        System.out.print(results[n]);
        if (n < (maxThreads - 1)) {
          System.out.print(", ");
        }
      }
      System.out.println("");
    }

  }

  public static void main(String args[]) {
    Q6 obj = new Q6();
  }
}

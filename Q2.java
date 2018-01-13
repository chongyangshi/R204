// To compile:
//
// javac Q2.java

import java.util.concurrent.atomic.*;

class Q2 implements Runnable {

  // Delay function waits a variable time controlled by "d".  The function
  // writes to a per-object volatile field -- this aims to prevent the compiler
  // from optimizing the delay away completely.

  volatile int temp;
  void delay(int arg) {
    for (int i = 0; i < arg; i++) {
      for (int j = 0; j < 1000000; j++) {
        this.temp += i + j;
      }
    }
  }

  // Constructor for an "Q2" object.  Fields in the object can be
  // used to pass values to/from the thread when it is started and
  // when it finishes.

  int arg;
  int result;
  int t;

  Q2(int arg, int t) {
    this.arg = arg;
    this.t = t;
  }

  // Q2 thread function.  This just delays for a little while,
  // controlled by the parameter passed when the thread is started.

  public void run() {
    delay(arg);
    result = 42;
  }

  // Shared variable for use with example atomic compare and swap
  // operations (ai.compareAndSet in this example).

  static AtomicInteger ai = new AtomicInteger(0);

  // Main function

  public static void main(String args[]) {

    // Start a new thread, and then wait for it to complete:

    if (args.length < 1) {
      System.out.println("Usage: java Q2 delayFactor");
      System.exit(1);
    }

    int delayFactor = Integer.parseInt(args[0]);

    int cores = Runtime.getRuntime().availableProcessors();
    int maxThreads = cores * 2;
    System.out.println("This system has " + cores + " hardware threads.");

    System.out.println("Results from 10 runs of 1-" + maxThreads + " threads: ");
    for (int c = 0; c < 10; c++) {

      Long[] results = new Long[maxThreads];
      for (int x = 1; x < (maxThreads + 1); x++) {
        // System.out.println("Executing with " + x + " threads...");
        Long startTime = System.currentTimeMillis();

        try {
          Thread[] threads = new Thread[x];
          Q2[] examples = new Q2[x];

          for (int n = 0; n < x; n++) {
            Q2 e = new Q2(delayFactor, (n+1));
            Thread t = new Thread(e);
            threads[n] = t;
            examples[n] = e;
            t.start();
          }

          for (int n = 0; n < x; n++) {
            threads[n].join();
          }

        } catch (InterruptedException ie) {
          System.out.println("Caught " + ie);
        }
        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);

        // System.out.println("Completed in " + duration + " ms.");
        results[x-1] = duration;
      }

      for (int n = 0; n < maxThreads; n++) {
        System.out.print((float)results[n] / 1000);
        if (n < (maxThreads - 1)) {
          System.out.print(", ");
        }
      }
      System.out.println("");
    }

  }
}

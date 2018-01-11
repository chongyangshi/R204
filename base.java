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
    System.out.println("Thread " + this.t + " started with delay factor " + arg + ".");
    delay(arg);
    result = 42;
    System.out.println("Thread " + this.t + " done with result " + result + ".");
  }

  // Shared variable for use with example atomic compare and swap
  // operations (ai.compareAndSet in this example).

  static AtomicInteger ai = new AtomicInteger(0);

  // Main function

  public static void main(String args[]) {

    // Start a new thread, and then wait for it to complete:

    if (args.length < 2) {
      System.out.println("Usage: java Q2 delayFactor noThreads");
      System.exit(1);
    }

    int delayFactor = Integer.parseInt(args[0]);
    System.out.println("Start of execution...");
    long startTime = System.currentTimeMillis();

    int ts = Integer.parseInt(args[1]);
    if (ts < 1 || ts > 16) {
      System.out.println("1 <= noThreads <= 16, exiting.");
      System.exit(1);
    }

    try {

      Thread[] threads = new Thread[ts];
      Q2[] examples = new Q2[ts];

      for (int n = 0; n < ts; n++) {
        Q2 e = new Q2(delayFactor, (n+1));
        Thread t = new Thread(e);
        threads[n] = t;
        examples[n] = e;
        t.start();
      }

      for (int n = 0; n < ts; n++) {
        threads[n].join();
        System.out.println("Joined with thread " + (n + 1) + ", returned " + examples[n].result + ".");
      }

    } catch (InterruptedException ie) {
      System.out.println("Caught " + ie);
    }
    long endTime = System.currentTimeMillis();
    long duration = (endTime - startTime);

    System.out.println("Completed in " + duration + " ms.");
  }
}

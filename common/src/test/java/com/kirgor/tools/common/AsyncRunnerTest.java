package com.kirgor.tools.common;

import org.junit.Test;

import static org.junit.Assert.*;

public class AsyncRunnerTest {
    @Test
    public void run() throws InterruptedException {
        SumAsyncRunner runner = new SumAsyncRunner();

        runner.addItem(1);
        runner.addItem(2);
        runner.addItem(3);

        runner.start(false);

        runner.addItem(1);
        runner.addItem(2);
        runner.addItem(3);

        Thread.sleep(100);
        assertEquals(12, runner.getSum());
    }

    final class SumAsyncRunner extends AsyncRunner<Integer> {
        private int sum = 0;

        int getSum() {
            return sum;
        }

        @Override
        protected void processItem(Integer item) {
            sum += item;
            System.out.println("SUM = " + sum);
        }
    }
}

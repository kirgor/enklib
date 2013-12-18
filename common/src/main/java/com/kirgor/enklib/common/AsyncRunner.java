package com.kirgor.enklib.common;

import java.util.ArrayDeque;
import java.util.concurrent.Semaphore;

/**
 * Base class for async runners, which run on separate thread and have items queue to be processed.
 *
 * @param <T> Type of items to be processed.
 */
public abstract class AsyncRunner<T> {
    private boolean isUsed;
    private boolean isRunning;
    private Semaphore semaphore = new Semaphore(0);
    private ArrayDeque<T> items = new ArrayDeque<T>();

    /**
     * Starts processing the items queue. This method can be called only once.
     *
     * @param isDaemon Specifies whether underlying thread will be daemon thread.
     */
    public void start(boolean isDaemon) {
        synchronized (this) {
            if (!isUsed) {
                isUsed = true;
                isRunning = true;
                Thread thread = new Thread(new RunnableImpl());
                thread.setDaemon(isDaemon);
                thread.start();
            } else {
                throw new IllegalStateException("Async runner has been already used once.");
            }
        }
    }

    /**
     * Stops processing the queue forever. In order to continue processing
     * items you need to create new instance of async runner.
     */
    public void stop() {
        synchronized (this) {
            isRunning = false;
            items.clear();
        }
    }

    /**
     * Adds an item to the processing queue. Can be called even before the start.
     *
     * @param item Item to add.
     */
    public void addItem(T item) {
        synchronized (this) {
            items.add(item);
            semaphore.release();
        }
    }

    /**
     * Adds multiple items to the processing queue. Can be called even before the start.
     * This method is preferred to calling addItem multiple times in row, because it will
     * make synchronization only once.
     *
     * @param items Items to add.
     */
    public void addItems(Iterable<T> items) {
        synchronized (this) {
            for (T i : items) {
                this.items.add(i);
            }
            semaphore.release();
        }
    }

    /**
     * Processes the item, which was just picked from the queue.
     *
     * @param item Item to process.
     */
    protected abstract void processItem(T item);

    class RunnableImpl implements Runnable {
        @Override
        public void run() {
            while (isRunning) {
                try {
                    semaphore.acquire();
                } catch (InterruptedException ignored) {
                }

                T item;
                synchronized (AsyncRunner.this) {
                    item = items.poll();
                }
                if (item != null) {
                    processItem(item);
                }
            }
        }
    }
}

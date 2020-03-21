/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.util;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * The type Task node thread pool manager.
 *
 * @since 2019 -10-15
 */
public class TaskNodeThreadPoolManager {
    private static final Logger logger = LoggerFactory.getLogger(TaskNodeThreadPoolManager.class);
    private static ScheduledExecutorService scheduledExecutorService;
    private static TaskNodeThreadPoolManager instance;
    private static int cpuProcessor;
    private static int maxPoolSize;


    static {
        cpuProcessor = 30;
        maxPoolSize = 50;
    }
    private ExecutorService executor;

    private TaskNodeThreadPoolManager() {

    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static TaskNodeThreadPoolManager getInstance() {
        synchronized (TaskNodeThreadPoolManager.class) {
            if (instance == null) {
                instance = new TaskNodeThreadPoolManager();
            }
            return instance;
        }
    }

    /**
     * Schedule.
     *
     * @param r the r
     * @param delay the delay
     */
    public static void schedule(Runnable r, int delay) {
        if (null == scheduledExecutorService) {
            scheduledExecutorService = Executors.newScheduledThreadPool(cpuProcessor);
        }
        scheduledExecutorService.schedule(r, delay, TimeUnit.SECONDS);
    }

    private static int getCpuProcessors() {
        int num = Runtime.getRuntime().availableProcessors();
        num = num > 4 ? num - 2 : num;
        return num;
    }

    /**
     * Execute.
     *
     * @param lst the lst
     */
    public void execute(List<Runnable> lst) {
        if (CollectionUtils.isEmpty(lst)) {
            return;
        }
        for (Runnable task : lst) {
            execute(task);
        }
    }

    /**
     * Execute.
     *
     * @param r the r
     */
    public void execute(Runnable r) {
        logger.info("TaskNodeThreadPoolManager execute enter. corePoolSize is {}, maximumPoolSize is {}.",
            cpuProcessor, maxPoolSize);
        synchronized (ExecutorService.class) {
            if (executor == null) {
                executor = new ThreadPoolExecutor(cpuProcessor, maxPoolSize, 60L, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(500));
            }
            executor.execute(r);
        }
        // 把任务丢到线程池里面去
        logger.info("TaskNodeThreadPoolManager execute leave...");
    }

    /**
     * Submit boolean thread future.
     *
     * @param task the task
     * @return the future
     */
    public Future<Boolean> submitBooleanThread(Callable<Boolean> task) {
        logger.info("TaskNodeThreadPoolManager submitVmWareThread enter...");
        synchronized (ExecutorService.class) {
            if (executor == null) {
                executor = new ThreadPoolExecutor(cpuProcessor, maxPoolSize, 60L, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(500));
            }
            logger.info("TaskNodeThreadPoolManager submitVmWareThread leave...");
            // 把任务丢到线程池里面去
            return executor.submit(task);
        }
    }

}

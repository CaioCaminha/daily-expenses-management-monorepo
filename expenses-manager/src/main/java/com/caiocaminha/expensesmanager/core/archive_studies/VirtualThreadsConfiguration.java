package com.caiocaminha.expensesmanager.core.archive_studies;

import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class VirtualThreadsConfiguration {

    /**
     * An Example of TaskExecutor bean but specifying AsyncTaskExecutor.
     *
     * Probably AsyncTaskExecutor or SimpleAsyncTaskExecutor it's best TaskExecutor implementation to use with
     * Virtual Threads, since by default it doesn't re-use any threads, what's expected
     * for virtual threads (VTs should not be pooled).
     * @return
     */
    @Bean(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    public AsyncTaskExecutor asyncTaskExecutor() {
//        return new SimpleAsyncTaskExecutor(Thread.ofVirtual().factory());
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }

    /**
     * Example of ThreadPoolTaskExecutor | Traditional Threads
     * Virtual Threads should not be pooled
     * @return
     */
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(5);
        threadPoolTaskExecutor.setMaxPoolSize(10);
        threadPoolTaskExecutor.setQueueCapacity(25);
        /**
         * Decorator should implement a custom behavior before/after executing the task
         */
        threadPoolTaskExecutor.setTaskDecorator(new DecoratorExample());
        return threadPoolTaskExecutor;
    }

    @Bean
    public Executor taskExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }


    private class DecoratorExample implements TaskDecorator {

        @Override
        public Runnable decorate(Runnable runnable) {
            return () -> {
                System.out.println("Logging before");
                runnable.run();
                System.out.println("Logging after");
            };
        }
    }

}

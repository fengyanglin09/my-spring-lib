package io.github.fengyanglin09.springintegrationlab.lessons.lesson09_split_aggregate_resequence.config;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson09_split_aggregate_resequence.support.Lesson09Channels;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

// @Configuration tells Spring:
// "Read this class during startup because it contains bean definitions."
@Configuration
public class Lesson09IntegrationConfiguration {

    // This is the input channel for lesson 09.
    //
    // The gateway sends one Lesson09BatchOrderRequest message here. The flow
    // starts from this same channel and then splits that one message into many
    // line-item messages.
    @Bean(name = Lesson09Channels.BATCH_ORDERS)
    MessageChannel lesson09BatchOrders() {
        return new DirectChannel();
    }

    // This executor is used by the flow's executor channel.
    //
    // Executor means:
    // "Run work on a pool of background threads instead of keeping every step
    // on the original gateway caller's thread."
    //
    // Why use it in this lesson?
    //
    // After the splitter creates one message per line item, those messages can
    // be priced independently. Independent work often finishes in a different
    // order than it started. That gives the resequencer a real job: restore the
    // original sequence before the aggregator builds the final summary.
    //
    // @Bean tells Spring:
    // "Create this ThreadPoolTaskExecutor during startup and make it available
    // for other beans to use."
    //
    // Lesson09SplitAggregateResequenceFlow receives this bean as a method
    // argument named lesson09LineItemExecutor. Spring passes it in
    // automatically because the bean name and type match.
    @Bean
    ThreadPoolTaskExecutor lesson09LineItemExecutor() {
        // ThreadPoolTaskExecutor is Spring's wrapper around a Java thread pool.
        //
        // Think of it like a small team of workers. Instead of one person
        // pricing line 1, then line 2, then line 3, the executor lets several
        // worker threads price different line-item messages at the same time.
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // corePoolSize means:
        // "Keep up to 3 worker threads ready for normal work."
        //
        // In this lesson, a batch has a few line items, so 3 workers is enough
        // to show parallel processing without making the example noisy.
        executor.setCorePoolSize(3);

        // maxPoolSize means:
        // "Do not grow beyond 3 worker threads."
        //
        // Because corePoolSize and maxPoolSize are both 3, this lesson uses a
        // fixed-size worker team. That makes the example easier to reason about.
        executor.setMaxPoolSize(3);

        // queueCapacity means:
        // "If all 3 workers are busy, allow up to 10 extra messages to wait."
        //
        // The queue is a waiting line in front of the worker threads. It is not
        // the same as a Spring Integration QueueChannel; it is only the
        // executor's internal waiting line for tasks.
        //
        // Lesson boundary:
        // This lesson assumes the batch is small enough that all split messages
        // can be accepted by this executor. If the splitter creates more work
        // than the executor can run or queue, the extra work may be rejected.
        //
        // We are intentionally not solving that here because this lesson is
        // focused on split, resequence, and aggregate. Executor saturation and
        // backpressure belong in the polling/scheduling/backpressure lesson.
        executor.setQueueCapacity(10);

        // threadNamePrefix controls the beginning of worker thread names.
        //
        // If you see logs or debugger threads named lesson09-line-item-1,
        // lesson09-line-item-2, and so on, they came from this executor.
        executor.setThreadNamePrefix("lesson09-line-item-");

        // waitForTasksToCompleteOnShutdown(true) means:
        // "When Spring is shutting down, do not immediately abandon line-item
        // work that is already running."
        executor.setWaitForTasksToCompleteOnShutdown(true);

        // awaitTerminationSeconds(2) means:
        // "During shutdown, wait up to 2 seconds for running tasks to finish."
        //
        // This is small because the lesson work is tiny. A production flow
        // would choose this value based on how long real work may take.
        executor.setAwaitTerminationSeconds(2);

        return executor;
    }
}

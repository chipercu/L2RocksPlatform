package com.fuzzy.subsystems.scheduler;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.sdk.component.Component;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.scheduler.internal.SchedulerJob;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import java.sql.Date;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class Scheduler {

    private final org.quartz.Scheduler scheduler;
    private final AtomicLong jobId = new AtomicLong(0);

    public Scheduler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) throws SchedulerException {
        scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.getListenerManager().addJobListener(new JobListener() {
            @Override
            public String getName() {
                return Scheduler.class.getName();
            }

            @Override
            public void jobToBeExecuted(JobExecutionContext jobExecutionContext) {
                // do nothing
            }

            @Override
            public void jobExecutionVetoed(JobExecutionContext jobExecutionContext) {
                // do nothing
            }

            @Override
            public void jobWasExecuted(JobExecutionContext jobExecutionContext, JobExecutionException e) {
                if (e != null && e.getCause() != null) {
                    Throwable ex = e.getCause();
                    if (ex.getCause() != null) {
                        ex = ex.getCause();
                    }
                    uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), ex);
                }
            }
        });
        scheduler.start();
    }

    public synchronized String scheduleJob(@NonNull Component component,
                                           @NonNull RepeatableTrigger trigger,
                                           @NonNull Supplier<Job> jobFactory) throws PlatformException {
        int repeatCount = trigger.getRepeatCount() == RepeatableTrigger.REPEAT_INDEFINITELY ?
                SimpleTrigger.REPEAT_INDEFINITELY : trigger.getRepeatCount();
        return scheduleJob(component, jobFactory, (groupName, jobName) -> TriggerBuilder.newTrigger()
                .withIdentity(jobName, groupName)
                .startAt(Date.from(trigger.getStartTime()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMilliseconds((int) trigger.getRepeatInterval().toMillis())
                        .withRepeatCount(repeatCount))
                .build());
    }

    public synchronized String scheduleJob(@NonNull Component component,
                                           @NonNull CronTrigger trigger,
                                           @NonNull Supplier<Job> jobFactory) throws PlatformException {
        return scheduleJob(component, jobFactory, (groupName, jobName) -> TriggerBuilder.newTrigger()
                .withIdentity(jobName, groupName)
                .withSchedule(CronScheduleBuilder.cronSchedule(trigger.getCronExpression())
                        .inTimeZone(trigger.getTimeZone()))
                .build());
    }

    public synchronized void removeJob(@NonNull Component component, @NonNull String jobName) throws PlatformException {
        String groupName = getGroupName(component);
        SchedulerJob.removeJob(groupName, jobName);
        try {
            scheduler.unscheduleJob(new TriggerKey(jobName, groupName));
        } catch (SchedulerException e) {
            throw GeneralExceptionBuilder.buildSchedulerException(e);
        }
    }

    public synchronized void clearJobs(@NonNull Component component) throws PlatformException {
        clearJobs(getGroupName(component));
    }

    public synchronized @NonNull Collection<Job> getJobs(@NonNull Component component) {
        String groupName = getGroupName(component);
        return SchedulerJob.getJobs(groupName);
    }

    public synchronized void shutdownAwait() throws PlatformException {
        for (String groupName : SchedulerJob.getGroups()) {
            clearJobs(groupName);
        }
        try {
            scheduler.shutdown(true);
        } catch (SchedulerException e) {
            throw GeneralExceptionBuilder.buildSchedulerException(e);
        }
    }

    private String scheduleJob(@NonNull Component component,
                               @NonNull Supplier<Job> jobFactory,
                               @NonNull BiFunction<String, String, Trigger> triggerGetter) throws PlatformException {
        String groupName = getGroupName(component);
        String jobName = buildJobName();
        SchedulerJob.putJob(component, groupName, jobName, jobFactory);
        JobBuilder jobBuilder = JobBuilder
                .newJob(SchedulerJob.class)
                .withIdentity(jobName, groupName);
        try {
            scheduler.scheduleJob(jobBuilder.build(), triggerGetter.apply(groupName, jobName));
        } catch (SchedulerException e) {
            throw GeneralExceptionBuilder.buildSchedulerException(e);
        }
        return jobName;
    }

    private void clearJobs(@NonNull String groupName) throws PlatformException {
        SchedulerJob.removeJobs(groupName);
        try {
            for (TriggerKey key : scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(groupName))) {
                scheduler.unscheduleJob(key);
            }
        } catch (SchedulerException e) {
            throw GeneralExceptionBuilder.buildSchedulerException(e);
        }
    }

    private String getGroupName(Component component) {
        return String.valueOf(component.getId());
    }

    private String buildJobName() {
        return String.valueOf(jobId.incrementAndGet());
    }
}

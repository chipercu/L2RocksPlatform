package com.fuzzy.subsystems.scheduler.internal;

import com.fuzzy.main.Subsystems;
import com.infomaximum.platform.querypool.QueryFuture;
import com.infomaximum.platform.sdk.component.Component;
import com.fuzzy.subsystems.scheduler.Job;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public class SchedulerJob implements org.quartz.Job {

    private static final Map<String, JobGroup> jobGroups = new ConcurrentHashMap<>();

    public static void putJob(@NonNull Component component,
                              @NonNull String jobGroupName,
                              @NonNull String jobName,
                              @NonNull Supplier<Job> jobFactory) {
        JobGroup jobGroup = jobGroups.computeIfAbsent(jobGroupName,
                s -> new JobGroup(component, new ConcurrentHashMap<>()));
        jobGroup.jobs.put(jobName, jobFactory);
    }

    public static void removeJob(@NonNull String jobGroupName, @NonNull String jobName) {
        JobGroup jobGroup = jobGroups.get(jobGroupName);
        if (jobGroup != null) {
            jobGroup.jobs.remove(jobName);
        }
    }

    public static void removeJobs(@NonNull String jobGroupName) {
        jobGroups.remove(jobGroupName);
    }

    public static @NonNull Collection<Job> getJobs(@NonNull String jobGroupName) {
        Collection<Job> jobs = new ArrayList<>();
        JobGroup jobGroup = jobGroups.get(jobGroupName);
        if (jobGroup != null) {
            for (Supplier<Job> jobFactory : jobGroup.jobs.values()) {
                jobs.add(jobFactory.get());
            }
        }
        return jobs;
    }

    public static @NonNull Set<String> getGroups() {
        return new HashSet<>(jobGroups.keySet());
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobKey jobKey = context.getJobDetail().getKey();
        JobGroup jobGroup = jobGroups.get(jobKey.getGroup());
        if (jobGroup != null) {
            Supplier<Job> jobFactory = jobGroup.jobs.get(jobKey.getName());
            if (jobFactory != null) {
                Job job = jobFactory.get();
                QueryFuture<Void> queryFuture = Subsystems.getInstance().getQueryPool().execute(jobGroup.component, job);
                job.postAction(queryFuture);
            }
        }
    }

    private record JobGroup(@NonNull Component component, ConcurrentMap<String, Supplier<Job>> jobs) {
    }
}

package com.fuzzy.subsystems.scheduler;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.sdk.component.Component;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;
import java.util.function.Supplier;

public class SubsystemScheduler {

	private final Component component;
    private final Scheduler scheduler;

	public SubsystemScheduler(@NonNull Component component, @NonNull Scheduler scheduler) {
		this.component = component;
        this.scheduler = scheduler;
    }

    public String scheduleJob(RepeatableTrigger trigger, Supplier<Job> jobFactory) throws PlatformException {
        return scheduler.scheduleJob(component, trigger, jobFactory);
    }

    public String scheduleJob(CronTrigger trigger, Supplier<Job> jobFactory) throws PlatformException {
        return scheduler.scheduleJob(component, trigger, jobFactory);
    }

    public void removeJob(String jobName) throws PlatformException {
        scheduler.removeJob(component, jobName);
    }

    public void clearJobs() throws PlatformException {
        scheduler.clearJobs(component);
    }

    public @NonNull Collection<Job> getJobs() {
        return scheduler.getJobs(component);
    }
}

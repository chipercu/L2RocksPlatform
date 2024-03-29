package com.fuzzy.platform.service.detectresource;

import com.fuzzy.main.Subsystems;
import com.fuzzy.main.SubsystemsConfig;
import com.infomaximum.platform.exception.PlatformException;
import com.fuzzy.platform.service.detectresource.observer.ResourceObservable;
import com.fuzzy.platform.service.detectresource.resourcemonitor.ResourceMonitor;
import com.fuzzy.platform.service.detectresource.resourcemonitor.ResourceMonitorBuilder;
import com.fuzzy.platform.service.detectresource.resourcemonitor.ResourceMonitorContext;
import com.fuzzy.platform.service.detectresource.resourcemonitor.cpuresourcesmonitor.CpuHostResourcesMonitor;
import com.fuzzy.platform.service.detectresource.resourcemonitor.cpuresourcesmonitor.CpuJvmResourcesMonitor;
import com.fuzzy.platform.service.detectresource.resourcemonitor.diskresourcesmonitor.DiskHostResourceMonitor;
import com.fuzzy.platform.service.detectresource.resourcemonitor.diskresourcesmonitor.DiskJvmResourceMonitor;
import com.fuzzy.platform.service.detectresource.resourcemonitor.memoryresourcesmonitor.MemoryHostResourceMonitor;
import com.fuzzy.platform.service.detectresource.resourcemonitor.memoryresourcesmonitor.MemoryJvmResourceMonitor;
import com.infomaximum.platform.utils.DefaultThreadFactory;
import com.fuzzy.subsystem.core.CoreSubsystem;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class DetectLowResourceService implements Callable<Void> {
    private final ResourceObservable resourceObservable;
    private static final Map<ResourceMonitor, Instant> monitorTimes = new ConcurrentHashMap<>();
    private final LazyMonitorThreadExecutor monitorsExecutorService;
    private Duration startDelayMillis;

    public DetectLowResourceService(ResourceObservable observable,
                                    Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        resourceObservable = observable;
        monitorsExecutorService = new LazyMonitorThreadExecutor(
                1,
                new DefaultThreadFactory("MonitorsScheduledThreadPool", uncaughtExceptionHandler));
    }

    public void start(Duration startDelayMillis) {
        this.startDelayMillis = startDelayMillis;
        monitorsExecutorService.schedule(this, startDelayMillis.toMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public Void call() throws PlatformException {
        final Instant start = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        executeMonitors(getExecutedMonitors(start), start);
        monitorsExecutorService.schedule(this, getNextSleepMills(start), TimeUnit.MILLISECONDS);
        return null;
    }

    private void executeMonitors(List<ResourceMonitor> executedMonitors, Instant start) throws PlatformException {
        for (ResourceMonitor executedMonitor : executedMonitors) {
            ResourceMonitorContext check = executedMonitor.scan();
            resourceObservable.notifyObservers(check.getEventType().name(),
                    check.getUuid(),
                    check.getMessage(),
                    check.getTtl(),
                    check.getEvent(),
                    check.getParams());
            updateMonitorRunTime(executedMonitor, start);
        }
    }

    private void updateMonitorRunTime(ResourceMonitor executedMonitor, Instant start) {
        monitorTimes.computeIfPresent(executedMonitor, (resourceMonitor, instant) -> start);
    }

    private List<ResourceMonitor> getExecutedMonitors(Instant start) {
        return monitorTimes.entrySet()
                .stream()
                .filter(entry -> {
                            final Duration interval = entry.getKey()
                                    .getParameters()
                                    .getPeriod();
                            return !entry.getValue()
                                    .plusMillis(interval.toMillis())
                                    .truncatedTo(ChronoUnit.SECONDS)
                                    .isAfter(start);
                        }
                ).map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private long getNextSleepMills(Instant start) {
        return monitorTimes.entrySet().stream()
                .map(entry -> {
                    final ResourceMonitor monitor = entry.getKey();
                    final Instant lastExecuteTime = entry.getValue();
                    final Duration period = monitor.getParameters().getPeriod();
                    return Duration.between(start, lastExecuteTime.plusMillis(period.toMillis()));
                }).mapToLong(Duration::toMillis)
                .min()
                .orElse(startDelayMillis.toMillis());
    }

    public void onDestroy() {
        if (!monitorsExecutorService.isTerminated()) {
            monitorsExecutorService.shutdownNow();
        }
    }

    private static ResourceMonitor[] createResourceMonitorList(String osName) {
        SubsystemsConfig subsystemsConfig = Subsystems.getInstance().getConfig();
        if (StringUtils.containsIgnoreCase(osName, "windows")) {
            return new ResourceMonitor[]{
                    new DiskHostResourceMonitor(subsystemsConfig.getDataDir(), ResourceMonitorBuilder.newBuilder()
                            .withPeriod(Duration.ofMinutes(10))
                            .withCondition(diskFreeSpaceGb -> diskFreeSpaceGb.doubleValue() < 10D)
                            .withTtl(Duration.ofMinutes(30))
                            .withUUID(CoreSubsystem.INFO.getUuid())),
                    new DiskHostResourceMonitor(subsystemsConfig.getWorkDir(), ResourceMonitorBuilder.newBuilder()
                            .withPeriod(Duration.ofMinutes(10))
                            .withCondition(diskFreeSpaceGb -> diskFreeSpaceGb.doubleValue() < 10D)
                            .withTtl(Duration.ofMinutes(30))
                            .withUUID(CoreSubsystem.INFO.getUuid())),
                    new CpuHostResourcesMonitor(ResourceMonitorBuilder.newBuilder()
                            .withPeriod(Duration.ofSeconds(5))
                            .withCondition(cpuPercentLoad -> cpuPercentLoad.doubleValue() > 60D)
                            .withTtl(Duration.ofMinutes(30))
                            .withUUID(CoreSubsystem.INFO.getUuid())),
                    new MemoryHostResourceMonitor(ResourceMonitorBuilder.newBuilder()
                            .withPeriod(Duration.ofMinutes(5))
                            .withCondition(memoryPercentUsage -> memoryPercentUsage.doubleValue() > 80D)
                            .withTtl(Duration.ofMinutes(30))
                            .withUUID(CoreSubsystem.INFO.getUuid()))};
        }
        return new ResourceMonitor[]{
                new DiskJvmResourceMonitor(subsystemsConfig.getDataDir(), ResourceMonitorBuilder.newBuilder()
                        .withPeriod(Duration.ofMinutes(10))
                        .withCondition(diskFreeSpaceGb -> diskFreeSpaceGb.doubleValue() < 10D)
                        .withTtl(Duration.ofMinutes(30))
                        .withUUID(CoreSubsystem.INFO.getUuid())),
                new DiskJvmResourceMonitor(subsystemsConfig.getWorkDir(), ResourceMonitorBuilder.newBuilder()
                        .withPeriod(Duration.ofMinutes(10))
                        .withCondition(diskFreeSpaceGb -> diskFreeSpaceGb.doubleValue() < 10D)
                        .withTtl(Duration.ofMinutes(30))
                        .withUUID(CoreSubsystem.INFO.getUuid())),
                new CpuJvmResourcesMonitor(ResourceMonitorBuilder.newBuilder()
                        .withPeriod(Duration.ofSeconds(5))
                        .withCondition(cpuPercentLoad -> cpuPercentLoad.doubleValue() > 60D)
                        .withTtl(Duration.ofMinutes(30))
                        .withUUID(CoreSubsystem.INFO.getUuid())),
                new MemoryJvmResourceMonitor(ResourceMonitorBuilder.newBuilder()
                        .withPeriod(Duration.ofMinutes(5))
                        .withCondition(memoryPercentUsage -> memoryPercentUsage.doubleValue() > 80D)
                        .withTtl(Duration.ofMinutes(30))
                        .withUUID(CoreSubsystem.INFO.getUuid()))};

    }

    private static class LazyMonitorThreadExecutor extends ScheduledThreadPoolExecutor {
        private final Logger log;

        public LazyMonitorThreadExecutor(int corePoolSize, ThreadFactory threadFactory) {
            super(corePoolSize, threadFactory);
            log = LoggerFactory.getLogger(LazyMonitorThreadExecutor.class);
        }

        @Override
        protected void beforeExecute(Thread t, Runnable r) {
            super.beforeExecute(t, r);
            if (monitorTimes.isEmpty()) {
                ResourceMonitor[] resourceMonitorList = createResourceMonitorList(System.getProperty("os.name"));
                for (ResourceMonitor resourceMonitor : resourceMonitorList) {
                    monitorTimes.putIfAbsent(resourceMonitor, Instant.EPOCH);
                }
                log.info("ResourceMonitor are generated.");
            }
        }
    }
}
package com.fuzzy.subsystems.dump;

//import com.sun.management.HotSpotDiagnosticMXBean;

//import java.lang.management.ManagementFactory;


/**
 * Есть вариант с потоковой обработкой(без создания временного файла)
 * но у него есть минус необходимо протащить в сборку: provided files(org.gradle.internal.jvm.Jvm.current().toolsJar)
 * и необходимо решить проблему с тем, что tar должен знать размер файла до начала его загрузки
 * <p>
 * HotSpotVirtualMachine vm = (HotSpotVirtualMachine) VirtualMachine.attach(ProcessInfoUtils.getPID());
 * try (InputStream in = vm.remoteDataDump()) {
 * try (OutputStream fos = Files.newOutputStream(file)) {
 * IOUtils.copy(in, fos);
 * }
 * } finally {
 * vm.detach();
 * }
 */
class HeapDumpBuilder {

    private static final String HOTSPOT_BEAN_NAME = "com.sun.management:type=HotSpotDiagnostic";
/*

    private static volatile HotSpotDiagnosticMXBean hotspotMBean;

    protected static void dumpHeap(Path file) throws IOException {
        initHotspotMBean();
        hotspotMBean.dumpHeap(file.normalize().toString(), true);
    }

    private static void initHotspotMBean() throws IOException {
        if (hotspotMBean == null) {
            synchronized (HeapDumpBuilder.class) {
                if (hotspotMBean == null) {
                    hotspotMBean = ManagementFactory.newPlatformMXBeanProxy(
                            ManagementFactory.getPlatformMBeanServer(),
                            HOTSPOT_BEAN_NAME,
                            HotSpotDiagnosticMXBean.class
                    );
                }
            }
        }
    }
*/

}

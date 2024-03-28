package com.fuzzy.main.cluster.core.service.transport.network.grpc.internal.service.remotecontroller;

import com.fuzzy.main.cluster.core.service.transport.executor.ComponentExecutorTransport;
import com.fuzzy.main.cluster.core.service.transport.network.RemoteControllerRequest;
import com.fuzzy.main.cluster.core.service.transport.network.grpc.internal.GrpcNetworkTransitImpl;
import com.fuzzy.main.cluster.core.service.transport.network.grpc.internal.channel.Channel;
import com.fuzzy.main.cluster.core.service.transport.network.grpc.internal.channel.ChannelImpl;
import com.fuzzy.main.cluster.core.service.transport.network.grpc.internal.utils.PackageLog;
import com.fuzzy.main.cluster.struct.Component;
import com.google.protobuf.ByteString;
import com.infomaximum.cluster.core.service.transport.network.grpc.struct.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GrpcRemoteControllerRequest implements RemoteControllerRequest {

    private final static Logger log = LoggerFactory.getLogger(GrpcRemoteControllerRequest.class);

    private final static long TIME_CLEAR_REQUEST_EXECUTE = Duration.ofHours(2).toMillis();

    private final GrpcNetworkTransitImpl grpcNetworkTransit;

    private final AtomicInteger ids;
    private final ConcurrentHashMap<Integer, NetRequest> requests;

    private final ScheduledExecutorService scheduledServiceWaitNetExecute;

    private final ConcurrentHashMap<WaitLocalExecute, WaitLocalExecuteResult> waitLocalExecuteRequest;
    private final ScheduledExecutorService scheduledServiceWaitLocalExecute;

    public GrpcRemoteControllerRequest(GrpcNetworkTransitImpl grpcNetworkTransit) {
        this.grpcNetworkTransit = grpcNetworkTransit;
        this.ids = new AtomicInteger();
        this.requests = new ConcurrentHashMap<>();

        this.scheduledServiceWaitNetExecute = Executors.newSingleThreadScheduledExecutor();
        this.scheduledServiceWaitNetExecute.scheduleWithFixedDelay(() -> checkTimeoutRequest(), 1, grpcNetworkTransit.getTimeoutConfirmationWaitResponse().toMillis()/3, TimeUnit.MILLISECONDS);

        this.waitLocalExecuteRequest = new ConcurrentHashMap<>();
        this.scheduledServiceWaitLocalExecute = Executors.newSingleThreadScheduledExecutor();
        this.scheduledServiceWaitLocalExecute.scheduleWithFixedDelay(() -> sendWaitResponsePackets(), 1, grpcNetworkTransit.getTimeoutConfirmationWaitResponse().toMillis()/3, TimeUnit.MILLISECONDS);
    }

    private int nextPackageId() {
        return ids.updateAndGet(value -> (value == Integer.MAX_VALUE) ? 1 : value + 1);
    }

    @Override
    public ComponentExecutorTransport.Result request(Component sourceComponent, UUID targetNodeRuntimeId, int targetComponentId, String rControllerClassName, int methodKey, byte[][] args) throws Exception {
        int packageId = nextPackageId();
        CompletableFuture<PNetPackageResponse> completableFuture = new CompletableFuture<>();

        //Формируем пакет-запрос
        PNetPackageRequest.Builder builderPackageRequest = PNetPackageRequest.newBuilder()
                .setPackageId(packageId)
                .setTargetComponentId(targetComponentId)
                .setRControllerClassName(rControllerClassName)
                .setMethodKey(methodKey);
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                builderPackageRequest.addArgs(ByteString.copyFrom(args[i]));
            }
        }
        PNetPackage netPackage = PNetPackage.newBuilder().setRequest(builderPackageRequest).build();

        //Отправляем, исходим из того, что, может отправиться несколько копий пакета, на другой стороне нужна фильтрация
        requests.put(packageId, new NetRequest(targetNodeRuntimeId, targetComponentId, rControllerClassName, methodKey, new Timeout(countTimeFail()), completableFuture));
        try {
            grpcNetworkTransit.getChannels().sendPacketWithRepeat(targetNodeRuntimeId, netPackage);
        } catch (Exception e) {
            requests.remove(packageId);
            throw e;
        }

        PNetPackageResponse netPackageResponse = completableFuture.get();

        if (!netPackageResponse.getException().isEmpty()) {
            return new ComponentExecutorTransport.Result(null, netPackageResponse.getException().toByteArray());
        } else {
            return new ComponentExecutorTransport.Result(netPackageResponse.getResult().toByteArray(), null);
        }
    }

    public void handleIncomingPacket(PNetPackageResponse response) {
        NetRequest netRequest = requests.remove(response.getPackageId());
        if (netRequest == null) {
            log.debug("Incoming unknown package: {}", PackageLog.toString(response));
        } else {
            netRequest.completableFuture().complete(response);
        }
    }

    public void handleIncomingPacket(PNetPackageProcessing response) {
        NetRequest netRequest = requests.get(response.getPackageId());
        if (netRequest == null) {
            log.debug("Incoming unknown package: {}", PackageLog.toString(response));
        } else {
            netRequest.timeout().timeFail = countTimeFail();
        }
    }

    private long countTimeFail(){
        return System.currentTimeMillis() + grpcNetworkTransit.getTimeoutConfirmationWaitResponse().toMillis();
    }

    public void handleIncomingPacket(PNetPackageRequest request, ChannelImpl channel) {
        UUID remoteNodeRuntimeId = channel.remoteNode.node.getRuntimeId();
        int packageId = request.getPackageId();
        WaitLocalExecute waitLocalExecute = new WaitLocalExecute(remoteNodeRuntimeId, packageId);
        WaitLocalExecuteResult waitLocalExecuteResult = new WaitLocalExecuteResult();

        //Механизм проверки, что это не дублирующий пакет
        synchronized (waitLocalExecuteRequest) {
            if (waitLocalExecuteRequest.contains(waitLocalExecute)) {
                log.debug("Duplicate packet, ignore: {}", PackageLog.toString(request));
                return;
            }
            waitLocalExecuteRequest.put(waitLocalExecute, waitLocalExecuteResult);
        }


        byte[][] byteArgs = new byte[request.getArgsCount()][];
        for (int i = 0; i < byteArgs.length; i++) {
            byteArgs[i] = request.getArgs(i).toByteArray();
        }
        ComponentExecutorTransport.Result result = grpcNetworkTransit.transportManager.localRequest(
                request.getTargetComponentId(),
                request.getRControllerClassName(),
                request.getMethodKey(),
                byteArgs
        );

        //Ставим флаг, что запрос выполнился
        waitLocalExecuteResult.setEndTime(Instant.now());

        PNetPackageResponse.Builder responseBuilder = PNetPackageResponse.newBuilder()
                .setPackageId(packageId);
        if (result.exception() != null) {
            responseBuilder.setException(ByteString.copyFrom(result.exception()));
        } else {
            responseBuilder.setResult(ByteString.copyFrom(result.value()));
        }

        PNetPackage responseNetPackage = PNetPackage.newBuilder().setResponse(responseBuilder).build();
        try {
            grpcNetworkTransit.getChannels().sendPacketWithRepeat(remoteNodeRuntimeId, responseNetPackage);
        } catch (Exception e) {
            log.debug("Exception send response package: {}", PackageLog.toString(responseNetPackage));
        }
    }

    public void disconnectChannel(Channel channel) {
        for (Map.Entry<Integer, NetRequest> entry : requests.entrySet()) {
            NetRequest netRequest = entry.getValue();
            UUID remoteNodeRuntimeId = netRequest.targetNodeRuntimeId();
            //Если активных каналов больше нет - кидаем ошибку
            if (grpcNetworkTransit.getChannels().getChannel(remoteNodeRuntimeId) == null) {
                int packageId = entry.getKey();
                fireErrorNetworkRequest(packageId);
            }
        }
    }

    private void checkTimeoutRequest() {
        try {
            long now = System.currentTimeMillis();
            for (Map.Entry<Integer, NetRequest> entry : requests.entrySet()) {
                NetRequest netRequest = entry.getValue();
                UUID remoteNodeRuntimeId = netRequest.targetNodeRuntimeId();
                //Если активных каналов больше нет или вышел таймаут - кидаем ошибку
                if (grpcNetworkTransit.getChannels().getChannel(remoteNodeRuntimeId) == null || now > netRequest.timeout().timeFail) {
                    int packageId = entry.getKey();
                    fireErrorNetworkRequest(packageId);
                }
            }
        } catch (Throwable e) {
            grpcNetworkTransit.getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
        }
    }

    private void fireErrorNetworkRequest(int packageId) {
        NetRequest netRequest = requests.remove(packageId);
        if (netRequest == null) return;

        UUID remoteNodeRuntimeId = netRequest.targetNodeRuntimeId();
        Exception exception = grpcNetworkTransit.transportManager.getExceptionBuilder().buildTransitRequestException(
                remoteNodeRuntimeId, netRequest.componentId(), netRequest.rControllerClassName(), netRequest.methodKey(),
                new RuntimeException("Fire error network request, packageId: " + packageId)
        );

        PNetPackageResponse pNetPackageResponse = PNetPackageResponse.newBuilder()
                .setPackageId(packageId)
                .setException(ByteString.copyFrom(
                        grpcNetworkTransit.transportManager.getRemotePackerObject().serialize(null, Throwable.class, exception)
                )).build();

        netRequest.completableFuture().complete(pNetPackageResponse);
    }

    private void sendWaitResponsePackets(){
        long cleaningTime = System.currentTimeMillis() + TIME_CLEAR_REQUEST_EXECUTE;
        for (Map.Entry<WaitLocalExecute, WaitLocalExecuteResult> entry : waitLocalExecuteRequest.entrySet()) {
            WaitLocalExecute waitLocalExecute = entry.getKey();
            WaitLocalExecuteResult waitLocalExecuteResult = entry.getValue();
            if (waitLocalExecuteResult.getEndTime() == null) {
                try {
                    PNetPackageProcessing pNetPackageProcessing = PNetPackageProcessing.newBuilder()
                            .setPackageId(waitLocalExecute.packageId())
                            .build();
                    PNetPackage pNetPackage = PNetPackage.newBuilder().setResponseProcessing(pNetPackageProcessing).build();
                    grpcNetworkTransit.getChannels().sendPacket(waitLocalExecute.nodeRuntimeId(), pNetPackage, 1);
                } catch (Exception ignore) {}
            } else if (waitLocalExecuteResult.getEndTime().toEpochMilli() < cleaningTime) {
                waitLocalExecuteRequest.remove(waitLocalExecute);
            }
        }
    }

    public void close() {
        scheduledServiceWaitNetExecute.shutdown();
        scheduledServiceWaitLocalExecute.shutdown();
    }
}

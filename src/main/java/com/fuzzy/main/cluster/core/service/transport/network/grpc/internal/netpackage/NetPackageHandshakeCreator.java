package com.fuzzy.main.cluster.core.service.transport.network.grpc.internal.netpackage;

import com.fuzzy.main.cluster.Cluster;
import com.fuzzy.main.cluster.core.component.RuntimeComponentInfo;
import com.infomaximum.cluster.core.service.transport.network.grpc.internal.GrpcNetworkTransitImpl;
import com.fuzzy.main.cluster.core.service.transport.network.local.LocalManagerRuntimeComponent;
import com.fuzzy.main.cluster.struct.Component;
import com.infomaximum.cluster.core.service.transport.network.grpc.struct.*;

import java.util.UUID;
import java.util.stream.Collectors;

public class NetPackageHandshakeCreator {

    //TODO не оптимально
    public static PNetPackage createResponse(GrpcNetworkTransitImpl grpcNetworkTransit) {
        PNetPackageHandshakeNode handshakeNode = buildHandshakeNode(grpcNetworkTransit);
        PNetPackageHandshakeResponse packageHandshake = PNetPackageHandshakeResponse.newBuilder()
                .setNode(handshakeNode)
                .build();
        return PNetPackage.newBuilder().setHandshakeResponse(packageHandshake).build();
    }

    public static PNetPackage createRequest(GrpcNetworkTransitImpl grpcNetworkTransit, UUID channelUuid) {
        PNetPackageHandshakeNode handshakeNode = buildHandshakeNode(grpcNetworkTransit);
        PNetPackageHandshakeRequest packageHandshake = PNetPackageHandshakeRequest.newBuilder()
                .setChannelIdMostSigBits(channelUuid.getMostSignificantBits())
                .setChannelIdLeastSigBit(channelUuid.getLeastSignificantBits())
                .setNode(handshakeNode)
                .build();
        return PNetPackage.newBuilder().setHandshakeRequest(packageHandshake).build();
    }

    private static PNetPackageHandshakeNode buildHandshakeNode(GrpcNetworkTransitImpl grpcNetworkTransit) {
        UUID nodeRuntimeId = grpcNetworkTransit.getNode().getRuntimeId();

        PNetPackageHandshakeNode.Builder nodeBuilder = PNetPackageHandshakeNode.newBuilder()
                .setName(grpcNetworkTransit.getNode().getName())
                .setRuntimeIdMostSigBits(nodeRuntimeId.getMostSignificantBits())
                .setRuntimeIdLeastSigBits(nodeRuntimeId.getLeastSignificantBits());

        Cluster cluster = grpcNetworkTransit.transportManager.cluster;
        for(Component component: cluster.getLocalComponents()) {
            nodeBuilder.addPNetPackageComponents(buildPackageComponent(component));
        }

        return nodeBuilder.build();
    }

    public static PNetPackageComponent buildPackageComponent(Component component) {
        return PNetPackageComponent.newBuilder()
                .setUuid(component.getInfo().getUuid())
                .setId(component.getId())
                .addAllClassNameRControllers(
                        component.getTransport().getExecutor().getClassRControllers().stream().map(it -> it.getName()).collect(Collectors.toList())
                )
                .build();
    }

    public static PNetPackage buildPacketUpdateNode(LocalManagerRuntimeComponent localManagerRuntimeComponent) {
        PNetPackageUpdateNode.Builder updateBuilder = PNetPackageUpdateNode.newBuilder();
        for (RuntimeComponentInfo runtimeComponentInfo : localManagerRuntimeComponent.getComponents()) {
            updateBuilder.addPNetPackageComponents(NetPackageHandshakeCreator.buildPackageComponent(runtimeComponentInfo));
        }
        return PNetPackage.newBuilder().setUpdateNode(updateBuilder).build();
    }

    public static PNetPackageComponent buildPackageComponent(RuntimeComponentInfo runtimeComponentInfo) {
        return PNetPackageComponent.newBuilder()
                .setUuid(runtimeComponentInfo.uuid)
                .setId(runtimeComponentInfo.id)
                .addAllClassNameRControllers(runtimeComponentInfo.getClassNameRControllers())
                .build();
    }
}

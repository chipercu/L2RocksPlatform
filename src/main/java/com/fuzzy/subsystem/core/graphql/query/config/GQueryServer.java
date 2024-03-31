package com.fuzzy.subsystem.core.graphql.query.config;

import com.fuzzy.cluster.Cluster;
import com.fuzzy.cluster.Node;
import com.fuzzy.cluster.component.manager.ManagerComponent;
import com.fuzzy.cluster.core.component.RuntimeComponentInfo;
import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.core.service.transport.network.LocationRuntimeComponent;
import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.cluster.struct.Component;
import com.fuzzy.main.Subsystems;
import com.fuzzy.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.config.CoreConfigDescription;
import com.fuzzy.subsystem.core.config.ServerStatus;
import com.fuzzy.subsystem.core.graphql.query.GCoreObjectConfigQuery;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.subsystem.Subsystem;
import com.fuzzy.subsystems.utils.ManifestUtils;
import net.minidev.json.parser.ParseException;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@GraphQLTypeOutObject("server")
public class GQueryServer {

    @GraphQLField
    @GraphQLAuthControl(UnauthorizedContext.class)
    @GraphQLDescription("Статус сервера")
    public static GraphQLQuery<RemoteObject, ServerStatus> getStatus() throws PlatformException {
        return new GCoreObjectConfigQuery<>(CoreConfigDescription.SERVER_STATUS);
    }

    @GraphQLField
    @GraphQLAuthControl(UnauthorizedContext.class)
    @GraphQLDescription("Активные подсистемы")
    public static ArrayList<GSubsystemInfo> getActiveSubsystem() {
        Map<String, String> modules = new HashMap<>();

        Cluster cluster = Subsystems.getInstance().getCluster();
        ManagerComponent managerComponent = cluster.getAnyLocalComponent(ManagerComponent.class);
        for (RuntimeComponentInfo runtimeComponentInfo : managerComponent.getRegisterComponent().getLocalComponents()) {
            String uuid = runtimeComponentInfo.uuid;
            Component component = cluster.getAnyLocalComponent(uuid);
            if (component != null) {
                if (!(component instanceof Subsystem)) continue;
                Subsystem subsystem = (Subsystem) component;
                modules.put(uuid, subsystem.getInfo().getVersion().toString());
            } else if (!modules.containsKey(uuid)) {
                modules.put(uuid, "Unknown");
            }
        }

        List<Node> remoteNodes = cluster.getRemoteNodes();
        if (!remoteNodes.isEmpty()) {
            for (Node node: remoteNodes) {
                for (LocationRuntimeComponent runtimeComponent: managerComponent.getRegisterComponent().getLocationRuntimeComponents(node.getRuntimeId())) {
                    String uuid = runtimeComponent.component().uuid;
                    modules.putIfAbsent(uuid, "Unknown");
                }
            }
        }

        ArrayList<GSubsystemInfo> subsystemInfos = modules.entrySet().stream()
                .map(entry -> new GSubsystemInfo(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(GSubsystemInfo::getUuid))
                .collect(Collectors.toCollection(ArrayList::new));
        return subsystemInfos;
    }

    @GraphQLField
    @GraphQLAuthControl(UnauthorizedContext.class)
    @GraphQLDescription("Серверное время")
    public static Instant getTime() {
        return Instant.now();
    }

    @GraphQLField
    @GraphQLAuthControl(UnauthorizedContext.class)
    @GraphQLDescription("Версия сборки")
    public static String getBuild() throws PlatformException {
        try {
            return ManifestUtils.readBuildVersion();
        } catch (IOException e) {
            throw GeneralExceptionBuilder.buildIOErrorException(e);
        } catch (ParseException e) {
            throw GeneralExceptionBuilder.buildInvalidJsonException(e);
        }
    }
}

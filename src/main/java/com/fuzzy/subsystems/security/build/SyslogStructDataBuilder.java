package com.fuzzy.subsystems.security.build;

import com.infomaximum.cluster.graphql.struct.GRequest;
import com.infomaximum.platform.component.frontend.authcontext.UnauthorizedContext;
import com.infomaximum.platform.component.frontend.context.source.SourceGRequestAuth;
import com.infomaximum.platform.sdk.context.Context;
import com.infomaximum.platform.sdk.context.source.Source;
import com.infomaximum.platform.sdk.context.source.SourceSystem;
import com.fuzzy.subsystem.core.authcontext.employee.EmployeeSessionAuthContext;
import com.fuzzy.subsystem.core.authcontext.system.ApiKeyAuthContext;
import com.fuzzy.subsystems.security.struct.data.source.SyslogStructDataSource;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SyslogStructDataBuilder {

    private static List<Builder> builders = new ArrayList<>();

    public static void registry(Builder builder) {
        builders.add(builder);
    }

    public static SyslogStructDataSource build(@NonNull Context context) {
        final Source source = context.getSource();
        if (source instanceof SourceSystem) {
            return build(context, (SourceSystem) source);
        } else if (source instanceof SourceGRequestAuth) {
            return build(context, (SourceGRequestAuth) source);
        } else {
            throw new RuntimeException("Not support type: " + source);
        }
    }

    public static SyslogStructDataSource build(@NonNull Context context, SourceGRequestAuth source) {
        GRequest request = source.getRequest();
        UnauthorizedContext authContext = source.getAuthContext();
        SyslogStructDataSource syslogStructDataSource = build(authContext);
        String remoteAddress = request.getRemoteAddress().rawRemoteAddress;
        String remoteProxy = remoteAddress.equals(request.getRemoteAddress().endRemoteAddress) ?
                null : request.getRemoteAddress().endRemoteAddress;
        syslogStructDataSource.addParam("remote_address", remoteAddress);
        syslogStructDataSource.addParam("trace", context.getTrace());
        if (remoteProxy != null) {
            syslogStructDataSource.addParam("remote_proxy", remoteProxy);
        }
        return syslogStructDataSource;
    }

    public static SyslogStructDataSource build(@NonNull Context context, SourceSystem source) {
        final SyslogStructDataSource syslogStructDataSource = new SyslogStructDataSource("system");
        syslogStructDataSource.addParam("trace", context.getTrace());
        return syslogStructDataSource;
    }

    private static SyslogStructDataSource build(UnauthorizedContext authContext) {
        if (authContext.getClass() == UnauthorizedContext.class) {
            return new SyslogStructDataSource("anonymous");
        } else if (authContext instanceof EmployeeSessionAuthContext employeeSessionAuthContext) {

            SyslogStructDataSource syslogStructDataSource = new SyslogStructDataSource("employee");
            if (employeeSessionAuthContext.getParams() != null) {
                for (Map.Entry<String, String> mapEntry : employeeSessionAuthContext.getParams().entrySet()) {
                    syslogStructDataSource.addParam(mapEntry.getKey(), mapEntry.getValue());
                }
            }
            return syslogStructDataSource;
        } else if (authContext instanceof ApiKeyAuthContext apiKeyAuthContext) {

            SyslogStructDataSource syslogStructDataSource = new SyslogStructDataSource("api_key");
            if (apiKeyAuthContext.getParams() != null) {
                apiKeyAuthContext.getParams().forEach(syslogStructDataSource::addParam);
            }
            return syslogStructDataSource;
        } else {
            Builder builder = null;
            for (Builder item : builders) {
                if (item.isSupport(authContext)) {
                    builder = item;
                    break;
                }
            }
            if (builder != null) {
                return builder.build(authContext);
            } else {
                throw new RuntimeException("Unknown auth context: " + authContext);
            }
        }
    }

    public static abstract class Builder {

        public abstract boolean isSupport(UnauthorizedContext authContext);

        public abstract SyslogStructDataSource build(UnauthorizedContext authContext);

    }
}

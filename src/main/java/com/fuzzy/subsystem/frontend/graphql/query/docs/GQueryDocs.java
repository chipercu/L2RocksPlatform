package com.fuzzy.subsystem.frontend.graphql.query.docs;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLField;
import com.infomaximum.cluster.graphql.anotation.GraphQLName;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.platform.component.frontend.authcontext.UnauthorizedContext;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.infomaximum.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.frontend.FrontendSubsystem;
import com.fuzzy.subsystem.frontend.access.FrontendPrivilege;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.graphql.query.GAccessQuery;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

@GraphQLTypeOutObject("docs")
public class GQueryDocs {

    private static final String PATH = "path";

    @GraphQLField
    @GraphQLAuthControl(UnauthorizedContext.class)
    @GraphQLDescription("Документация")
    public static GraphQLQuery<RemoteObject, String> getDocs(
            FrontendSubsystem frontendSubsystem,
            @GraphQLDescription("Путь до раздела")
            @NonNull @GraphQLName(PATH) final String path
    ) {
        final GraphQLQuery<RemoteObject, String> query = new GraphQLQuery<>() {

            @Override
            public void prepare(ResourceProvider resources) {

            }

            @Override
            public String execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                Path filePath = frontendSubsystem.getDocsDirPath().resolve(path);
                if (!filePath.isAbsolute() || !Files.exists(filePath) || Files.isDirectory(filePath)) {
                    throw GeneralExceptionBuilder.buildInvalidValueException(PATH);
                }

                try (BufferedReader br = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
                    return br.lines().collect(Collectors.joining("\n"));
                } catch (IOException e) {
                    throw GeneralExceptionBuilder.buildIOErrorException(e);
                }
            }
        };
        return new GAccessQuery<>(query, FrontendPrivilege.DOCUMENTATION_ACCESS, AccessOperation.READ);
    }
}

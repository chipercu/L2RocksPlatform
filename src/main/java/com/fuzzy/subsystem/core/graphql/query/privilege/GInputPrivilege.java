package com.fuzzy.subsystem.core.graphql.query.privilege;

import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLName;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeInput;
import com.fuzzy.subsystem.core.remote.accessroleprivileges.PrivilegeValue;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

@GraphQLTypeInput("input_privilege")
public class GInputPrivilege {

    private final String key;
    private final ArrayList<AccessOperation> operations;

    public GInputPrivilege(
            @GraphQLDescription("Ключ")
            @NonNull @GraphQLName("key") String key,
            @GraphQLDescription("Операции")
            @NonNull @GraphQLName("operations") ArrayList<AccessOperation> operations) {
        this.key = key;
        this.operations = operations;
    }

    public @NonNull String getKey() {
        return key;
    }

    public @NonNull ArrayList<AccessOperation> getOperations() {
        return operations;
    }

    public static @NonNull PrivilegeValue[] convert(@NonNull ArrayList<GInputPrivilege> privileges) {
        PrivilegeValue[] privilegeValues = new PrivilegeValue[privileges.size()];
        for (int i = 0; i < privileges.size(); i++) {
            GInputPrivilege gInputPrivilege = privileges.get(i);
            privilegeValues[i] = new PrivilegeValue(
                    gInputPrivilege.getKey(),
                    new AccessOperationCollection(gInputPrivilege.getOperations()
                            .toArray(new AccessOperation[0]))
            );
        }
        return privilegeValues;
    }
}

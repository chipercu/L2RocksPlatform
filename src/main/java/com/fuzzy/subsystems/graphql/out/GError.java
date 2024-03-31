package com.fuzzy.subsystems.graphql.out;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.sdk.graphql.annotation.GraphQLAuthControl;

import java.util.ArrayList;
import java.util.Map;

@GraphQLTypeOutObject("error")
public class GError implements RemoteObject {

    private String code;
    private String comment;
    private ArrayList<GKeyValue> parameters;

    public GError(PlatformException exception) {
        code = exception.getCode();
        comment = exception.getComment();
        parameters = new ArrayList<>();
        if (exception.getParameters() != null) {
            for (Map.Entry<String, Object> entry : exception.getParameters().entrySet()) {
                parameters.add(new GKeyValue(entry.getKey(), String.valueOf(entry.getValue())));
            }
        }
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Код")
    public String getCode() {
        return code;
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Комментарий")
    public String getComment() {
        return comment;
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Параметры")
    public ArrayList<GKeyValue> getParameters() {
        return parameters;
    }
}

package com.fuzzy.subsystem.frontend.graphql.query;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.main.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.graphql.query.employee.GEmployee;
import com.fuzzy.subsystem.core.remote.logon.AuthStatus;
import com.fuzzy.subsystems.graphql.query.GPrimaryKeyQuery;

/**
 * Created by kris on 26.01.17.
 */
@GraphQLTypeOutObject("session")
public class GSession implements RemoteObject {

	@GraphQLField(value = "uuid")
	@GraphQLAuthControl(UnauthorizedContext.class)
	@GraphQLDescription("Уникальный идентификатор")
	private final String id;

	private final Long authEmployeeId;

	@GraphQLField
	@GraphQLAuthControl(UnauthorizedContext.class)
	@GraphQLDescription("Статус авторизации")
	private final AuthStatus status;

	public GSession(String id, Long authEmployeeId, AuthStatus status) {
		this.id = id;
		this.authEmployeeId = authEmployeeId;
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public Long getAuthEmployeeId() {
		return authEmployeeId;
	}

	public AuthStatus getStatus() {
		return status;
	}

	@GraphQLField
	@GraphQLAuthControl({ UnauthorizedContext.class })
	@GraphQLDescription("Сотрудник")
	public static GraphQLQuery<GSession, GEmployee> getEmployee() {
		return new GPrimaryKeyQuery<GSession, EmployeeReadable, GEmployee>(EmployeeReadable.class, GEmployee::new) {
			@Override
			protected Long getIdentificator(GSession source, QueryTransaction transaction) {
				return source.authEmployeeId;
			}
		};
	}
}
package com.fuzzy.subsystem.core.graphql.query.department;

import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;
import com.fuzzy.subsystem.core.graphql.query.employee.GDepartmentEmployeeElement;
import com.fuzzy.subsystem.core.grouping.enumerator.DepartmentGroupingEnumerator;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.graphql.GDomainObject;
import com.fuzzy.subsystems.graphql.query.GAccessQuery;
import com.fuzzy.subsystems.graphql.query.GParentsQuery;
import com.fuzzy.subsystems.graphql.query.GPrimaryKeyQuery;
import com.fuzzy.subsystems.grouping.GroupingEnumerator;

import java.util.ArrayList;

@GraphQLTypeOutObject("department")
public class GDepartment extends GDomainObject<DepartmentReadable> implements GDepartmentEmployeeElement {

	public GDepartment(DepartmentReadable source) {
		super(source);
	}

	@Override
	public long getId() {
		return getSource().getId();
	}

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Название отдела")
	public static GraphQLQuery<GDepartment, String> getName() {
		return new GAccessQuery<>(s -> s.getSource().getName(), CorePrivilege.EMPLOYEES, AccessOperation.READ);
	}

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Родительский отдел")
	public static GraphQLQuery<GDepartment, GDepartment> getParentDepartment() {
		GraphQLQuery<GDepartment, GDepartment> query = new GPrimaryKeyQuery<GDepartment, DepartmentReadable, GDepartment>(
				DepartmentReadable.class, GDepartment::new) {
			@Override
			protected Long getIdentificator(GDepartment source, QueryTransaction transaction)
					throws PlatformException {
				return source.getSource().getParentDepartmentId();
			}
		};
		return new GAccessQuery<>(query, CorePrivilege.EMPLOYEES, AccessOperation.READ);
	}

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Цепочка родительских отделов")
	public static GraphQLQuery<GDepartment, ArrayList<GDepartment>> getParentDepartments() {
		GraphQLQuery<GDepartment, ArrayList<GDepartment>> query =
				new GParentsQuery<GDepartment, DepartmentReadable, GDepartment>(DepartmentReadable.class, GDepartment::new) {
			@Override
			protected GroupingEnumerator getGroupingEnumerator(ResourceProvider resources) {
				return new DepartmentGroupingEnumerator(resources);
			}

			@Override
			protected Long getParentId(GDepartment source, QueryTransaction transaction) throws PlatformException {
				return source.getSource().getParentDepartmentId();
			}
		};
		return new GAccessQuery<>(query, CorePrivilege.EMPLOYEES, AccessOperation.READ);
	}
}

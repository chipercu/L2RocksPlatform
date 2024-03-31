package com.fuzzy.subsystem.core.autocomplete;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.apikeyprivileges.ApiKeyPrivilegesGetter;
import com.fuzzy.subsystem.core.domainobject.apikey.ApiKeyReadable;
import com.fuzzy.subsystem.core.graphql.query.privilege.GInputPrivilege;
import com.fuzzy.subsystem.core.textfilter.ApiKeyTextFilterGetter;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import com.fuzzy.subsystems.autocomplete.AtomicAutocompleteImpl;

import java.util.ArrayList;
import java.util.HashMap;

public class ApiKeyAtomicAutocomplete extends AtomicAutocompleteImpl<ApiKeyReadable> {

	private ArrayList<GInputPrivilege> privilegeFilter = null;
	private final ApiKeyPrivilegesGetter apiKeyPrivilegesGetter;


	public ApiKeyAtomicAutocomplete(ResourceProvider resources) {
		super(
				new ApiKeyTextFilterGetter(resources),
				ApiKeyTextFilterGetter.FIELD_NAME,
				null
		);
		apiKeyPrivilegesGetter = new ApiKeyPrivilegesGetter(resources);
	}

	public void setPrivilegeFilter(ArrayList<GInputPrivilege> privilegeFilter) {
		this.privilegeFilter = privilegeFilter;
	}

	@Override
	protected boolean checkItem(ApiKeyReadable item, ContextTransaction<?> context) throws PlatformException {
		if (privilegeFilter == null) {
			return true;
		}
		HashMap<String, AccessOperationCollection> privileges =
				apiKeyPrivilegesGetter.getPrivileges(item.getId(), context);
		for (GInputPrivilege privilegeFilterItem : privilegeFilter) {
			if (privilegeFilterItem != null) {
				AccessOperationCollection operations = privileges.get(privilegeFilterItem.getKey());
				if (operations != null && operations.contains(privilegeFilterItem.getOperations())) {
					return true;
				}
			}
		}
		return false;
	}
}

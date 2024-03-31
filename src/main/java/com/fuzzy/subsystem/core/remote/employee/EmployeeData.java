package com.fuzzy.subsystem.core.remote.employee;


import com.fuzzy.cluster.core.remote.struct.RemoteObject;

public class EmployeeData implements RemoteObject {

	private String login;
	private String domain;

	public String getLogin() {
		return login;
	}

	public String getDomain() {
		return domain;
	}

	private EmployeeData() {

	}

	public static Builder newBuilder() {
		return new EmployeeData().new Builder();
	}

	public class Builder {

		private Builder() {

		}

		public Builder setLogin(String login) {
			EmployeeData.this.login = login;
			return this;
		}

		public Builder setDomain(String domain) {
			EmployeeData.this.domain = domain;
			return this;
		}

		public EmployeeData build() {
			return EmployeeData.this;
		}
	}
}

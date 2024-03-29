package com.fuzzy.subsystem.frontend.component.authcontext.builder.apikey;

import com.fuzzy.main.cluster.graphql.struct.GRequest;
import com.fuzzy.main.rdao.database.domainobject.filter.HashFilter;
import com.fuzzy.main.platform.component.frontend.request.GRequestHttp;
import com.fuzzy.main.platform.component.frontend.request.GRequestWebSocket;
import com.fuzzy.main.platform.component.frontend.utils.GRequestUtils;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.ReadableResource;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.apikeyprivileges.ApiKeyPrivilegesGetter;
import com.fuzzy.subsystem.core.domainobject.apikey.ApiKeyReadable;
import com.fuzzy.subsystem.core.domainobject.apikey.ApiKeyTypes;
import com.fuzzy.subsystem.frontend.struct.request.RequestWebSocket;
import com.fuzzy.subsystems.utils.ApiKeyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuilderApiKeyDefaultAuthContext extends BuilderApiKeyAuthContext {

	private final static Logger log = LoggerFactory.getLogger(BuilderApiKeyDefaultAuthContext.class);

	private static final String REQUEST_PARAM_API_KEY = "api_key";

	private ReadableResource<ApiKeyReadable> apiKeyReadableResource;

	public BuilderApiKeyDefaultAuthContext(boolean serviceMode) {
		super(serviceMode);
	}

	@Override
	public boolean prepare(ResourceProvider resources, GRequest gRequest) {
		boolean isOk;
		if (gRequest instanceof GRequestHttp) {
			GRequestHttp gRequestHttp = (GRequestHttp) gRequest;
			String apiKey = gRequestHttp.getParameter(REQUEST_PARAM_API_KEY);
			isOk = (apiKey != null);
		} else if (gRequest instanceof GRequestWebSocket) {
			GRequestWebSocket requestWebSocket = (GRequestWebSocket) gRequest;
			String apiKey = requestWebSocket.getParameter(REQUEST_PARAM_API_KEY);
			isOk = (apiKey != null);
		} else {
			throw new RuntimeException("Not implemented");
		}
		if (isOk) {
			apiKeyReadableResource = resources.getReadableResource(ApiKeyReadable.class);
			apiKeyPrivilegesGetter = new ApiKeyPrivilegesGetter(resources);
		}
		return isOk;
	}

	@Override
	public String getBuilderName() throws PlatformException{
		return this.getClass().getSimpleName();
	}

	protected ApiKeyReadable getApiKey(GRequest gRequest, ContextTransaction context) throws PlatformException {
		String apiKey;
		if (gRequest instanceof GRequestHttp) {
			apiKey = ((GRequestHttp) gRequest).getParameter(REQUEST_PARAM_API_KEY);
		} else if (gRequest instanceof RequestWebSocket) {
			apiKey = ((RequestWebSocket) gRequest).getParameter(REQUEST_PARAM_API_KEY);
		} else {
			throw new RuntimeException("Not implemented");
		}
		return apiKey == null ? null : getApiKey(apiKey.trim(), gRequest, context);
	}

	private ApiKeyReadable getApiKey(String apiKeyValue, GRequest gRequest, ContextTransaction context) throws PlatformException {
		ApiKeyUtils.advanceValidationApiKey(apiKeyValue);

		HashFilter filter = new HashFilter(ApiKeyReadable.FIELD_VALUE, apiKeyValue);
		ApiKeyReadable apiKey = apiKeyReadableResource.find(filter, context.getTransaction());
		if (apiKey == null) {
			log.debug("Request (Trace: {}). Integration logon failed by api key: {}",
					GRequestUtils.getTraceRequest(gRequest),
					ApiKeyUtils.concealmentApiKey(apiKeyValue)
			);
			return null;
		}
		return ApiKeyTypes.NONE.isSame(apiKey.getType(), apiKey.getSubsystemUuid()) ? apiKey : null;
	}

	@Override
	public String toString() {
		return "BuilderApiKeyDefaultAuthContext";
	}

}


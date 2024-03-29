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
import com.fuzzy.subsystem.frontend.utils.HttpsRequestUtil;
import com.fuzzy.subsystems.utils.ApiKeyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuilderApiKeyCertificateAuthContext extends BuilderApiKeyAuthContext {

	private final static Logger log = LoggerFactory.getLogger(BuilderApiKeyCertificateAuthContext.class);

	private static final String REQUEST_PARAM_API_KEY_CERTIFICATE = "api_key_certificate";

	private ReadableResource<ApiKeyReadable> apiKeyReadableResource;

	public BuilderApiKeyCertificateAuthContext(boolean serviceMode) {
		super(serviceMode);
	}

	@Override
	public boolean prepare(ResourceProvider resources, GRequest gRequest) {
		boolean isOk;
		if (gRequest instanceof GRequestHttp) {
		    //Почему требуем отдельное поле, а не просто проверяем наличие присланных клиенских сертификатов:
            //Делов в том, что на агенте есть проблема с библиотекой curl, которая сама в определеных ситуация отправляет
            //клиенский сертификат, в итоге при авторизации получаем неопределенную ситуация
			GRequestHttp gRequestHttp = (GRequestHttp) gRequest;
			String sApiKeyCertificate = gRequestHttp.getParameter(REQUEST_PARAM_API_KEY_CERTIFICATE);
			isOk = (sApiKeyCertificate != null);
		} else if (gRequest instanceof GRequestWebSocket) {
			isOk = false;
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
		if (gRequest instanceof GRequestHttp) {
			String[] certificateApiKeys = ((GRequestHttp) gRequest).getAttributes(HttpsRequestUtil.ATTRIBUTE_CERTIFICATE_API_KEYS);
			if (certificateApiKeys != null) {
				for (String certificateApiKey : certificateApiKeys) {
					ApiKeyReadable apiKeyReadable = getApiKey(certificateApiKey, gRequest, context);
					if (certificateApiKey != null) {
						return apiKeyReadable;
					}
				}
			}
		} else if (gRequest instanceof RequestWebSocket) {
			//TODO Not implemented
			throw new RuntimeException("Not implemented");
		} else {
			throw new RuntimeException("Not implemented");
		}
		return null;
	}

	private ApiKeyReadable getApiKey(String apiKeyValue, GRequest gRequest, ContextTransaction context)
			throws PlatformException {
		HashFilter filter = new HashFilter(ApiKeyReadable.FIELD_VALUE, apiKeyValue.trim());
		ApiKeyReadable apiKey = apiKeyReadableResource.find(filter, context.getTransaction());
		if (apiKey == null) {
			log.debug("Request (Trace: {}). Integration logon failed by api key: {}",
					GRequestUtils.getTraceRequest(gRequest),
					ApiKeyUtils.concealmentApiKey(apiKeyValue)
			);
			return null;
		}
		return ApiKeyTypes.CERTIFICATE.isSame(apiKey.getType(), apiKey.getSubsystemUuid()) ? apiKey : null;
	}

	@Override
	public String toString() {
		return "BuilderApiKeyCertificateAuthContext";
	}
}

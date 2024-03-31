package com.fuzzy.subsystem.frontend.controller.http;

import com.fuzzy.main.Subsystems;
import com.fuzzy.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.platform.component.frontend.context.impl.ContextTransactionRequestImpl;
import com.fuzzy.platform.component.frontend.context.source.impl.SourceGRequestAuthImpl;
import com.fuzzy.platform.component.frontend.request.GRequestHttp;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.Query;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.authcontext.employee.EmployeeAuthContext;
import com.fuzzy.subsystem.frontend.FrontendSubsystem;
import com.fuzzy.subsystem.frontend.component.authcontext.AuthContextComponent;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import jakarta.servlet.http.HttpServletRequest;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.Instant;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by kris on 26.12.16.
 */
@Controller
@RequestMapping("/")
public class GraphiQLController {

	private final static Logger log = LoggerFactory.getLogger(GraphiQLController.class);

	@RequestMapping(value = "/graphiql")
	public Object viewGraphiQL(HttpServletRequest request) {
		try {
			FrontendSubsystem frontendSubsystem = Subsystems.getInstance().getCluster().getAnyLocalComponent(FrontendSubsystem.class);

			GRequestHttp gRequest = new GRequestHttp(
					Instant.now(),
					null,
					null, new HashMap<>(),
					null,
					null,
					new HashMap<>(),
					new HashMap<>(),
					request.getCookies(),
					null
			);

			SourceGRequestAuthImpl source = new SourceGRequestAuthImpl(gRequest);
			ContextTransactionRequestImpl context = new ContextTransactionRequestImpl(source);

			boolean access = Subsystems.getInstance().getQueryPool().execute(
					frontendSubsystem,
					new Query<Boolean>() {

						private AuthContextComponent authContextComponent;

						@Override
						public void prepare(ResourceProvider resources) throws PlatformException {
							authContextComponent = new AuthContextComponent(frontendSubsystem, gRequest, resources);
						}

						@Override
						public Boolean execute(QueryTransaction transaction) throws PlatformException {
							context.setTransaction(transaction);

							UnauthorizedContext authContext = authContextComponent.getAuthContext(context);
							if (!(authContext instanceof EmployeeAuthContext)) return false;
							EmployeeAuthContext employeeAuthContext = (EmployeeAuthContext) authContext;

							return employeeAuthContext.getOperations(CorePrivilege.GRAPHQL_TOOL.getUniqueKey())
									.contains(AccessOperation.EXECUTE);
						}
					}
			).get();
			if (access) {
				return new ModelAndView("/static/graphiql/graphiql.html");
			} else {
				throw GeneralExceptionBuilder.buildAccessDeniedException();
			}
		} catch (Exception exception) {
			PlatformException e;
			if (exception instanceof PlatformException) {
				e = (PlatformException) exception;
			} else if (exception instanceof ExecutionException && exception.getCause() instanceof PlatformException) {
				e = (PlatformException) exception.getCause();
			} else {
				throw new RuntimeException(exception);
			}

			JSONObject error = new JSONObject();
			error.put("code", e.getCode());
			if (e.getComment() != null) {
				error.put("message", e.getComment());
			}

			return new ResponseEntity(
					error.toJSONString(),
					HttpStatus.INTERNAL_SERVER_ERROR
			);
		}
	}
}

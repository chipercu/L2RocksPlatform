package com.fuzzy.subsystem.frontend.controller.http;

import com.infomaximum.main.Subsystems;
import com.fuzzy.subsystem.frontend.FrontendSubsystem;
import com.fuzzy.subsystem.frontend.component.authcontext.builder.employee.BuilderEmployeeSessionAuthContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Controller
@RequestMapping("/")
public class IndexController {

	private final static Logger log = LoggerFactory.getLogger(IndexController.class);

	@RequestMapping(value = { "/" }, headers = { "Connection!=Upgrade", "Connection!=upgrade" })
	public Object index(HttpServletRequest request, HttpServletResponse response) throws IOException {
		FrontendSubsystem frontEndSubSystem = Subsystems.getInstance().getCluster().getAnyLocalComponent(FrontendSubsystem.class);

		//Механизм позволяющий прокинуть сесионную куку через get параметр
		String sessionUuid = request.getParameter(BuilderEmployeeSessionAuthContext.REQUEST_PARAM_SESSION);
		if (sessionUuid != null) {
			Cookie cookie = new Cookie(BuilderEmployeeSessionAuthContext.REQUEST_PARAM_SESSION, sessionUuid);
			cookie.setPath("/");
			cookie.setMaxAge((int) frontEndSubSystem.getConfig().getSessionTimeout().getSeconds());
			response.addCookie(cookie);
		}

		responseIndexHtml(response);
		return null;
	}

	public static void responseIndexHtml(HttpServletResponse response) throws IOException {
		FrontendSubsystem frontEndSubSystem = Subsystems.getInstance().getCluster().getAnyLocalComponent(FrontendSubsystem.class);
		Path fileIndexHtml = frontEndSubSystem.getConfig().getWebPath().resolve("index.html");

		response.setContentType("text/html; charset=UTF-8");
		if (Files.exists(fileIndexHtml)) {
			response.setStatus(HttpStatus.OK.value());
			response.setContentLengthLong(Files.size(fileIndexHtml));

			try (InputStream in = Files.newInputStream(fileIndexHtml)) {
				in.transferTo(response.getOutputStream());
			}
		} else {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			response.getOutputStream().println("Not found index.html");

			log.error("Not found index.html: {}", fileIndexHtml.toAbsolutePath());
		}
	}
}
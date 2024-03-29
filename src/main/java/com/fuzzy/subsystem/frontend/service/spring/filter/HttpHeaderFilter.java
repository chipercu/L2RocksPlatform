package com.fuzzy.subsystem.frontend.service.spring.filter;

import com.fuzzy.main.Subsystems;
import com.fuzzy.subsystem.frontend.FrontendSubsystem;
import com.fuzzy.subsystem.frontend.struct.config.ConnectorConfig;
import com.fuzzy.subsystem.frontend.struct.config.Protocol;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class HttpHeaderFilter implements Filter {

    private boolean isSupportIFrame;
    private boolean isOnlyHttps;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        FrontendSubsystem frontEndSubSystem = Subsystems.getInstance().getCluster().getAnyLocalComponent(FrontendSubsystem.class);

        isSupportIFrame = frontEndSubSystem.getConfig().isSupportIFrame();

        isOnlyHttps = true;
        for (ConnectorConfig connectorConfig : frontEndSubSystem.getConfig().getConnectors()) {
            if (connectorConfig.getProtocol() != Protocol.HTTPS) {
                isOnlyHttps = false;
                break;
            }
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        if (!isSupportIFrame) {
            httpResponse.setHeader("X-Frame-Options", "DENY");
        }
        if (isOnlyHttps) {
            httpResponse.setHeader("Strict-Transport-Security", "max-age=3600");
        }
        chain.doFilter(request, response);
    }
}

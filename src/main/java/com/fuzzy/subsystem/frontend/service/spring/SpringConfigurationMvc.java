package com.fuzzy.subsystem.frontend.service.spring;

import org.apache.commons.lang3.SystemUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.DeferredResultProcessingInterceptor;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.*;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.Duration;

/**
 * Created by kris on 13.10.16.
 */
@EnableWebMvc
@Configuration
@ComponentScan({ "com.infomaximum.subsystem" })
public class SpringConfigurationMvc implements WebMvcConfigurer {

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public static class AsyncTimeoutException extends Exception {
    }

    private static Path webPath;
    private static Duration requestTimeout;
    private static Path docsDir;
    private static Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setDefaultTimeout(requestTimeout.toMillis());
        configurer.registerDeferredResultInterceptors(
                new DeferredResultProcessingInterceptor() {
                    @Override
                    public <T> boolean handleTimeout(NativeWebRequest req, DeferredResult<T> result) {
                        return result.setErrorResult(new AsyncTimeoutException());
                    }
                });
    }

    //TODO Выключили при миграции на jdk17 - что бы не проходило через jstl, если так и не пригодится - удалить
//	@Bean
//	public InternalResourceViewResolver viewResolver() {
//		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
//		viewResolver.setViewClass(JstlView.class);
//		return viewResolver;
//	}

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String prefixFile = "file:";
        if (SystemUtils.IS_OS_WINDOWS) prefixFile += "/";

        String fileSeparator = FileSystems.getDefault().getSeparator();

        registry.addResourceHandler("/favicon.ico").addResourceLocations(prefixFile + webPath.resolve("favicon.ico").normalize().toAbsolutePath());
        registry.addResourceHandler("/_build/**").addResourceLocations(prefixFile + webPath.resolve("_build").normalize().toAbsolutePath() + fileSeparator);

        //Встроенные в jar ресурсы
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:webapp/static/");

        // Документация
        registry.addResourceHandler("/docs/**").addResourceLocations(prefixFile + docsDir.normalize().toAbsolutePath() + fileSeparator);

        //Путь для загружемых пользователями ресурсов
//		registry.addResourceHandler("/store/**").addResourceLocations(prefixFile + storePath.toAbsolutePath() + fileSeparator);
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        AntPathMatcher matcher = new AntPathMatcher();
        matcher.setCaseSensitive(false);
        configurer.setPathMatcher(matcher);
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    public static void init(Path webPath,
                            Duration requestTimeout,
                            Path docsDir,
                            Thread.UncaughtExceptionHandler uncaughtExceptionHandler
    ) {
        SpringConfigurationMvc.webPath = webPath;
        SpringConfigurationMvc.requestTimeout = requestTimeout;
        SpringConfigurationMvc.docsDir = docsDir;
        SpringConfigurationMvc.uncaughtExceptionHandler = uncaughtExceptionHandler;
    }
}
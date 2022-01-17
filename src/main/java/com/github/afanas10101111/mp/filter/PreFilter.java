package com.github.afanas10101111.mp.filter;

import com.github.afanas10101111.mp.service.RequestBodyChecker;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

@Slf4j
@RequiredArgsConstructor
@Component
public class PreFilter extends ZuulFilter {
    private final RequestBodyChecker checker;

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();
        try (InputStream in = context.getRequest().getInputStream()) {
            String body = StreamUtils.copyToString(in, StandardCharsets.UTF_8);
            log.info("run -> request: {}", body);
            checker.getStubbedResponse(body).ifPresent(s -> {
                log.info("run -> stubbed response: {}", s);
                context.setResponseBody(s);
                context.getResponse().setHeader("Content-Type", "text/xml;charset=UTF-8");
                context.setSendZuulResponse(false);
            });
        } catch (IOException e) {
            log.warn("run -> exception: {}", e.getMessage());
        }
        return null;
    }
}

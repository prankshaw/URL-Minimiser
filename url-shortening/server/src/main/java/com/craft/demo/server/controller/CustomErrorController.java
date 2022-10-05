package com.craft.demo.server.controller;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@RestController
@Slf4j
public class CustomErrorController implements ErrorController {

    @GetMapping(value = "/error")
    public void basicErrorHandling(HttpServletRequest request) {

        StringBuilder builder = new StringBuilder();
        builder.append("URL: ").append(request.getRequestURL()).append("\n")
                .append("URI: ").append(request.getRequestURI()).append("\n")
                .append("PATH INFO: ").append(request.getPathInfo()).append("\n")
                .append("QUERY PARAMS: ").append(request.getQueryString()).append("\n")
                .append("REMOTE USER: ").append(request.getRemoteUser()).append("\n");

        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            val header = headers.nextElement();
            builder.append(header).append(" : ").append(request.getHeader(header));
        }

        log.info("CustomErrorController unknown request caught url info {}", builder.toString());
    }
}

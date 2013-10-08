package com.github.safrain.gan;

import javax.servlet.*;
import java.io.IOException;

/**
 * Http basic auth filter
 */
public class HttpBasicAuthFilter implements Filter {

    private String username;
    private String passwordHash;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //TODO:
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //TODO:
    }

    @Override
    public void destroy() {
    }
}

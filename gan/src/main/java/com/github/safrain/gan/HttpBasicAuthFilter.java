package com.github.safrain.gan;

import sun.misc.BASE64Decoder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Http basic auth filter
 */
public class HttpBasicAuthFilter implements Filter {

    private String user;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //TODO:
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String auth = request.getHeader("Authorization");

        if (auth == null || !auth.toUpperCase().startsWith("BASIC ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        String userpassEncoded = auth.substring(6);
        String userpassDecoded = new String(new BASE64Decoder().decodeBuffer(userpassEncoded));

        if (!userpassDecoded.equals(user)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        chain.doFilter(req, resp);
    }

    @Override
    public void destroy() {
    }
}

package com.github.safrain.grooveadmin;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.logging.Logger;

/**
 * GAN server side <br>
 * <h2>Supported entries</h2> Assume that requests with URI '/admin/gan' will be
 * handled with this filter:
 * <ul>
 * <li><b>GET /admin/gan</b> Show welcome screen</li>
 * <li><b>GET /admin/gan?r=install</b> Download shell client install script</li>
 * <li><b>POST /admin/gan</b> Run script in request body</li>
 * </ul>
 * <br>
 * <p/>
 * <h2>Init parameters:</h2> Init paramters configured in you web.xml
 * <ul>
 * <li><b>charset</b> Request & response charset</li>
 * <li><b>resourcePackage</b> Init script classpath, some initialization work(put
 * some utility function in the script context or something more) will be done
 * in this script</li>
 * <li><b>initScriptCharset</b> Init script charset</li>
 * </ul>
 *
 * @author safrain
 */
public class GrooveAdminFilter implements Filter {
    private static final Logger log = Logger.getLogger(GrooveAdminFilter.class.getName());
    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final String DEFAULT_RESOURCE_PATH = "com/github/safrain/grooveadmin";

    /**
     * Request & response charset
     */
    private String charset = DEFAULT_CHARSET;

    /**
     * Charset used when reading resource files
     */
    private String resourceCharset = DEFAULT_CHARSET;


    private String envScript = DEFAULT_RESOURCE_PATH + "/init.groovy";


    // ==========Filter implementation==========
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        charset = filterConfig.getInitParameter("charset");
        resourceCharset = filterConfig.getInitParameter("resourceCharset");
        envScript = filterConfig.getInitParameter("envScript");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        if (!(req instanceof HttpServletRequest && resp instanceof HttpServletResponse)) {
            throw new IllegalArgumentException();
        }
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        req.setCharacterEncoding(charset);
        resp.setCharacterEncoding(charset);

        ScriptEngine engine = new ScriptEngineManager().getEngineByName("Groovy");
        if (engine == null) {
            throw new IllegalArgumentException("Groovy engine not found.");
        }
        PrintWriter writer = response.getWriter();
        engine.getContext().setWriter(writer);
        engine.getContext().setErrorWriter(writer);
        engine.put("_request", request);
        engine.put("_response", response);
        engine.put("_filter", this);

        try {
            engine.eval(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(""), resourceCharset));
        } catch (ScriptException e) {
            throw new ServletException(e);
        }
    }

    public void destroy() {
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getResourceCharset() {
        return resourceCharset;
    }

    public void setResourceCharset(String resourceCharset) {
        this.resourceCharset = resourceCharset;
    }

    public String getEnvScript() {
        return envScript;
    }

    public void setEnvScript(String envScript) {
        this.envScript = envScript;
    }
}

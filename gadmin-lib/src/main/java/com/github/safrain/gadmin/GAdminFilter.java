package com.github.safrain.gadmin;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * GAdmin server side <br>
 * <h2>Supported entries</h2> Assume that requests with URI '/gadmin' will be
 * handled with this filter:
 * <ul>
 * <li><b>GET /gadmin</b> Welcome screen</li>
 * <li><b>GET /gadmin?client</b> gadmin bash client</li>
 * <li><b>GET /gadmin?install</b> Client install script</li>
 * <li><b>POST /gadmin</b> Run script in request body on server</li>
 * </ul>
 * <br>
 * <p/>
 * <h2>Init parameters:</h2>
 * <ul>
 * <li><b>charset</b> Request & response charset</li>
 * </ul>
 *
 * @author safrain
 */
public class GAdminFilter implements Filter {
	public static final String DEFAULT_CHARSET = "UTF-8";
	public static final String DEFAULT_RESOURCE_PACKAGE = "/com/github/safrain/gadmin/";

	/**
	 * Request & response charset
	 */
	private String charset;

	/**
	 * All threads running script through this filter
	 */
	public static final Set<RunInfo> runningInfos = Collections.newSetFromMap(new ConcurrentHashMap<RunInfo, Boolean>());


	/**
	 * Override this method if you want to use some other engine
	 */
	protected ScriptEngine createScriptEngine() {
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("Groovy");
		if (engine == null) {
			throw new IllegalArgumentException("Groovy engine not found.");
		}
		return engine;
	}

	/**
	 * Scripts returned by this method will be evaluated *BEFORE* the script in request body.
	 * Some initialization work shall be done in this script, such like put some variable
	 * or utility methods into the script environment, have a look at <b>spring.groovy</b>.
	 */
	protected List<String> getScriptBeforeEvaluation() {
		List<String> scripts = new ArrayList<String>();
		scripts.add(loadFromClasspath(DEFAULT_RESOURCE_PACKAGE + "utils.groovy", DEFAULT_CHARSET));
		scripts.add(loadFromClasspath(DEFAULT_RESOURCE_PACKAGE + "spring.groovy", DEFAULT_CHARSET));
		return scripts;
	}

	/**
	 * Content returned by this method will be displayed when user do an http GET on this
	 * filter without parameters, just show some tips on the screen.
	 */
	protected String getWelcomeScreen() {
		return loadFromClasspath(DEFAULT_RESOURCE_PACKAGE + "welcome.txt", DEFAULT_CHARSET);
	}

	/**
	 * Content returned by this method will be executed as an install script, to download
	 * gadmin shell client and add execute permission.
	 */
	protected String getInstallScript() {
		return loadFromClasspath(DEFAULT_RESOURCE_PACKAGE + "install.sh", DEFAULT_CHARSET);
	}

	/**
	 * Content return by this method will be stored on user computer, as a client script
	 */
	protected String getClient() {
		return loadFromClasspath(DEFAULT_RESOURCE_PACKAGE + "gadmin.sh", DEFAULT_CHARSET);
	}

	public static String loadFromClasspath(String path, String charset) {
		InputStream in = GAdminFilter.class.getClassLoader().getResourceAsStream(path);
		return toString(in, charset);
	}

	private static String toString(InputStream in, String charset) {
		Scanner s = new Scanner(in, charset).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	/**
	 * Return a map containing contents you want to replace in {@link #getInstallScript()}.
	 * {@link #getWelcomeScreen()} ,{@link #getClient()} ,{@link #getScriptBeforeEvaluation()},
	 * Placeholder format: {{KEY}}
	 */
	protected Map<String, Object> getReplacements(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> replacement = new HashMap<String, Object>();
		replacement.put("host", request.getRequestURL().toString());
		return replacement;
	}

	private String replace(String content, Map<String, Object> replacements) {
		String r = content;
		for (Map.Entry<String, Object> entry : replacements.entrySet()) {
			r = r.replaceAll("\\{\\{" + entry.getKey() + "\\}\\}", entry.getValue() == null ? "" : String.valueOf(entry.getValue()));
		}
		return r;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		charset = filterConfig.getInitParameter("charset");
		if (charset == null) {
			charset = DEFAULT_CHARSET;
		}
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
		PrintWriter writer = response.getWriter();

		if ("GET".equals(request.getMethod())) {
			String content;
			if (request.getParameter("install") != null) {
				content = getInstallScript();
			} else if (request.getParameter("client") != null) {
				content = getClient();
			} else {
				content = getWelcomeScreen();
			}
			writer.write(replace(content, getReplacements(request, response)));
		} else if ("POST".equals(request.getMethod())) {
			ScriptEngine engine = createScriptEngine();
			engine.getContext().setWriter(writer);
			engine.getContext().setErrorWriter(writer);
			engine.put("_request", request);
			engine.put("_response", response);
			engine.put("_this", this);
			engine.put("_engine", engine);

			String script = toString(request.getInputStream(), charset);
			RunInfo runInfo = new RunInfo(script, request.getRemoteAddr(), new Date(), Thread.currentThread());
			try {
				runningInfos.add(runInfo);
				for (String before : getScriptBeforeEvaluation()) {
					engine.eval(before);
				}
				engine.eval(script);
			} catch (ScriptException e) {
				response.setStatus(500);
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				writer.write(sw.toString());
			} finally {
				runningInfos.remove(runInfo);
			}
		} else {
			throw new UnsupportedOperationException();
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

	public static class RunInfo {
		public final String scriptContent;
		public final String remoteAddress;
		public final Date startTime;
		public final Thread thread;
		public final String uuid;

		public RunInfo(String scriptContent, String remoteAddress, Date startTime, Thread thread) {
			this.scriptContent = scriptContent;
			this.remoteAddress = remoteAddress;
			this.startTime = startTime;
			this.thread = thread;
			this.uuid = UUID.randomUUID().toString();
		}

		@Override
		public String toString() {
			return "RunInfo{" +
					"scriptContent='" + scriptContent + '\'' +
					", remoteAddress='" + remoteAddress + '\'' +
					", startTime=" + startTime +
					", thread=" + thread +
					", uuid='" + uuid + '\'' +
					'}';
		}
	}

}

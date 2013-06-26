package com.github.safrain.grooveadmin

/**
 * Spring framework support
 */
public class Beans {
    org.springframework.context.ApplicationContext context

    def propertyMissing(String name) {
        try {
            return context.getBean(name)
        } catch (e) {
            e.printStackTrace()
        }
    }

    String toString() {
        StringWriter sw = new StringWriter()
        context.getBeanDefinitionNames().each { sw.println "${it}" }
        sw.buffer.toString()
    }
}
_context = org.springframework.web.context.support.WebApplicationContextUtils.getWebApplicationContext(_request.session.servletContext)
beans = new java.beans.Beans()
beans.context = _context

package com.github.safrain.gadmin

import org.springframework.context.ConfigurableApplicationContext

/**
 * Spring framework support
 */
class Beans {
    ConfigurableApplicationContext context

    Object get(String name) {
        context.getBean(name)
    }

    String toString() {
        context.getBeanDefinitionNames().inject("""
Spring application context: ${context}
Beans defined in this context:
""") { r, beanName ->
            def bd = context.getBeanFactory().getBeanDefinition(beanName)
            r + "${beanName} - [${bd.getBeanClassName()}]\n"
        }
    }

    Object propertyMissing = {
        return context.getBean(it)
    }

}
beans = new Beans(context: org.springframework.web.context.support.WebApplicationContextUtils.getWebApplicationContext(_request.session.servletContext))


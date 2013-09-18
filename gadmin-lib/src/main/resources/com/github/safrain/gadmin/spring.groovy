package com.github.safrain.gadmin
/**
 * Spring framework support
 */
_context = org.springframework.web.context.support.WebApplicationContextUtils.getWebApplicationContext(_request.session.servletContext)
def beans = new Object()

beans.metaClass.propertyMissing = {
    return _context.getBean(it)
}
beans.metaClass.list = {
    _context.getBeanDefinitionNames().each { println "${it}" }
}
_engine.put('beans', beans)


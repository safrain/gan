/**
 * Spring framework support
 */
try {
    Class.forName('org.springframework.context.ApplicationContext')
    _context = org.springframework.web.context.support.WebApplicationContextUtils.getWebApplicationContext(_request.session.servletContext)
    beans = new Object()

    beans.metaClass.propertyMissing = {
        return _context.getBean(it)
    }
    beans.list = {
        _context.getBeanDefinitionNames().each { println "${it}" }
    }

} catch (ClassNotFoundException e) {
}


package com.github.safrain.grooveadmin

import groovy.text.GStringTemplateEngine

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

HttpServletRequest request = _request;
HttpServletResponse response = _response;
GrooveAdminFilter grooveAdminFilter = _filter;

def getResource = { String it ->
    grooveAdminFilter.class.classLoader.getResourceAsStream(it).getText(grooveAdminFilter.resourceCharset)
}

if (request.method == 'GET') {
    def action = request.getParameter 'action'
    def filename
    if (action == 'install') {
        filename = 'install.sh'
    } else if (action == 'client') {
        filename = 'client.sh'
    } else {
        filename = 'welcome.txt'
    }
    def bindings = [
            'host': request.getRequestURL().toString()
    ]
    new GStringTemplateEngine()
            .createTemplate(getResource("${GrooveAdminFilter.DEFAULT_RESOURCE_PATH}/${filename}"))
            .make(bindings)
            .writeTo(response.writer)
} else if (request.method == 'POST') {
    def script = request.inputStream.getText(grooveAdminFilter.charset)
    try {
        try {
            evaluate getResource(GrooveAdminFilter.DEFAULT_RESOURCE_PATH + '/spring.groovy')
        } catch (e) {
        }
        evaluate script
        response.status = 200;
    } catch (e) {
        e.cause.printStackTrace response.writer;
        response.status = 500;
    }
}















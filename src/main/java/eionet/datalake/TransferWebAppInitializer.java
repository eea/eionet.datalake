/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Web Transfer 1.0
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency. All Rights Reserved.
 *
 * Contributor(s):
 *        Søren Roug
 */
package eionet.datalake;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;


/**
 * Initialise DispatcherServlet.
 *
 * See http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#mvc-container-config
 */
public class TransferWebAppInitializer implements WebApplicationInitializer {

    private Log logger = LogFactory.getLog(TransferWebAppInitializer.class);

    @Override
    public void onStartup(ServletContext container) {
        XmlWebApplicationContext appContext = new XmlWebApplicationContext();
        appContext.setConfigLocation("/WEB-INF/classes/spring-db-config.xml "
                                + "/WEB-INF/classes/spring-init-config.xml "
                                + "/WEB-INF/classes/spring-mvc-config.xml");

        // Listeners
        //SingleSignOutHttpSessionListener ssoListener = new SingleSignOutHttpSessionListener(appContext);
        //container.addListener(ssoListener);

        //ContextLoaderListener contextLoaderListener = new ContextLoaderListener(appContext);
        //container.addListener(contextLoaderListener);

        ServletRegistration.Dynamic dispatcher = container.addServlet("dispatcher", new DispatcherServlet(appContext));

        String location = null;
        try {
            location = System.getProperty("upload.dir", null);
        } catch (Exception e) {
            location = null;
        }

        logger.info("Upload directory configured to: " + (location == null ? "[default]" : location));

        //long maxFileSize = 5000000L;
        //long maxRequestSize = 5000000L;
        //int fileSizeThreshold = 0;
        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(location);
        //MultipartConfigElement multipartConfigElement = new MultipartConfigElement(location, maxFileSize, maxRequestSize, fileSizeThreshold);
        dispatcher.setMultipartConfig(multipartConfigElement);

        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }

}

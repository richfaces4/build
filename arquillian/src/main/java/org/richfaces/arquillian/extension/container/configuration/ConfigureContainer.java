package org.richfaces.arquillian.extension.container.configuration;

import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.EventContext;
import org.jboss.arquillian.test.spi.event.suite.BeforeSuite;

public class ConfigureContainer {

    public void configureContainer(@Observes EventContext<BeforeSuite> ctx) {
        ctx.proceed();
    }

    private void configureContainer() {
        System.out.println("configure container !!!!!!!!!!!!!!");
    }
}

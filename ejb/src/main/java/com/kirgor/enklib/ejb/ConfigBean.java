package com.kirgor.enklib.ejb;

import com.kirgor.enklib.common.ConfigUtils;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

/**
 * Startup singleton bean, which is responsible for loading configuration file from WEB-INF/enklib-ejb.xml and
 * storing service {@link Config} instance (each application server deployment, which uses Enklib EJB, has own instance).
 */
@Singleton
@Startup
public class ConfigBean {
    private Config config;

    @PostConstruct
    public void postConstruct() {
        try {
            String classesUrl = ConfigBean.class.getResource("/").toString();
            String webInfUrl = classesUrl.substring(0, classesUrl.lastIndexOf("/classes"));

            URL configFileUrl = new URL(webInfUrl + "/enklib-ejb.xml");
            InputStream inputStream = configFileUrl.openStream();
            config = ConfigUtils.loadFromXMLStream(Config.class, inputStream);
            inputStream.close();
        } catch (Exception e) {
            System.out.println("Can't read configuration file enklib-ejb.xml");
        }
    }

    public Config getConfig() {
        return config;
    }
}

package com.kirgor.tools.ejb;

import com.kirgor.tools.common.ConfigUtils;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

@Singleton
@Startup
public class ConfigBean {
    private Config config;

    @PostConstruct
    public void postConstruct() {
        try {
            String classesUrl = ConfigBean.class.getResource("/").toString();
            String webInfUrl = classesUrl.substring(0, classesUrl.lastIndexOf("/classes"));

            URL configFileUrl = new URL(webInfUrl + "/kirgor-tools-ejb.xml");
            InputStream inputStream = configFileUrl.openStream();
            config = ConfigUtils.loadFromXMLStream(Config.class, inputStream);
            inputStream.close();
        } catch (Exception e) {
            System.out.println("Can't read configuration file kirgor-tools-ejb.xml");
        }
    }

    public Config getConfig() {
        return config;
    }
}

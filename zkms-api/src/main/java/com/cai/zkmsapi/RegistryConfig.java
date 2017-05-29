package com.cai.zkmsapi;

import dom.cai.zkms.ServiceRegistry;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by reason on 17/5/29.
 */
@Configuration
@ConfigurationProperties(prefix = "registry")
public class RegistryConfig {

    private String servers;

    @Bean
    public ServiceRegistry serviceRegistry(){
        return new ServiceRegistryImpl(servers);
    }

    public void  setServers(String servers){
        this.servers =servers;
    }
}

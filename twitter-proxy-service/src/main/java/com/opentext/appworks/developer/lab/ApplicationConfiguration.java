/**
 * Copyright © 2017 Open Text.  All Rights Reserved.
 */
package com.opentext.appworks.developer.lab;

import com.opentext.appworks.developer.lab.services.DefaultSearchTermSettingManager;
import com.opentext.appworks.developer.lab.services.TwitterClientServiceImpl;
import com.opentext.otag.service.context.components.AWComponentContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.social.twitter.api.impl.TwitterTemplate;

@Configuration
@ComponentScan("com.opentext.appworks.developer.lab")
@PropertySource("classpath:application.properties")
public class ApplicationConfiguration {

    @Value("${spring.social.twitter.appId}")
    private String consumerKey;

    @Value("${spring.social.twitter.appSecret}")
    private String consumerSecret;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public TwitterTemplate twitterTemplate() {
        return new TwitterTemplate(consumerKey, consumerSecret);
    }

    @Bean
    public TwitterClientServiceImpl twitterClientService() {
        return new TwitterClientServiceImpl(twitterTemplate());
    }

    @Bean @Lazy
    public DefaultSearchTermSettingManager defaultSearchTermSettingManager() {
        return AWComponentContext.getComponent(DefaultSearchTermSettingManager.class);
    }

}

/**
 * Copyright © 2017 Open Text.  All Rights Reserved.
 */
package com.opentext.appworks.developer.lab.services;

import com.opentext.appworks.developer.lab.ApplicationConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {ApplicationConfiguration.class},
        loader = AnnotationConfigContextLoader.class
)
public class TwitterClientServiceImplTest {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterClientServiceImplTest.class);

    @Autowired
    private TwitterTemplate twitterTemplate;

    @Autowired
    private TwitterClientService underTest;

    @Test
    public void testSearchForTweets() {
        assertThat(underTest).isNotNull();
        List<Tweet> tweets = underTest.search("Wayne Gretzky", false);
        assertThat(tweets).isNotEmpty();

        tweets.forEach(tweet -> {
            try {
                Tweet status = twitterTemplate.timelineOperations().getStatus(tweet.getId());
                assertThat(status).isNotNull();
            } catch (Exception e) {
                LOG.error("Failed to find current status of - " + tweet);
            }

        });
    }

}
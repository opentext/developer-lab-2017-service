/**
 * Copyright © 2017 Open Text.  All Rights Reserved.
 */
package com.opentext.appworks.developer.lab.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.twitter.api.Place;
import org.springframework.social.twitter.api.SearchResults;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.impl.TwitterTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TwitterClientServiceImpl implements TwitterClientService {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterClientServiceImpl.class);

    private final TwitterTemplate twitterTemplate;

    @Autowired
    public TwitterClientServiceImpl(TwitterTemplate twitterTemplate) {
        this.twitterTemplate = twitterTemplate;
    }

    @Override
    public List<Tweet> search(String searchTerm, boolean requiresMedia) {
        try {
            SearchResults search = twitterTemplate.searchOperations().search(searchTerm);
            return search.getTweets()
                    .stream()
                    .filter(tweet -> !requiresMedia || tweet.hasMedia())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOG.error("Failed to find tweets for search term {}", searchTerm, e);
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<Tweet> findById(long tweetId) {
        try {
            return Optional.of(twitterTemplate.timelineOperations().getStatus(tweetId));
        } catch (Exception e) {
            LOG.error("Failed to get tweet with id {}", tweetId, e);
            return Optional.empty();
        }
    }

}

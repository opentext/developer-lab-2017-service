/**
 * Copyright © 2017 Open Text.  All Rights Reserved.
 */
package com.opentext.appworks.developer.lab.services;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.twitter.api.SearchResults;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.impl.TwitterTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TwitterClientServiceImpl implements TwitterClientService {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterClientServiceImpl.class);

    private final TwitterTemplate twitterTemplate;

    /**
     * We only want one of these per service, we'd normally place it in a singleton.
     */
    private static final GeoApiContext geoContext;

    static {
        // we don't pay for this key so will be rate limited I imagine
        geoContext = new GeoApiContext()
                .setApiKey("AIzaSyBrOuMDjWaZYDRBPs5OYqMVXuF_aLCM_50")
                .setQueryRateLimit(1000);
    }

    @Autowired
    public TwitterClientServiceImpl(TwitterTemplate twitterTemplate) {
        this.twitterTemplate = twitterTemplate;
    }

    @Override
    public List<TweetWithLocation> search(String searchTerm, boolean requiresMedia) {
        try {
            SearchResults search = twitterTemplate.searchOperations().search(searchTerm);
            return search.getTweets()
                    .stream()
                    .filter(tweet -> !requiresMedia || tweet.hasMedia())
                    // convert
                    .map(tweet -> new TweetWithLocation(tweet, getLocation(tweet)))
                    // remove those Tweets whose users have an unknown location
                    .filter(tweetWithLocation -> tweetWithLocation.getLatLng() != null)
                    .parallel()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOG.error("Failed to find tweets for search term {}", searchTerm, e);
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<TweetWithLocation> findById(long tweetId) {
        try {
            Tweet status = twitterTemplate.timelineOperations().getStatus(tweetId);
            return Optional.of(new TweetWithLocation(status, getLocation(status)));
        } catch (Exception e) {
            LOG.error("Failed to get tweet with id {}", tweetId, e);
            return Optional.empty();
        }
    }

    private LatLng getLocation(Tweet tweet) {
        String location = getTweetLocation(tweet);
        try {
            // ask Google's GeoCoding API for the details of the place
            GeocodingResult[] results = GeocodingApi.geocode(geoContext, location).await();
            if (results.length > 0) {
                return results[0].geometry.location;
            }
        } catch (ApiException | InterruptedException | IOException e) {
            LOG.error("We failed to search for the {} using the Google Maps API", location, e);
        }
        return null;
    }

    private String getTweetLocation(Tweet tweet) {
        String location = "";
        try {
            location = tweet.getUser().getLocation();
        } catch (Exception ignore) {
        }
        return location;
    }

}

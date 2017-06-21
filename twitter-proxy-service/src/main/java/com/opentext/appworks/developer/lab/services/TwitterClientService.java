/**
 * Copyright © 2017 Open Text.  All Rights Reserved.
 */
package com.opentext.appworks.developer.lab.services;

import com.google.maps.model.LatLng;
import org.springframework.social.twitter.api.Tweet;

import java.util.List;
import java.util.Optional;

public interface TwitterClientService {

    List<TweetWithLocation> search(String searchTerm, boolean requiresMedia);

    Optional<TweetWithLocation> findById(long tweetId);

    class TweetWithLocation {
        private Tweet tweet;
        private LatLng latLng;

        public TweetWithLocation() {

        }

        public TweetWithLocation(Tweet tweet, LatLng latLng) {
            this.tweet = tweet;
            this.latLng = latLng;
        }

        public Tweet getTweet() {
            return tweet;
        }

        public LatLng getLatLng() {
            return latLng;
        }
    }

}

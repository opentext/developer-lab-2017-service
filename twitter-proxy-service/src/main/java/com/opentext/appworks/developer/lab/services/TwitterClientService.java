/**
 * Copyright © 2017 Open Text.  All Rights Reserved.
 */
package com.opentext.appworks.developer.lab.services;

import org.springframework.social.twitter.api.Tweet;

import java.util.List;
import java.util.Optional;

public interface TwitterClientService {

    List<Tweet> search(String searchTerm, boolean requiresMedia);

    Optional<Tweet> findById(long tweetId);

}

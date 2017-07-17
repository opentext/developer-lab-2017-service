/**
 * Copyright © 2017 Open Text.  All Rights Reserved.
 */
package com.opentext.appworks.developer.lab.api;

import com.opentext.appworks.developer.lab.services.DefaultSearchTermSettingManager;
import com.opentext.appworks.developer.lab.services.TwitterClientService;
import com.opentext.otag.service.context.components.AWComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("tweets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TweetResource {

    private static final Logger LOG = LoggerFactory.getLogger(TweetResource.class);

    @Autowired
    private TwitterClientService twitterClientService;

    @GET
    public Response searchForTweets(@QueryParam("searchTerm") String searchTerm,
                                    @QueryParam("requiresMedia") @DefaultValue("false") String requiresMedia) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            searchTerm = getDefaultSearchTerm();
            LOG.debug("Resolved search term {}", searchTerm);
        }

        try {
            boolean hasMedia = Boolean.valueOf(requiresMedia);
            return Response.ok(twitterClientService.search(searchTerm, hasMedia)).build();
        } catch (Exception e) {
            return errorResponse(400, "Illegal (non boolean) String value supplied for \"requiresMedia\" parameter");
        }
    }

    @GET
    @Path("{tweetId}")
    public Response getTweetById(@PathParam("tweetId") String tweetId) {
        try {
            long id = Long.valueOf(tweetId);
            Optional<TwitterClientService.TweetWithLocation> byId = twitterClientService.findById(id);
            if (byId.isPresent()) {
                return Response.ok(byId.get()).build();
            } else {
                return errorResponse(404, "The tweet id provided was not recognised - " + tweetId);
            }
        } catch (NumberFormatException e) {
            return errorResponse(400, "The tweet id provided was invalid - " + tweetId);
        }
    }

    private Response errorResponse(int status, String errorMessage) {
        return Response.
                status(status)
                .entity("{ \"error\": \"" + errorMessage + "\" }")
                .build();
    }

    private String getDefaultSearchTerm() {
        String searchTerm;DefaultSearchTermSettingManager settingManager = AWComponentContext.getComponent(DefaultSearchTermSettingManager.class);
        searchTerm = settingManager.getDefaultSearchTerm();
        return searchTerm;
    }

}

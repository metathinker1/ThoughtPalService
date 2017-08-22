package com.thoughtpal.endpoints;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/")
///@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @_(@Inject))
public class DefaultResource {

    @GET()
    @Path("ping")
    @Produces(MediaType.TEXT_PLAIN)
    public String pong() {
        return "pong";
    }

}

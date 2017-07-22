package com.thoughtpal.repo;

import com.thoughtpal.model.admin.AppUser;

/**
 * Created by robertwood on 6/30/16.
 */
public interface AppUserRepository extends MongoRepository<AppUser, String> {
    AppUser findById(String id);
    AppUser findByUserName(String userName);
}

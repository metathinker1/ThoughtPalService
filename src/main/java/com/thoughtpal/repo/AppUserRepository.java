package com.thoughtpal.repo;

import com.thoughtpal.model.AppUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by robertwood on 6/30/16.
 */
@RepositoryRestResource(collectionResourceRel = "user", path = "user")
public interface AppUserRepository extends MongoRepository<AppUser, String> {
    AppUser findById(String id);
    AppUser findByUserName(String userName);
}

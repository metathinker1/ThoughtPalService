package com.thoughtpal.com.thoughtpal.repo;

import com.thoughtpal.com.thoughtpal.model.Workspace;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by robertwood on 4/30/16.
 */
public interface WorkspaceRepository extends MongoRepository<Workspace, String> {
    Workspace findById(String id);
    Workspace getByName(String workspaceName);
    List<Workspace> findByOwnerId(String ownerId);
}

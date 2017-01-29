package com.thoughtpal.repo;

import com.thoughtpal.model.admin.Workspace;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * Created by robertwood on 4/30/16.
 */
/*
 Usage:
 - Do not get by Name alone: must get by OwnerId and Name
 */
@RepositoryRestResource(collectionResourceRel = "workspaces", path = "workspaces")
public interface WorkspaceRepository extends MongoRepository<Workspace, String> {
    Workspace findById(String id);
    Workspace getByOwnerIdAndName(String ownerId, String workspaceName);
    List<Workspace> findByOwnerId(String ownerId);
}

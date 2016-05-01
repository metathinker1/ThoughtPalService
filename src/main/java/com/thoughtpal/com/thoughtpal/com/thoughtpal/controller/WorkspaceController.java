package com.thoughtpal.com.thoughtpal.com.thoughtpal.controller;

import com.thoughtpal.com.thoughtpal.model.Workspace;
import com.thoughtpal.com.thoughtpal.repo.WorkspaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by robertwood on 4/30/16.
 */
@RestController
@RequestMapping("/workspace")
public class WorkspaceController {

    @Autowired
    private WorkspaceRepository workspaceRepo;

    @RequestMapping(method = RequestMethod.POST)
    public String createWorkspace(@RequestBody Map<String, Object> workspaceMap) {
        Workspace workspace = new Workspace(workspaceMap.get("ownerId").toString(), workspaceMap.get("name").toString());
        workspaceRepo.save(workspace);
        String response = "Workspace (" + workspace + ") was saved";
        return response;
    }


}

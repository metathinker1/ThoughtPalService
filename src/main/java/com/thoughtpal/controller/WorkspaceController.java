package com.thoughtpal.controller;

import com.thoughtpal.model.admin.Workspace;
import com.thoughtpal.repo.WorkspaceRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by robertwood on 4/30/16.
 */
public class WorkspaceController {

    //@Autowired
    private WorkspaceRepository workspaceRepo;

    // TODO: Pass in userid; complete proper implementation
    //@RequestMapping(method = RequestMethod.GET)
    public List<String> getWorkspaces(String ownerId) {
        List<Workspace> workspaces = workspaceRepo.findByOwnerId(ownerId);
        List<String> workspaceNames = workspaces.stream().map(Workspace::getName).collect(Collectors.toCollection(ArrayList::new));
        return workspaceNames;
    }

    //@RequestMapping(method = RequestMethod.POST)
    public String createWorkspace(@RequestBody Map<String, Object> workspaceFields) {
        Workspace workspace = new Workspace(workspaceFields.get("ownerId").toString(), workspaceFields.get("name").toString());
        workspaceRepo.save(workspace);
        String response = "Workspace (" + workspace + ") was saved";
        return response;
    }



}

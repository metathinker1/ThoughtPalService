package com.thoughtpal.controller;

import com.thoughtpal.model.admin.AppUser;
import com.thoughtpal.repo.AppUserRepository;

import java.util.Map;

/**
 * Created by robertwood on 6/30/16.
 */

public class AppUserController {
    //@Autowired
    private AppUserRepository userRepo;

    // TODO: Pass in userid
    //@RequestMapping(method = RequestMethod.GET)
    public String getUserName() {
        return "";
    }

    //@RequestMapping(method = RequestMethod.POST)
    public String createWorkspace(@RequestBody Map<String, Object> userFields) {
        AppUser user = new AppUser(userFields.get("userName").toString(), userFields.get("email").toString());
        userRepo.save(user);
        String response = "User (" + user + ") was saved";
        return response;
    }
}

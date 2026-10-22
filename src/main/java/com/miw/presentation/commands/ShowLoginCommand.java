package com.miw.presentation.commands;

import com.miw.presentation.configuration.AccessConfigurationManager;
import com.miw.presentation.di.HttpSessionAware;
import com.miw.presentation.di.ServletRequestAware;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.List;

public class ShowLoginCommand implements Command, HttpSessionAware, ServletRequestAware {

    private HttpServletRequest request;
    private HttpSession session;

    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void setHttpSession(HttpSession session) {
        this.session = session;
    }

    @Override
    public void execute() {
        AccessConfigurationManager configuration = AccessConfigurationManager.getInstance();

        List<String> users = configuration.getUsers();

        Object currentUser = session.getAttribute("username");
        String selectedUser = currentUser instanceof String && configuration.userExists((String) currentUser)
                ? (String) currentUser : users.getFirst();

        request.setAttribute("users", users);
        request.setAttribute("selectedUser", selectedUser);
    }

}

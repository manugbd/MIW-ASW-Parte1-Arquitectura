package com.miw.presentation.commands;

import com.miw.presentation.configuration.AccessConfigurationManager;
import com.miw.presentation.di.HttpSessionAware;
import com.miw.presentation.di.ServletRequestAware;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class LoginCommand implements Command, ServletRequestAware, HttpSessionAware {
    private HttpServletRequest request;
    private HttpSession session;
    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

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
        AccessConfigurationManager configuration =
                AccessConfigurationManager.getInstance();
        request.setAttribute("users", configuration.getUsers());

        if (!configuration.userExists(username)) {
            request.setAttribute("error", true);
            request.setAttribute("errorMessage", "El usuario seleccionado no existe.");

            Object currentUser = session.getAttribute("username");

            request.setAttribute("selectedUser",
                    currentUser instanceof String && configuration.userExists((String) currentUser)
                            ? currentUser : configuration.getUsers().getFirst());
            return;
        }

        session.setAttribute("username", username);
        request.setAttribute("selectedUser", username);
        request.setAttribute("loginSuccess", true);
    }
}
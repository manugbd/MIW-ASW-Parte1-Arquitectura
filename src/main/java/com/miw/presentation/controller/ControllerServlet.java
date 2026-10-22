package com.miw.presentation.controller;

import com.miw.presentation.commands.Command;
import com.miw.presentation.di.HttpSessionAware;
import com.miw.presentation.di.ServletContextAware;
import com.miw.presentation.di.ServletRequestAware;
import com.miw.presentation.interceptors.Interceptor;
import com.miw.presentation.interceptors.InterceptorChain;
import com.miw.presentation.interceptors.InterceptorConfigurationManager;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Serial;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

@WebServlet(value = "/Controller")
public class ControllerServlet extends HttpServlet {

	@Serial
	private static final long serialVersionUID = 1L;

	protected Logger logger = LogManager.getLogger(getClass());

	Command command = null;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = req.getParameter("action");
		if (action == null || action.isEmpty()) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta la acción");
			return;
		}

		logger.debug("Executing action{}", action);

		command = ControllerConfigurationManager.getInstance().getCommand(action);

		if (command == null)
			throw new IllegalStateException("No se pudo crear el comando: " + action);

		InterceptorChain chain;
		try {
			injectDependencies(command, req, action);
			populateParameters(req);

			List<Interceptor> interceptors = InterceptorConfigurationManager.getInstance().getInterceptors(action);
			for (Interceptor interceptor : interceptors)
				injectDependencies(interceptor, req, action);

			chain = new InterceptorChain(action, command, interceptors);
			chain.proceed();
		} catch (Exception e) {
			throw new ServletException("Error ejecutando la acción " + action, e);
		}

		if (chain.isDenied()) {
			resp.sendError(HttpServletResponse.SC_FORBIDDEN, chain.getDenialMessage());
			return;
		}
		if (!chain.wasCommandExecuted())
			throw new ServletException("La cadena terminó sin ejecutar el comando");

		String forward = ControllerConfigurationManager.getInstance().getForward(action);
		req.getRequestDispatcher(forward).forward(req, resp);

	}

	private void injectDependencies(Object target, HttpServletRequest req, String action)
			throws IllegalAccessException, InvocationTargetException {
		// 2.3 Inyección por las interfaces existentes.
		if (command instanceof ServletRequestAware) {
			logger.debug("Injecting request in command {}", action);
			((ServletRequestAware) command).setServletRequest(req);
		}
		if (command instanceof HttpSessionAware) {
			logger.debug("Injecting session in command {}", action);
			((HttpSessionAware) command).setHttpSession(req.getSession());
		}
		if (command instanceof ServletContextAware) {
			logger.debug("Injecting session in command {}", action);
			((ServletContextAware) command).setServletContext(req.getServletContext());
		}

		// 2.3 Inyección del logger por setter, descubierto mediante reflexión.
		Method setter;
		try {
			setter = target.getClass().getMethod("setLogger", Logger.class);
		} catch (NoSuchMethodException e) {
			return;
		}
		setter.invoke(target, LogManager.getLogger(target.getClass()));
	}

	private void populateParameters(HttpServletRequest req) {
		for (String param : req.getParameterMap().keySet()) {

			String setterName = "set" + Character.toUpperCase(param.charAt(0)) + param.substring(1);
			try {

				Method m = command.getClass().getMethod(setterName, String.class);
                logger.debug("Found expected parameter set{}{}, populating the value", param.substring(0, 1).toUpperCase(), param.substring(1));
                try {
                    m.invoke(command, req.getParameter(param));
                } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
                    e.printStackTrace();
                }
            } catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doGet(req, resp);
	}
}

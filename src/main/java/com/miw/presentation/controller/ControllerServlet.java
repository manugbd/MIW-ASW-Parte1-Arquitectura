package com.miw.presentation.controller;

import com.miw.presentation.commands.Command;
import com.miw.presentation.configuration.ControllerConfigurationManager;
import com.miw.presentation.configuration.InterceptorConfigurationManager;
import com.miw.presentation.di.DependencyInjector;
import com.miw.presentation.di.impl.DependencyInjectorImpl;
import com.miw.presentation.interceptors.Interceptor;
import com.miw.presentation.interceptors.InterceptorExecutor;
import com.miw.presentation.interceptors.InvocationContext;
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

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = req.getParameter("action");
		if (action == null || action.isEmpty()) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta la accion");
			return;
		}

		logger.debug("Executing action{}", action);

		Command command = ControllerConfigurationManager.getInstance().getCommand(action);

		if (command == null)
			throw new IllegalStateException("No se pudo crear el comando: " + action);

		InvocationContext context;
		try {
			DependencyInjector dependencyInjector = DependencyInjectorImpl.getInstance();
			dependencyInjector.inject(command, req);
			populateParameters(command,req);

			List<Interceptor> interceptors = InterceptorConfigurationManager.getInstance().getInterceptors(action);
			for (Interceptor interceptor : interceptors)
				dependencyInjector.inject(interceptor, req);

			context = new InvocationContext(action, command.getClass().getName());
			new InterceptorExecutor().execute(command, context, interceptors);
		} catch (Exception e) {
			throw new ServletException("Error ejecutando la accion " + action, e);
		}

		if (context.isDenied()) {
			String deniedForward = ControllerConfigurationManager.getInstance().getDeniedForward(action);
			if (deniedForward == null || deniedForward.isBlank()) {
				resp.sendError(HttpServletResponse.SC_FORBIDDEN, context.getDenialMessage());
				return;
			}
			req.setAttribute("error", true);
			req.setAttribute("errorMessage", context.getDenialMessage());
			resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
			req.getRequestDispatcher(deniedForward).forward(req, resp);
			return;
		}
		if (!context.wasCommandExecuted())
			throw new ServletException("La cadena termino sin ejecutar el comando");

		String forward = ControllerConfigurationManager.getInstance().getForward(action);
		req.getRequestDispatcher(forward).forward(req, resp);

	}

	private void populateParameters(Command command, HttpServletRequest req) {
		for (String param : req.getParameterMap().keySet()) {
			if (param.isEmpty() || "action".equals(param))
				continue;

			String setterName = "set" + Character.toUpperCase(param.charAt(0)) + param.substring(1);
			Method setter;
			try {
				setter = command.getClass().getMethod(setterName, String.class);
                logger.debug("Found expected parameter set{}{}, populating the value", param.substring(0, 1).toUpperCase(), param.substring(1));
                try {
					setter.invoke(command, req.getParameter(param));
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

package org.scada_lts.web.mvc.api;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;

/**
 * Controller for data point edition
 * 
 * @author Grzesiek Bylica grzegorz.bylica@gmail.com
 */
@Controller 
public class AuthenticationAPI {
	
	private static final Log LOG = LogFactory.getLog(AuthenticationAPI.class);
	
	private UserService userService = new UserService();
	
	@RequestMapping(value = "/api/auth/{username}/{password}", method = RequestMethod.GET)
	public @ResponseBody String setAuthentication(@PathVariable("username") String username, @PathVariable("password") String password, HttpServletRequest request) {
		LOG.info("/api/auth/{username}/{password} username:" + username);
		
		User user = userService.getUser(username);
		
		Boolean ok = null;

		if (user == null) {
			ok =  false;
		}

		if (!user.getPassword().equals(Common.encrypt(password))) {
			ok = false;
		}
		
		if (ok == null) {
			// Update the last login time.
	        userService.recordLogin(user.getId());

	        // Add the user object to the session. This indicates to the rest
	        // of the application whether the user is logged in or not.
	        Common.setUser(request, user);
	        if (LOG.isDebugEnabled()) {
	        	LOG.debug("User object added to session");
	        }
	        ok = new Boolean(true);
		}

		ObjectMapper mapper = new ObjectMapper();
		String json = null;
		try {
			json = mapper.writeValueAsString(ok);
		} catch (JsonProcessingException e) {
			LOG.error(e);
		}
		return json;
		
	}
	
	@RequestMapping(value = "/api/auth/isLogged/{username}", method = RequestMethod.GET)
	public @ResponseBody String checkIsLogged(@PathVariable("username") String username, HttpServletRequest request) {
		
		LOG.info("/api/auth/isLogged/{username} username:"+username);
		
		User user = userService.getUser(username);
		
		Boolean ok = null;
		
		User userInServer = Common.getUser(request);
		
		if ( 
				(user != null && userInServer != null) &&
				(user.getUsername().equals(userInServer.getUsername()))
		) {
			ok = true;
		} else {
			ok = false;
		}
		
		ObjectMapper mapper = new ObjectMapper();
		String json = null;
		try {
			json = mapper.writeValueAsString(ok);
		} catch (JsonProcessingException e) {
			LOG.error(e);
		}
		return json;

	}
	
	
}

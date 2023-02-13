package com.increff.pos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import com.increff.pos.model.InfoData;
import com.increff.pos.util.SecurityUtil;
import com.increff.pos.util.UserPrincipal;

@Controller
public abstract class AbstractUiController {

	@Autowired
	private InfoData info;

	@Value("${app.baseUrl}")
	private String baseUrl;

	protected ModelAndView mav(String page) {
		// Get current user
		UserPrincipal principal = SecurityUtil.getPrincipal();

		info.setEmail(principal == null ? "" : principal.getEmail());
		info.setRole(getRole());

		// Set info
		ModelAndView mav = new ModelAndView(page);
		mav.addObject("info", info);
		mav.addObject("baseUrl", baseUrl);
		mav.addObject("role",info.getRole());
		return mav;
	}

	private static String getRole() {
		Authentication auth = SecurityUtil.getAuthentication();
		if (auth == null || !auth.isAuthenticated()) {
			return "";
		}

		boolean isSupervisor = auth.getAuthorities()
				.stream()
				.anyMatch(it -> it.getAuthority().equalsIgnoreCase("supervisor"));

		return isSupervisor ? "supervisor" : "operator";
	}

}

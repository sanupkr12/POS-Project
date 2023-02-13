package com.increff.pos.controller;

import com.increff.pos.model.InfoData;
import com.increff.pos.util.SecurityUtil;
import com.increff.pos.util.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SiteUiController extends AbstractUiController {

	@Autowired
	private InfoData info;

	// WEBSITE PAGES
	@RequestMapping(value = "")
	public ModelAndView index() {
		return new ModelAndView("redirect:/site/login");
	}

	@RequestMapping(value = "/site/login")
	public ModelAndView login() {

		if(!info.getEmail().equals(""))
		{

			return mav("redirect:/ui/home");
		}


		return mav("login.html");
	}

	@RequestMapping(value="/site/signup")
	public ModelAndView signup(){
		if(!info.getEmail().equals(""))
		{

			return mav("redirect:/ui/home");
		}

		info.setMessage("");

		return mav("signup.html");
	}

	@RequestMapping(value = "/site/logout")
	public ModelAndView logout() {

		return mav("logout.html");
	}

	@RequestMapping(value = "/site/pricing")
	public ModelAndView pricing() {

		return mav("pricing.html");
	}

	@RequestMapping(value = "/site/features")
	public ModelAndView features() {

		return mav("features.html");
	}

}

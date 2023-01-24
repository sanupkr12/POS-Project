package com.increff.pos.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AppUiController extends AbstractUiController {

	@RequestMapping(value = "/ui/home")
	public ModelAndView home() {
		return mav("home.html");
	}

	@RequestMapping(value = "/ui/brand")
	public ModelAndView brand() {
		return mav("brand.html");
	}

	@RequestMapping(value = "/ui/products")
	public ModelAndView product() {
		return mav("products.html");
	}

	@RequestMapping(value = "/ui/inventory")
	public ModelAndView inventory() {
		return mav("inventory.html");
	}

	@RequestMapping(value = "/ui/supervisor")
	public ModelAndView admin() {
		return mav("user.html");
	}

	@RequestMapping(value = "/ui/order/{id}")
	public ModelAndView orderItem() {
		return mav("orderItem.html");
	}


	@RequestMapping(value = "/ui/order")
	public ModelAndView order() {
		return mav("order.html");
	}




	@RequestMapping(value="ui/report/inventory")
	public ModelAndView inventoryReport(){return mav("inventoryReport.html");}

	@RequestMapping(value="ui/report/brand")
	public ModelAndView brandReport(){return mav("brandReport.html");}

	@RequestMapping(value="ui/report/sales")
	public ModelAndView salesReport(){return mav("salesReport.html");}

	@RequestMapping(value="ui/report/daySales")
	public ModelAndView daySalesReport(){return mav("daySalesReport.html");}

	@RequestMapping(value="ui/report")
	public ModelAndView report(){return mav("report.html");}



}

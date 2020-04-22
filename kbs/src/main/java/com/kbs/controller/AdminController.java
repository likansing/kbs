package com.kbs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.kbs.model.Platform;
import com.kbs.repository.PlatformRepository;

@Controller
public class AdminController {

	@Autowired
	PlatformRepository platformRepository;

	@RequestMapping("**/admin")
	public ModelAndView adminPage() {
		ModelAndView modelAndView = new ModelAndView("admin/admin");
		Iterable<Platform> platformIterator = platformRepository.findAll();
		modelAndView.addObject("platformList", platformIterator);
		modelAndView.addObject("platformObj", new Platform());
		return modelAndView;
	}

	@RequestMapping(method = RequestMethod.POST, value = "**/saveplatform")
	public ModelAndView savePlatform(Platform platform) {

		platformRepository.save(platform);
		ModelAndView modelAndView = new ModelAndView("admin/admin");
		Iterable<Platform> platformIterator = platformRepository.findAll();
		modelAndView.addObject("platformList", platformIterator);
		modelAndView.addObject("platformObj", new Platform());
		return modelAndView;
	}

}

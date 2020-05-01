package com.kbs.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kbs.model.Platform;
import com.kbs.repository.PlatformRepository;
import com.kbs.repository.service.PlatformReadFileService;

@Controller
public class AdminController {

	@Autowired
	PlatformRepository platformRepository;
	
	@Autowired
	private PlatformReadFileService platformReadFileService;

	@RequestMapping("**/admin")
	public ModelAndView adminPage() {
		ModelAndView modelAndView = new ModelAndView("admin/admin");
//		Iterable<Platform> platformIterator = platformRepository.findAll();
		modelAndView.addObject("platformList", platformRepository.findAll(PageRequest.of(0, 5, Sort.by("id").descending())));
		modelAndView.addObject("platformObj", new Platform());
		return modelAndView;
	}
	
	@GetMapping("**/platformpage")
	public ModelAndView paginacao(@PageableDefault(size = 5,sort = "id",direction = Direction.DESC) Pageable pageable, ModelAndView model){
			
		Page<Platform> pagePlatform = platformRepository.findPlatformByPage(pageable);
		model.addObject("platformList", pagePlatform);
		model.addObject("platformObj", new Platform());
		model.setViewName("admin/admin");
		return model;
	}

	@RequestMapping(method = RequestMethod.POST, value = "**/saveplatform")
	public String savePlatform(Platform platform, Model model, RedirectAttributes redirectAttributes) {

		platformRepository.save(platform);
//		Iterable<Platform> platformIterator = platformRepository.findAll();
		model.addAttribute("platformList", platformRepository.findAll(PageRequest.of(0, 5, Sort.by("id").descending())));
		model.addAttribute("platformObj", new Platform());
		return "redirect:/admin";
	}
	
	@PostMapping(value = "**/fileupload")
	public String uploadFile(@ModelAttribute Platform platform, RedirectAttributes redirectAttributes) {
		
		boolean isFlag = platformReadFileService.saveDataFromUploadFile(platform.getFile());
		
		if(isFlag) {
			redirectAttributes.addFlashAttribute("msg", "File upload Successfully!");
		} else {
			redirectAttributes.addFlashAttribute("msg", "File upload Failed. Please try again.");
		}
		return "redirect:/admin";
	}
	
	@GetMapping("**/deleteplatform/{platformItemId}")
	public ModelAndView deleteplatform(@PathVariable("platformItemId") Long platformItemId) {
		
		ModelAndView modelAndView = new ModelAndView("admin/admin");
		try {
			platformRepository.deleteById(platformItemId);
		} catch (Exception e) {
			modelAndView.addObject("msg", "Cannot delete this item! There is data related to it in Databse.");
			e.printStackTrace();
		} finally {
			
//			Iterable<Platform> platformIterator = platformRepository.findAll();
			modelAndView.addObject("platformList", platformRepository.findAll(PageRequest.of(0, 5, Sort.by("id").descending())));
			modelAndView.addObject("platformObj", new Platform());
		}
		
		return modelAndView;
	}
	
	@GetMapping("**/editplatform/{platformItemId}")
	public ModelAndView editKnowledge(@PathVariable("platformItemId") Long platformItemId) {
		Optional<Platform> platform = platformRepository.findById(platformItemId);
		ModelAndView modelAndView = new ModelAndView("admin/admin");
//		Iterable<Platform> platformIterator = platformRepository.findAll();
		modelAndView.addObject("platformObj", platform.get());
		modelAndView.addObject("platformList", platformRepository.findAll(PageRequest.of(0, 5, Sort.by("id").descending())));
		return modelAndView;
	}

}

package com.kbs.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.kbs.model.Commodity;
import com.kbs.model.Knowledge;
import com.kbs.model.Platform;
import com.kbs.model.RegionCountry;
import com.kbs.repository.CommodityRepository;
import com.kbs.repository.DinamicHql;
import com.kbs.repository.KnowledgeRepository;
import com.kbs.repository.PlatformRepository;
import com.kbs.repository.RegionCountryRepository;

@Controller
public class KnowledgeController {
	
//	@Autowired
//	private KnowledgeService service;
	
	@Autowired
	KnowledgeRepository knowledgeRepository;

	@Autowired
	PlatformRepository platformRepository;

	@Autowired
	CommodityRepository commodityRepository;

	@Autowired
	RegionCountryRepository regionCountryRepository;
	
	@Autowired
	DinamicHql dinamicHql;


	@RequestMapping("/")
	public ModelAndView index() {
		
//		Knowledge kInicial = new Knowledge();
//		Commodity cInicial = new Commodity();
//		RegionCountry rcInicial = new RegionCountry();
//		
//		cInicial.setId(2L);
//		rcInicial.setId(2L);
//		kInicial.setCommodity(cInicial);
//		kInicial.setRegionCountry(rcInicial);
		
		ModelAndView modelAndView = new ModelAndView("/index");
		modelAndView.addObject("knowledgeList", knowledgeRepository.findAll(PageRequest.of(0, 5, Sort.by("id").descending())));
		modelAndView.addObject("commodities", commodityRepository.findAll());
		modelAndView.addObject("knowledgeObj", new Knowledge());
		modelAndView.addObject("countries", regionCountryRepository.findAll());
		return modelAndView;
	}

	@RequestMapping("**/index")
	public ModelAndView index2() {
		
		
		
		ModelAndView modelAndView = new ModelAndView("/index");
		modelAndView.addObject("knowledgeList", knowledgeRepository.findAll(PageRequest.of(0, 5, Sort.by("id").descending())));
		modelAndView.addObject("commodities", commodityRepository.findAll());
		modelAndView.addObject("knowledgeObj", new Knowledge());
		modelAndView.addObject("countries", regionCountryRepository.findAll());
		return modelAndView;
	}
	
	@GetMapping("**/registerknowledge")
	public ModelAndView registerKnowledge() {
		ModelAndView modelAndView = new ModelAndView("register/registerknowledge");
		modelAndView.addObject("knowledgeObj", new Knowledge());
		
		Iterable<Platform> platforms = platformRepository.findAll();
		
		for (Platform platform : platforms) {
			platform.setPlatformName(platform.getPlatformName() + " " + platform.getMarketName());
		}
		
		modelAndView.addObject("platforms", platforms);
//		modelAndView.addObject("commodities", commodityRepository.findAll());
		modelAndView.addObject("commodities", commodityRepository.searchAllUnlessId1());
//		modelAndView.addObject("countries", regionCountryRepository.findAll());
		modelAndView.addObject("countries", regionCountryRepository.searchAllUnlessId1());
		return modelAndView;
	}

	@RequestMapping(method = RequestMethod.POST, value = "**/saveknowledge", consumes = {"multipart/form-data"})
	public ModelAndView saveKnowledge(@Valid Knowledge knowledge, BindingResult bindingResult,
			final MultipartFile file) throws IOException {

		if (bindingResult.hasErrors()) {
			ModelAndView modelAndView = new ModelAndView("register/registerknowledge");
			modelAndView.addObject("knowledgeObj", knowledge);

			List<String> msg = new ArrayList<String>();
			for (ObjectError objectError : bindingResult.getAllErrors()) {
				msg.add(objectError.getDefaultMessage()); // vem das anotacoes do model @notempty por exemplo
			}
			modelAndView.addObject("msg", msg);
			return modelAndView;
		}

		Optional<Platform> platform = platformRepository.findById((knowledge).getPlatform().getId());
		Optional<Commodity> commodity = commodityRepository.findById((knowledge).getCommodity().getId());
		Optional<RegionCountry> rc = regionCountryRepository.findById((knowledge).getRegionCountry().getId());
		
		knowledge.setCommodity(commodity.get());
		knowledge.setRegionCountry(rc.get());
		knowledge.setPlatform(platform.get());
		
		/*check if has attachment*/
		if(file.getSize() > 0) {
			knowledge.setAttachment(file.getBytes());
			knowledge.setAttachementFileType(file.getContentType());
			knowledge.setAttachementFileName(file.getOriginalFilename());
		} else {
			if(knowledge.getId() != null && knowledge.getId() > 0) { //edit case
				Knowledge knowledgeTemp = knowledgeRepository.findById(knowledge.getId()).get();
				knowledge.setAttachment(knowledgeTemp.getAttachment());
				knowledge.setAttachementFileType(knowledgeTemp.getAttachementFileType());
				knowledge.setAttachementFileName(knowledgeTemp.getAttachementFileName());
			}
		}
		
		knowledgeRepository.save(knowledge);
		ModelAndView modelAndView = new ModelAndView("/index");
		modelAndView.addObject("knowledgeList", knowledgeRepository.findAll(PageRequest.of(0, 5, Sort.by("id").descending())));
		modelAndView.addObject("commodities", commodityRepository.findAll());
		modelAndView.addObject("knowledgeObj", new Knowledge());
		modelAndView.addObject("countries", regionCountryRepository.findAll());
		return modelAndView;
	}

	@GetMapping("**/editknowledge/{knowledgeItemId}")
	public ModelAndView editKnowledge(@PathVariable("knowledgeItemId") Long knowledgeItemId) {

		Optional<Knowledge> knowledge = knowledgeRepository.findById(knowledgeItemId);
		ModelAndView modelAndView = new ModelAndView("register/registerknowledge");
		modelAndView.addObject("knowledgeObj", knowledge.get());
		modelAndView.addObject("platforms", platformRepository.findAll());
		modelAndView.addObject("commodities", commodityRepository.findAll());
		modelAndView.addObject("countries", regionCountryRepository.findAll());
		return modelAndView;
	}
	
	@GetMapping("**/downloadknowledge/{knowledgeItemId}")
	public void downloadknowledge(@PathVariable("knowledgeItemId") Long knowledgeItemId, HttpServletResponse response ) throws IOException{
		
		/*query obj*/
		Knowledge knowledge = knowledgeRepository.findById(knowledgeItemId).get();
		
		if(knowledge.getAttachment() != null) {
			/*set response lenght*/
			response.setContentLength(knowledge.getAttachment().length);
			
			/*set content type - type of the file*/
			/*for generic use as response.setContentType("application/octet-stream"); */
			response.setContentType(knowledge.getAttachementFileType());
			
			/*set header to response*/
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", knowledge.getAttachementFileName());
			response.setHeader(headerKey, headerValue);
			
			/*finalize response adding attachemnet file*/
			response.getOutputStream().write(knowledge.getAttachment());
		}
	}

	@GetMapping("/deleteknowledge/{knowledgeItemId}")
	public ModelAndView deleteKnowledge(@PathVariable("knowledgeItemId") Long knowledgeItemId) {
		
		knowledgeRepository.deleteById(knowledgeItemId);
		ModelAndView modelAndView = new ModelAndView("/index");
		modelAndView.addObject("knowledgeList", knowledgeRepository.findAll(PageRequest.of(0, 5, Sort.by("id").descending())));
		modelAndView.addObject("commodities", commodityRepository.findAll());
		modelAndView.addObject("knowledgeObj", new Knowledge());
		modelAndView.addObject("countries", regionCountryRepository.findAll());
		return modelAndView;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "**/pag")
	//@GetMapping("**/pag")
	public ModelAndView paginacao(@PageableDefault(size = 5,sort = "id",direction = Direction.DESC) Pageable pageable, ModelAndView model,
			@RequestParam("searchWord") String searchWord, //campo livre de digitacao para busca de platform
			@RequestParam("platforminfo") String platforminfo, //recebe string sendo platformName, marketName ou sysid
			@RequestParam("countryinfo") String countryinfo,     //recebe id do pais em string
			@RequestParam("commodityinfo") String commodityinfo, // recebe id da commmodity em string
			@RequestParam("statusinfo") String statusinfo,  // recebe String OPEN or CLOSED
			@RequestParam("severityinfo") String severityinfo,     // recebe String LOW_IMPACY, MEDIUM_IMPACT OR HIGH_IMPACT
			@RequestParam("titledescriptioninfo") String titledescriptioninfo,	//recebe String do campo title and or Description (title, description or both)
			@RequestParam("titledescriptionkeyword") String titledescriptionkeyword){    //campo lire de tile ou description
			
		
		Knowledge k = new Knowledge();
		Commodity c = new Commodity();
		RegionCountry rc = new RegionCountry();
		Platform p = new Platform();
		Knowledge kAnswer = new Knowledge();
		
		if(countryinfo.equalsIgnoreCase("1") && countryinfo != null) {
			rc = regionCountryRepository.findById(1L).get();
			kAnswer.setRegionCountry(rc);
			rc = new RegionCountry();
		} else {
			rc = regionCountryRepository.findById(Long.parseLong(countryinfo)).get();
			kAnswer.setRegionCountry(rc);
		}
		
//		if(countryinfo != null && !countryinfo.isEmpty()) {
//			rc = regionCountryRepository.findById(Long.parseLong(countryinfo)).get();
//			kAnswer.setRegionCountry(rc);
//		}
		
		
		if(commodityinfo.equalsIgnoreCase("1") && commodityinfo != null) {
			c = commodityRepository.findById(1L).get();
			kAnswer.setCommodity(c);
			c = new Commodity();
		} else {
			c = commodityRepository.findById(Long.parseLong(commodityinfo)).get();
			kAnswer.setCommodity(c);
		}
		
//		if(commodityinfo != null && !commodityinfo.isEmpty()) {
//			c = commodityRepository.findById(Long.parseLong(commodityinfo)).get();
//			kAnswer.setCommodity(c);
//		}
		
		
		//platforminfo recebe string sendo platformName, marketName ou sysid
		// searchWord contem o digitado para busca
		
		if(platforminfo != null && platforminfo.equalsIgnoreCase("platformName")) {
			p.setPlatformName(searchWord);
		} else if(platforminfo != null && platforminfo.equalsIgnoreCase("marketName")) {
			p.setMarketName(searchWord);
		} else if(platforminfo != null && platforminfo.equalsIgnoreCase("sysid")) {
			p.setSysid(searchWord);
		}
		
		//recebe String do campo title and or Description (title, description or both)
		if(titledescriptioninfo != null && titledescriptioninfo.equalsIgnoreCase("title")) {
			k.setTitle(titledescriptionkeyword);
		} else if (titledescriptioninfo != null && titledescriptioninfo.equalsIgnoreCase("description")) {
			k.setDescription(titledescriptionkeyword);
		} else if (titledescriptioninfo != null && titledescriptioninfo.equalsIgnoreCase("both")){
			k.setTitle(titledescriptionkeyword);
			k.setDescription(titledescriptionkeyword);
		}
		
		// recebe String OPEN or CLOSED
		if(statusinfo != null && !statusinfo.isEmpty()) {
			k.setStatus(statusinfo);
		}
				
		// recebe String LOW_IMPACY, MEDIUM_IMPACT OR HIGH_IMPACT
		if(severityinfo != null && !severityinfo.isEmpty()) {
			k.setSeverity(severityinfo);
		}
		
		Page<Knowledge> pageKnowledge = knowledgeRepository.findKnowledgeByPage(k, c, p, rc, pageable);
		model.addObject("knowledgeList", pageKnowledge);
		model.addObject("commodities", commodityRepository.findAll());
		model.addObject("knowledgeObj", kAnswer);
		model.addObject("countries", regionCountryRepository.findAll());
		model.addObject("searchWord", searchWord);
		model.addObject("platforminfo", platforminfo);
		model.addObject("titledescriptioninfo", titledescriptioninfo);
		model.addObject("titledescriptionkeyword", titledescriptionkeyword);
		model.addObject("commodityinfo", commodityinfo);
		model.addObject("countryinfo", countryinfo);
		model.addObject("statusinfo", statusinfo);
		model.addObject("severityinfo", severityinfo);
		model.setViewName("index");
		return model;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "**/searchknowledge")
	public ModelAndView search(Knowledge knowledgeRecebido,
			@RequestParam("searchWord") String searchWord, //campo livre de digitacao para busca de platform
			@RequestParam("platforminfo") String platforminfo, //recebe string sendo platformName, marketName ou sysid
			//@RequestParam("countryinfo") String countryinfo,     //recebe id do pais em string
			//@RequestParam("commodityinfo") String commodityinfo, // recebe id da commmodity em string
			@RequestParam("statusinfo") String statusinfo,  // recebe String OPEN or CLOSED
			@RequestParam("severityinfo") String severityinfo,     // recebe String LOW_IMPACY, MEDIUM_IMPACT OR HIGH_IMPACT
			@RequestParam("titledescriptioninfo") String titledescriptioninfo,	//recebe String do campo title and or Description (title, description or both)
			@RequestParam("titledescriptionkeyword") String titledescriptionkeyword,    //campo lire de tile ou description
			@PageableDefault(size = 5, sort = "id",direction = Direction.DESC) Pageable pageable){
		
		
		//teste para verificar qdo nao escolhe - vem empty!
//		System.out.println("platforminfo=["+platforminfo+"]");
//		System.out.println("searchWord=["+searchWord+"]");
//		System.out.println("titledescriptioninfo=["+titledescriptioninfo+"]");
//		System.out.println("titledescriptionkeyword=["+titledescriptionkeyword+"]");
//		System.out.println("commodityinfo["+commodityinfo+"]");
//		System.out.println("countryinfo=["+countryinfo+"]");
//		System.out.println("statusinfo=["+statusinfo+"]");
//		System.out.println("severityinfo=["+severityinfo+"]");
//		
//		if(platforminfo.isEmpty()) {
//			System.out.println("vazio!");
//		}
		
		ModelAndView modelAndView = new ModelAndView("/index");
		
		Commodity commodityFromSearch = new Commodity();
		RegionCountry rcFromSearch = new RegionCountry();
		Knowledge kAnswer = new Knowledge();
		
		if(knowledgeRecebido.getRegionCountry().getId().equals(1L) && knowledgeRecebido.getRegionCountry() != null) {
			rcFromSearch = regionCountryRepository.findById(1L).get();
			kAnswer.setRegionCountry(rcFromSearch);
			rcFromSearch = new RegionCountry();
		} else {
			rcFromSearch = regionCountryRepository.findById(knowledgeRecebido.getRegionCountry().getId()).get();
			kAnswer.setRegionCountry(rcFromSearch);
		}
		
//		if(knowledgeRecebido.getRegionCountry() != null) {
//			rcFromSearch = regionCountryRepository.findById(knowledgeRecebido.getRegionCountry().getId()).get();
//			kAnswer.setRegionCountry(rcFromSearch);
//		}
		
		if(knowledgeRecebido.getCommodity().getId().equals(1L) && knowledgeRecebido.getCommodity() != null) {
			commodityFromSearch = commodityRepository.findById(1L).get();
			kAnswer.setCommodity(commodityFromSearch);
			commodityFromSearch = new Commodity();
		} else {
			commodityFromSearch = commodityRepository.findById(knowledgeRecebido.getCommodity().getId()).get();
			kAnswer.setCommodity(commodityFromSearch);
		}
		
//		if(knowledgeRecebido.getCommodity() != null) {
//			commodityFromSearch = commodityRepository.findById(knowledgeRecebido.getCommodity().getId()).get();
//			kAnswer.setCommodity(commodityFromSearch);
//		}
		
		Knowledge k = new Knowledge();
//		k.setCommodity(commodityFromSearch);
//		k.setCommodity(commodityFromSearch);
		Platform p = new Platform();
		
		//platforminfo recebe string sendo platformName, marketName ou sysid
		// searchWord contem o digitado para busca
		if(platforminfo != null && platforminfo.equalsIgnoreCase("platformName")) {
			p.setPlatformName(searchWord);
		} else if(platforminfo != null && platforminfo.equalsIgnoreCase("marketName")) {
			p.setMarketName(searchWord);
		} else if(platforminfo != null && platforminfo.equalsIgnoreCase("sysid")) {
			p.setSysid(searchWord);
		}
		
		//recebe String do campo title and or Description (title, description or both)
		if(titledescriptioninfo != null && titledescriptioninfo.equalsIgnoreCase("title")) {
			k.setTitle(titledescriptionkeyword);
		} else if (titledescriptioninfo != null && titledescriptioninfo.equalsIgnoreCase("description")) {
			k.setDescription(titledescriptionkeyword);
		} else if (titledescriptioninfo != null && titledescriptioninfo.equalsIgnoreCase("both")){
			k.setTitle(titledescriptionkeyword);
			k.setDescription(titledescriptionkeyword);
		}
		
		// recebe String OPEN or CLOSED
		if(statusinfo != null && !statusinfo.isEmpty()) {
			k.setStatus(statusinfo);
		}
				
		// recebe String LOW_IMPACY, MEDIUM_IMPACT OR HIGH_IMPACT
		if(severityinfo != null && !severityinfo.isEmpty()) {
			k.setSeverity(severityinfo);
		}
		
		Page<Knowledge> filteredList = null;
		filteredList = knowledgeRepository.findKnowledgeByPage(k, commodityFromSearch, p, rcFromSearch, pageable);
		
		modelAndView.addObject("knowledgeList", filteredList);
		modelAndView.addObject("knowledgeObj", kAnswer);
		modelAndView.addObject("commodities", commodityRepository.findAll());
		modelAndView.addObject("countries", regionCountryRepository.findAll());
		modelAndView.addObject("searchWord", searchWord);
		modelAndView.addObject("platforminfo", platforminfo);
		modelAndView.addObject("titledescriptioninfo", titledescriptioninfo);
		modelAndView.addObject("titledescriptionkeyword", titledescriptionkeyword);
//		modelAndView.addObject("commodityinfo", commodityinfo);
//		modelAndView.addObject("countryinfo", countryinfo);
		modelAndView.addObject("statusinfo", statusinfo);
		modelAndView.addObject("severityinfo", severityinfo);
		return modelAndView;
	}

	@GetMapping("**/viewknowledge/{knowledgeItemId}")
	public ModelAndView viewKnowledge(@PathVariable("knowledgeItemId") Long knowledgeItemId) {

		Optional<Knowledge> knowledge = knowledgeRepository.findById(knowledgeItemId);
		ModelAndView modelAndView = new ModelAndView("register/viewknowledge");
		modelAndView.addObject("knowledgeObj", knowledge.get());
		return modelAndView;
	}
//	//, produces = MediaType.APPLICATION_JSON_VALUE
//	@RequestMapping(value = "**/productchange", method = RequestMethod.POST)
//	@ResponseBody
//	public String updatePlatformMarktnameCombo (HttpServletRequest req, HttpServletResponse res) {
//		
//		Optional<Platform> platform = platformRepository.findById(Long.parseLong(req.getParameter("id")));
//		return platform.get().toString();
//	}

}

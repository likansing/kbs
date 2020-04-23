package com.kbs.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
		ModelAndView modelAndView = new ModelAndView("/index");
//		modelAndView.addObject("knowledgeList", knowledgeRepository.findAll(PageRequest.of(0, 5,Sort.by("id").descending())));
		Iterable<Knowledge> knowledgeIterator = knowledgeRepository.sortAllByIdOrderByDESC();
		modelAndView.addObject("knowledgeList", knowledgeIterator);
		modelAndView.addObject("commodities", commodityRepository.findAll());
//		modelAndView.addObject("commodityobj", new Commodity());
		modelAndView.addObject("knowledgeObj", new Knowledge());
		modelAndView.addObject("countries", regionCountryRepository.findAll());
		return modelAndView;
	}

	@RequestMapping("**/index")
	public ModelAndView index2() {
		ModelAndView modelAndView = new ModelAndView("/index");
//		modelAndView.addObject("knowledgeList", knowledgeRepository.findAll(PageRequest.of(0, 5,Sort.by("id").descending())));
		Iterable<Knowledge> knowledgeIterator = knowledgeRepository.sortAllByIdOrderByDESC();
		modelAndView.addObject("knowledgeList", knowledgeIterator);
		modelAndView.addObject("commodities", commodityRepository.findAll());
		modelAndView.addObject("knowledgeObj", new Knowledge());
		modelAndView.addObject("countries", regionCountryRepository.findAll());
		return modelAndView;
	}
	
	@GetMapping("**/pageknowledge")
	public ModelAndView knowledgePaginatorLoader(@PageableDefault(size = 5) Pageable pageable, ModelAndView modelAndView) {
		
		Page<Knowledge> pageKnowledge = knowledgeRepository.findAll(pageable);
		modelAndView.addObject("knowledgeList", pageKnowledge);
		modelAndView.addObject("commodities", commodityRepository.findAll());
		modelAndView.addObject("knowledgeObj", new Knowledge());
		modelAndView.addObject("countries", regionCountryRepository.findAll());
		modelAndView.setViewName("index");
		return modelAndView;
	}

	// @RequestMapping(method = RequestMethod.GET, value = "**/registerknowledge")
	@GetMapping("**/registerknowledge")
	public ModelAndView registerKnowledge() {
		ModelAndView modelAndView = new ModelAndView("register/registerknowledge");
		modelAndView.addObject("knowledgeObj", new Knowledge());
		modelAndView.addObject("platforms", platformRepository.findAll());
		modelAndView.addObject("commodities", commodityRepository.findAll());
		modelAndView.addObject("countries", regionCountryRepository.findAll());
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
//		modelAndView.addObject("knowledgeList", knowledgeRepository.findAll(PageRequest.of(0, 5,Sort.by("id").descending())));
		Iterable<Knowledge> knowledgeIterator = knowledgeRepository.sortAllByIdOrderByDESC();
		modelAndView.addObject("knowledgeList", knowledgeIterator);
		modelAndView.addObject("commodities", commodityRepository.findAll());
		modelAndView.addObject("knowledgeObj", new Knowledge());
		modelAndView.addObject("countries", regionCountryRepository.findAll());
		return modelAndView;
	}

	// nao listando na tela de cadastro
//	@RequestMapping(method = RequestMethod.GET, value = "/listknowledge")
//	public ModelAndView listknowledge() {
//		ModelAndView modelAndView = new ModelAndView("register/registerknowledge");
//		Iterable<Knowledge> knowledgeIterator = knowledgeRepository.findAll();
//		modelAndView.addObject("knowledgeList", knowledgeIterator);
//		return modelAndView;
//	}

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
//		modelAndView.addObject("knowledgeList", knowledgeRepository.findAll(PageRequest.of(0, 5,Sort.by("id").descending())));
		Iterable<Knowledge> knowledgeIterator = knowledgeRepository.sortAllByIdOrderByDESC();
		modelAndView.addObject("knowledgeList", knowledgeIterator);
		modelAndView.addObject("commodities", commodityRepository.findAll());
		modelAndView.addObject("knowledgeObj", new Knowledge());
		modelAndView.addObject("countries", regionCountryRepository.findAll());
		return modelAndView;
	}

	@PostMapping("**/searchknowledge")
	public ModelAndView search(@RequestParam("searchWord") String searchWord, //campo livre de digitacao para busca
			@RequestParam("platforminfo") String platforminfo, //recebe string sendo platformName, marketName ou sysid
			@RequestParam("countryinfo") String countryinfo,     //recebe id do pais em string
			@RequestParam("commodityinfo") String commodityinfo, // recebe id da commmodity em string
			@RequestParam("statusinfo") String statusinfo,  // recebe String OPEN or CLLOSED
			@RequestParam("severityinfo") String severityinfo,     // recebe String LOW_IMPACY, MEDIUM_IMPACT OR HIGH_IMPACT
			@RequestParam("titledescriptioninfo") String titledescriptioninfo,	//recebe String do campo title and or Description (title, description or both)
			@RequestParam("titledescriptionkeyword") String titledescriptionkeyword){   //campo lire de tile ou description
		

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
		
		
		int controle = 0;
		ModelAndView modelAndView = new ModelAndView("/index");
		StringBuilder busca = new StringBuilder();
		busca.append("select k from Knowledge k where");
		
		if(platforminfo != null && !platforminfo.isEmpty()) { //deseja pesquisar por platforminfo
			busca.append(" LOWER (k.platform." + platforminfo + ")");
			controle = 1;
			
			if(searchWord != null && !searchWord.isEmpty()) {
				busca.append(" LIKE LOWER ('%" + searchWord + "%')");
			} else {
//				modelAndView.addObject("knowledgeList", knowledgeRepository.findAll(PageRequest.of(0, 5,Sort.by("id").descending())));
				Iterable<Knowledge> knowledgeIterator = knowledgeRepository.sortAllByIdOrderByDESC();
				modelAndView.addObject("knowledgeList", knowledgeIterator);
				modelAndView.addObject("msg", "Searching by Platform information and not inputing any keyword to filter data will list all by default.");
				modelAndView.addObject("commodities", commodityRepository.findAll());
				modelAndView.addObject("knowledgeObj", new Knowledge()); //add
				modelAndView.addObject("countries", regionCountryRepository.findAll());
				
				modelAndView.addObject("platforminfo", platforminfo);
				modelAndView.addObject("searchWord", searchWord);
				modelAndView.addObject("titledescriptioninfo", titledescriptioninfo);
				modelAndView.addObject("titledescriptionkeyword", titledescriptionkeyword);
				modelAndView.addObject("commodityinfo", commodityinfo);
				modelAndView.addObject("countryinfo", countryinfo);
				modelAndView.addObject("statusinfo", statusinfo);
				modelAndView.addObject("severityinfo", severityinfo);
				return modelAndView;
			}
		}
		
		if(titledescriptioninfo != null && !titledescriptioninfo.isEmpty()) { //deseja pesquisar por Title and or desacription ( recebe title, description or both)
			
			if(controle == 1) {
				busca.append(" and");
			}
			
			if(titledescriptionkeyword != null && !titledescriptionkeyword.isEmpty()) {
				
				if(titledescriptioninfo.equalsIgnoreCase("both")) {
					controle = 1;
					busca.append(" LOWER (k.title) LIKE LOWER ('%" + titledescriptionkeyword + "%') OR LOWER (k.description) LIKE LOWER ('%" + titledescriptionkeyword + "%')"); //precisa arrumar both
				} else {
					controle = 1;
					busca.append(" LOWER (k." + titledescriptioninfo + ") LIKE LOWER ('%" + titledescriptionkeyword + "%')");
				}
			} else {
//				modelAndView.addObject("knowledgeList", knowledgeRepository.findAll(PageRequest.of(0, 5,Sort.by("id").descending())));
				Iterable<Knowledge> knowledgeIterator = knowledgeRepository.sortAllByIdOrderByDESC();
				modelAndView.addObject("knowledgeList", knowledgeIterator);
				modelAndView.addObject("msg", "Searching by Title and or Description information and not inputing any keyword to filter data will list all by default.");
				modelAndView.addObject("commodities", commodityRepository.findAll());
				modelAndView.addObject("knowledgeObj", new Knowledge()); //add
				modelAndView.addObject("countries", regionCountryRepository.findAll());
				
				modelAndView.addObject("platforminfo", platforminfo);
				modelAndView.addObject("searchWord", searchWord);
				modelAndView.addObject("titledescriptioninfo", titledescriptioninfo);
				modelAndView.addObject("titledescriptionkeyword", titledescriptionkeyword);
				modelAndView.addObject("commodityinfo", commodityinfo);
				modelAndView.addObject("countryinfo", countryinfo);
				modelAndView.addObject("statusinfo", statusinfo);
				modelAndView.addObject("severityinfo", severityinfo);
				return modelAndView;
			}
		}
		
		if(commodityinfo != null && !commodityinfo.isEmpty()) { //deseja pesquisar por commodityinfo
			if(controle == 1) {
				busca.append(" and ");
			}
			busca.append(" k.commodity.id = " + commodityinfo);
			controle = 1;
			
			//build knowledgeObj from filter - nao fuincionando ainda
			Knowledge knowledgeObj = new Knowledge();
			Optional<Commodity> commodity = commodityRepository.findById(Long.parseLong(commodityinfo));
			knowledgeObj.setCommodity(commodity.get());
		}
		if(countryinfo != null && !countryinfo.isEmpty()) { //deseja pesquisar por countryinfo
			if(controle == 1) {
				busca.append(" and ");
			}
			busca.append(" k.regionCountry.id = " + countryinfo);
			controle = 1;
		}
		if(statusinfo != null && !statusinfo.isEmpty()) { //deseja pesquisar por statusinfo
			if(controle == 1) {
				busca.append(" and ");
			}
			busca.append(" LOWER (k.status) = LOWER ('" + statusinfo + "')");
			controle = 1;
		}
		if(severityinfo != null && !severityinfo.isEmpty()) { //deseja pesquisar por severityinfo
			if(controle == 1) {
				busca.append(" and ");
			}
			busca.append(" LOWER (k.severity) = LOWER ('" + severityinfo +"')");
			controle = 1;
		}

		String  sql = busca.toString();
		System.out.println("["+sql+"]");
		
		if(controle == 0) {
//			modelAndView.addObject("knowledgeList", knowledgeRepository.findAll(PageRequest.of(0, 5,Sort.by("id").descending())));
			Iterable<Knowledge> knowledgeIterator = knowledgeRepository.sortAllByIdOrderByDESC();
			modelAndView.addObject("knowledgeList", knowledgeIterator);
			modelAndView.addObject("msg", "Searching by not filtered data list all by default.");
			modelAndView.addObject("commodities", commodityRepository.findAll());
			modelAndView.addObject("knowledgeObj", new Knowledge()); //add
			modelAndView.addObject("countries", regionCountryRepository.findAll());
			
			modelAndView.addObject("platforminfo", platforminfo);
			modelAndView.addObject("searchWord", searchWord);
			modelAndView.addObject("titledescriptioninfo", titledescriptioninfo);
			modelAndView.addObject("titledescriptionkeyword", titledescriptionkeyword);
			modelAndView.addObject("commodityinfo", commodityinfo);
			modelAndView.addObject("countryinfo", countryinfo);
			modelAndView.addObject("statusinfo", statusinfo);
			modelAndView.addObject("severityinfo", severityinfo);
			return modelAndView;
		}
		
		
		List<Knowledge> filteredList = (List<Knowledge>) dinamicHql.executaHqlDinamic(sql);
		modelAndView.addObject("knowledgeList", filteredList);
		modelAndView.addObject("commodities", commodityRepository.findAll());
		modelAndView.addObject("knowledgeObj", new Knowledge()); //add to try to keep option os filter (not working so far)
		modelAndView.addObject("countries", regionCountryRepository.findAll());
		
		modelAndView.addObject("platforminfo", platforminfo);
		modelAndView.addObject("searchWord", searchWord);
		modelAndView.addObject("titledescriptioninfo", titledescriptioninfo);
		modelAndView.addObject("titledescriptionkeyword", titledescriptionkeyword);
		modelAndView.addObject("commodityinfo", commodityinfo);
		modelAndView.addObject("countryinfo", countryinfo);
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
	//, produces = MediaType.APPLICATION_JSON_VALUE
	@RequestMapping(value = "**/productchange", method = RequestMethod.POST)
	@ResponseBody
	public String updatePlatformMarktnameCombo (HttpServletRequest req, HttpServletResponse res) {
		
		Optional<Platform> platform = platformRepository.findById(Long.parseLong(req.getParameter("id")));
		return platform.get().toString();
	}
	

}

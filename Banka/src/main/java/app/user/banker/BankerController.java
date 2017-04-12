package app.user.banker;

import java.util.List;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import app.codeBookActivities.CodeBookActivities;
import app.codeBookActivities.CodeBookActivitiesService;
import app.country.Country;
import app.country.CountryService;

@RestController
@RequestMapping("/banker")
public class BankerController {
	
	private final BankerService bankerService;
	private final CodeBookActivitiesService codeBookActivitiesService;
	private final CountryService countryService;
	private HttpSession httpSession;
	
	@Autowired
	public BankerController(final HttpSession httpSession,final BankerService bankerService, final CodeBookActivitiesService codeBookActivitiesService, 
							final CountryService countryService) {
		this.bankerService = bankerService;
		this.codeBookActivitiesService = codeBookActivitiesService;
		this.countryService = countryService;
		this.httpSession = httpSession;
	}
	
	@GetMapping("/checkRights")
	@ResponseStatus(HttpStatus.OK)
	public Banker checkRights() throws AuthenticationException {
		try {
			return ((Banker) httpSession.getAttribute("user"));
		} catch (Exception e) {
			throw new AuthenticationException("Forbidden.");
		}
	}
	
	
	@PostMapping(path = "/update")
	@ResponseStatus(HttpStatus.OK)
	public String update(@RequestBody Banker banker) {
		
		return "OK";
		
	}
	
	
	@GetMapping("/getAllCodeBookActivities")
	@ResponseStatus(HttpStatus.OK)
	public List<CodeBookActivities> getAllCodeBookActivities() {
		
		List<CodeBookActivities> codeBookActivities = codeBookActivitiesService.findAll();

		return codeBookActivities; 
	}
	
	
	@PostMapping(path = "/addCodeBookActivity")
	@ResponseStatus(HttpStatus.CREATED)
	public void addCodeBookActivity(@RequestBody CodeBookActivities codeBookActivity) {
		codeBookActivitiesService.save(codeBookActivity);
	}
	
	@GetMapping("/getAllCountries")
	@ResponseStatus(HttpStatus.OK)
	public List<Country> getAllCountries() {
		
		List<Country> countries = countryService.findAll();

		return countries; 
	}
	
	@PostMapping(path = "/addCountry")
	@ResponseStatus(HttpStatus.CREATED)
	public void addCountry(@RequestBody Country country) {
		
		countryService.save(country);
	}
	
	
}

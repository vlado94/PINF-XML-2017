package app.user.banker;

import java.util.List;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.ws.rs.NotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import app.client.Client;
import app.client.Client.TypeOfClient;
import app.client.ClientService;
import app.codeBookActivities.CodeBookActivities;
import app.codeBookActivities.CodeBookActivitiesService;
import app.country.Country;
import app.country.CountryService;
import app.populatedPlace.PopulatedPlace;
import app.populatedPlace.PopulatedPlaceService;

@RestController
@RequestMapping("/banker")
public class BankerController {
	
	private final BankerService bankerService;
	private final CodeBookActivitiesService codeBookActivitiesService;
	private final CountryService countryService;
	private final ClientService clientService;
	private final PopulatedPlaceService populatedPlaceService;
	private HttpSession httpSession;
	
	@Autowired
	public BankerController(final HttpSession httpSession,final BankerService bankerService, final CodeBookActivitiesService codeBookActivitiesService, 
							final CountryService countryService, final ClientService clientService, final PopulatedPlaceService populatedPlaceService) {
		this.bankerService = bankerService;
		this.codeBookActivitiesService = codeBookActivitiesService;
		this.countryService = countryService;
		this.clientService = clientService;
		this.populatedPlaceService = populatedPlaceService;
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
	
	@PutMapping("/updateProfile/{id}")
	@ResponseStatus(HttpStatus.OK)
	public Banker update(@PathVariable Long id,@RequestBody Banker banker) {
		Banker bankerForEdit = bankerService.findOneById(id);
		if(bankerForEdit != null) {
			bankerForEdit.setAttributes(banker);
			banker = bankerService.save(bankerForEdit);
			return banker;		
		}
		else {
			throw new NotFoundException();
		}
	}
	
	@GetMapping("/findAllCodeBookActivities")
	@ResponseStatus(HttpStatus.OK)
	public List<CodeBookActivities> findAllCodeBookActivities() {
		return codeBookActivitiesService.findAll(); 
	}
	
	@GetMapping(path = "/findActivityById/{id}")
	@ResponseStatus(HttpStatus.OK)
	public CodeBookActivities findActivityById(@PathVariable Long id) {
		return codeBookActivitiesService.findOne(id);
	}
	
	
	@GetMapping(path = "/findActivityByName/{name}")
	@ResponseStatus(HttpStatus.OK)
	public CodeBookActivities findActivityByName(@PathVariable String name) {
		return codeBookActivitiesService.findByName(name);
		
	}
	
	
	@PostMapping(path = "/saveCodeBookActivity")
	@ResponseStatus(HttpStatus.CREATED)
	public void saveCodeBookActivity(@RequestBody CodeBookActivities codeBookActivity) {
		try {
			codeBookActivitiesService.save(codeBookActivity);
		}
		catch(Exception ex) {
			throw new NotFoundException();
		}

	}
	
	@PutMapping(path = "/updateCodeBookActivity/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public void updateCodeBookActivity(@PathVariable Long id,@RequestBody CodeBookActivities codeBookActivity) {
		CodeBookActivities codeBookActivityForUpdate = codeBookActivitiesService.findOne(id);
		if(codeBookActivityForUpdate != null) {
			codeBookActivityForUpdate.setCode(codeBookActivity.getCode());
			codeBookActivityForUpdate.setName(codeBookActivity.getName());
			codeBookActivitiesService.save(codeBookActivity);
		}
		else {
			throw new NotFoundException();
		}
	}
	
	
	@DeleteMapping(path = "/deleteCodeBookActivity/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void deleteCodeBookActivity(@PathVariable Long id) {
		codeBookActivitiesService.delete(id);
	}
	
	@GetMapping("/findAllCountries")
	@ResponseStatus(HttpStatus.OK)
	public List<Country> findAllCountries() {
		return countryService.findAll(); 
	}
	
	
	@GetMapping(path = "/findCountryById/{id}")
	@ResponseStatus(HttpStatus.OK)
	public Country findCountryById(@PathVariable Long id) {
		return countryService.findOne(id);
	}
	
	@GetMapping(path = "/findCountryByName/{name}")
	@ResponseStatus(HttpStatus.OK)
	public Country findCountryByName(@PathVariable String name) {
		return countryService.findByName(name);
		
	}
	
	@PostMapping(path = "/saveCountry")
	@ResponseStatus(HttpStatus.CREATED)
	public void saveCountry(@RequestBody Country country) {
		countryService.save(country);
	}
	
	@PutMapping(path = "/updateCountry/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public void updateCountry(@PathVariable Long id,@RequestBody Country country) {
		Country countryForUpdate = countryService.findOne(id);
		countryForUpdate.setCode(country.getCode());
		countryForUpdate.setName(country.getName());
		countryService.save(countryForUpdate);
	}
	
	@DeleteMapping(path = "/deleteCountry/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void deleteCountry(@PathVariable Long id) {
		countryService.delete(id);
	}
	
	
	@GetMapping("/findAllIndividualBills")
	@ResponseStatus(HttpStatus.OK)
	public List<Client> findAllIndividualBills() {
		return clientService.findAllIndividualBills(); 
	}
	
	@PostMapping(path = "/saveIndividualBill")
	@ResponseStatus(HttpStatus.CREATED)
	public void saveIndividualBill(@Valid @RequestBody Client client) {
		client.setType(TypeOfClient.FIZICKO);
		clientService.save(client);
	}
	
	@PutMapping(path = "/updateIndividualClient/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public void updateIndividualClient(@PathVariable Long id,@RequestBody Client client) {
		Client clientForUpdate = clientService.findOne(id);
		if(clientForUpdate != null) {
			clientForUpdate.setApplicant(client.getApplicant());
			clientForUpdate.setJmbg(client.getJmbg());
			clientForUpdate.setAddress(client.getAddress());
			clientForUpdate.setPhone(client.getPhone());
			clientForUpdate.setFax(client.getFax());
			clientForUpdate.setMail(client.getMail());
			clientForUpdate.setDeliveryAddress(client.getDeliveryAddress());
			clientForUpdate.setDeliveryByMail(client.isDeliveryByMail());
			clientForUpdate.setShortName(client.getShortName());
			clientForUpdate.setPib(client.getPib());
			clientForUpdate.setMib(client.getMib());
			clientForUpdate.setTaxAuthority(client.getTaxAuthority());
			clientForUpdate.setResponsiblePerson(client.getResponsiblePerson());
			clientForUpdate.setCodeBookActivities(client.getCodeBookActivities());
			
			clientService.save(clientForUpdate);
		}
		else {
			throw new NotFoundException();
		}
	}
	
	
	@PutMapping(path = "/updateLegalClient/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public void updateLegalClient(@PathVariable Long id,@RequestBody Client client) {
		Client clientForUpdate = clientService.findOne(id);
		if(clientForUpdate != null) {
			clientForUpdate.setApplicant(client.getApplicant());
			clientForUpdate.setJmbg(client.getJmbg());
			clientForUpdate.setAddress(client.getAddress());
			clientForUpdate.setPhone(client.getPhone());
			clientForUpdate.setFax(client.getFax());
			clientForUpdate.setMail(client.getMail());
			clientForUpdate.setDeliveryAddress(client.getDeliveryAddress());
			clientForUpdate.setDeliveryByMail(client.isDeliveryByMail());
			
			
			clientService.save(clientForUpdate);
		}
		else {
			throw new NotFoundException();
		}
	}
	
	@GetMapping("/findAllLegalBills")
	@ResponseStatus(HttpStatus.OK)
	public List<Client> findAllLegalBills() {
		return clientService.findAllLegalBills(); 
	}
	
	@PostMapping(path = "/saveLegalBill")
	@ResponseStatus(HttpStatus.CREATED)
	public void saveLegalBill(@Valid @RequestBody Client client) {
		client.setType(TypeOfClient.PRAVNO);
		clientService.save(client);
	}
	
	@GetMapping("/findAllPopulatedPlaces")
	@ResponseStatus(HttpStatus.OK)
	public List<PopulatedPlace> findAllPopulatedPlaces() {
		return populatedPlaceService.findAll(); 
	}
	
	@PostMapping(path = "/savePopulatedPlace")
	@ResponseStatus(HttpStatus.CREATED)
	public void savePopulatedPlace(@Valid @RequestBody PopulatedPlace populatedPlace) {
		populatedPlaceService.save(populatedPlace);
	}
	
	@DeleteMapping(path = "/deletePopulatedPlace/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void deletePopulatedPlace(@PathVariable Long id) {
		populatedPlaceService.delete(id);
	}
	
	@PutMapping(path = "/updatePopulatedPlace/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public void updatePopulatedPlace(@PathVariable Long id,@RequestBody PopulatedPlace populatedPlace) {
		PopulatedPlace populatedPlaceForUpdate = populatedPlaceService.findOne(id);
		if(populatedPlaceForUpdate != null) {
			populatedPlaceForUpdate.setName(populatedPlace.getName());
			populatedPlaceForUpdate.setPttCode(populatedPlace.getPttCode());
			populatedPlaceForUpdate.setCountry(populatedPlace.getCountry());
			populatedPlaceService.save(populatedPlaceForUpdate);
		}
		else {
			throw new NotFoundException();
		}
	}
}

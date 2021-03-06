package app.bill;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import app.bank.Bank;
import app.bank.BankService;
import app.client.Client;
import app.client.Client.TypeOfClient;
import app.client.ClientService;
import app.codeBookActivities.CodeBookActivities;
import app.codeBookActivities.CodeBookActivitiesService;
import app.dailyBalance.DailyBalance;
import app.user.banker.Banker;

@RestController
@RequestMapping("/bill")
public class BillController {

	private final BankService bankService;
	private final BillService billService;
	private final ClientService clientService;
	private final CodeBookActivitiesService codeBookActivitiesService;
	private HttpSession httpSession;
	
	@Autowired
	public BillController(final BankService bankService,final BillService billService,HttpSession httpSession,final ClientService clientService
			,final CodeBookActivitiesService codeBookActivitiesService) {
		this.bankService = bankService;
		this.billService = billService;
		this.httpSession = httpSession;
		this.clientService = clientService;
		this.codeBookActivitiesService = codeBookActivitiesService;
	}
	
	@GetMapping(path = "/findAllBills")
	@ResponseStatus(HttpStatus.CREATED)
	public List<Bill> findAllBills() {
		return billService.findAll();
	}
	
	@GetMapping(path = "/findAllIndividualBills")
	@ResponseStatus(HttpStatus.CREATED)
	public List<Bill> findAllIndividualBills() {
		List<Bill> individualBills = new ArrayList<Bill>();
		Bank bank = bankService.findOne(((Banker)httpSession.getAttribute("user")).getBank().getId());
		for(int i=0;i<bank.getBills().size();i++) {
			if(bank.getBills().get(i).getClient().getType() == TypeOfClient.FIZICKO && bank.getBills().get(i).isStatus()) {
				individualBills.add(bank.getBills().get(i));
			}
		}		
		return individualBills;
	}
	
	@GetMapping(path = "/findAllLegalBills")
	@ResponseStatus(HttpStatus.CREATED)
	public List<Bill> findAllLegalBills() {
		List<Bill> individualBills = new ArrayList<Bill>();
		Bank bank = bankService.findOne(((Banker)httpSession.getAttribute("user")).getBank().getId());
		for(int i=0;i<bank.getBills().size();i++) {
			if(bank.getBills().get(i).getClient().getType() == TypeOfClient.PRAVNO && bank.getBills().get(i).isStatus()) {
				individualBills.add(bank.getBills().get(i));
			}
		}		
		return individualBills;
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Bill saveBill(@Valid @RequestBody Bill bill) {
		bill.setStatus(true);
		if(bill.getClient().getCodeBookActivities() != null) {
			CodeBookActivities codeBookActivities = codeBookActivitiesService.findOne(bill.getClient().getCodeBookActivities().getId()	 );
			bill.getClient().setCodeBookActivities(codeBookActivities);
		}
		Client client = clientService.save(bill.getClient());
		bill.setClient(client);
		bill.setDailyBalances(new ArrayList<DailyBalance>());
		bill.setStatus(true);//postavi da je racun otvoren
		bill = billService.save(bill);
		Bank bank = bankService.findOne(((Banker)httpSession.getAttribute("user")).getBank().getId());
		bank.getBills().add(bill);
		bankService.save(bank);
		return bill;
	}
	
	@GetMapping("/findBillsForBank/{id}")
	@ResponseStatus(HttpStatus.OK)
	public List<Bill> findBillsForAllBanks(@PathVariable Long id) {
		Bank bank = bankService.findOne(((Banker)httpSession.getAttribute("user")).getBank().getId());
		Integer code = bank.getCode();
		System.out.println("Code "+code);
		List<Bill> bills = new ArrayList<Bill>();
		List<Bill> allBills = billService.findAllCurrentBillsExceptClosingOne(id);
		for(int i=0;i<allBills.size();i++){
			Bill tempBill = allBills.get(i);
			Integer tempCode = Integer.valueOf(tempBill.getAccountNumber().substring(0, 3));
			System.out.println("tempCode "+tempCode);
			if(tempCode.equals(code)){
				bills.add(tempBill);
			}
		}
		return bills;
	}
	
	@PostMapping("/search")
	@ResponseStatus(HttpStatus.CREATED)
	public List<Bill> searchBill(@RequestBody Bill bill) {
		String accountNumber = bill.getAccountNumber();
		if(accountNumber==null){
			accountNumber="%";
		}else{
			accountNumber="%"+bill.getAccountNumber()+"%";
		}
		String clientApplicant = "";
		if(bill.getClient()==null){
			clientApplicant="%";
		}else{
			clientApplicant="%"+bill.getClient().getApplicant()+"%";
		}
		System.out.println(accountNumber+"   "+clientApplicant);
		return billService.findByAccountNumberLikeAndClient_ApplicantLike(accountNumber, clientApplicant);
	}
	
	@GetMapping("/findDailyBalances/{accNum}")
	@ResponseStatus(HttpStatus.OK)
	public Bill findDailyBalances(@PathVariable String accNum) {
		Bill bill = billService.findByAccountNumber(accNum);
		return bill;
	}
}

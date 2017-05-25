package app.depositSlip;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.ws.rs.BadRequestException;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import app.MyValidationEventHandler;
import app.MT102xml.MT102xml;
import app.bill.Bill;
import app.bill.BillService;
import app.dailyBalance.DailyBalance;
import app.dailyBalance.DailyBalanceService;
import app.depositSlip.DepositSlip.Type;
import app.user.banker.Banker;

@RestController
@RequestMapping("/depositSlip")
public class DepositSlipController {

	private final DepositSlipService depositSlipService;
	private final DailyBalanceService dailyBalanceService;
	private HttpSession httpSession;
	private final BillService billService;
	
	@Autowired
	public DepositSlipController(final DepositSlipService depositSlipService,final BillService billService,final DailyBalanceService dailyBalanceService,HttpSession httpSession) {
		this.depositSlipService = depositSlipService;
		this.httpSession = httpSession;
		this.dailyBalanceService = dailyBalanceService;
		this.billService = billService;
	}
	
	@GetMapping("/findAllDepositSlips")
	@ResponseStatus(HttpStatus.OK)
	public List<DepositSlip> findAllDepositSlips() {
		return depositSlipService.findAll();
	}
	
	@PostMapping(path = "/saveDepositSlip")
	@ResponseStatus(HttpStatus.CREATED)
	public void saveDepositSlip(@RequestBody DepositSlip depositSlip) {
		try {
			depositSlipService.save(depositSlip);
			int bankCodeBillOfReciver = Integer.parseInt(depositSlip.getBillOfReceiver().substring(0, 3));
			int bankCodeBillOfDeptor = Integer.parseInt(depositSlip.getBillOfDeptor().substring(0, 3));
			int bankCode = ((Banker)httpSession.getAttribute("user")).getBank().getCode();
			if(bankCode == bankCodeBillOfReciver && bankCode == bankCodeBillOfDeptor) {
				Bill billForRecieveMoney = null;
				if(depositSlip.getType() == Type.TRANSFER) {
					billForRecieveMoney = billService.findByAccountNumber(depositSlip.getBillOfReceiver());
				}else if (depositSlip.getType() == Type.PAYOUT) {
					
				}else if (depositSlip.getType() == Type.PAYMENTIN) {
					
				}else if (depositSlip.getType() == Type.PAYMENTOUT) {
					
				}
				if(billForRecieveMoney == null)
					throw new BadRequestException();
				if(billForRecieveMoney.getDailyBalances() == null) {
					billForRecieveMoney.setDailyBalances(new ArrayList<DailyBalance>());
					DailyBalance dailyB = dailyBalanceService.save(new DailyBalance());
					dailyB.getDepositSlips().add(depositSlip);
					dailyB.setPreviousState(dailyB.getNewState());
					dailyB.setNewState(dailyB.getNewState() + depositSlip.getAmount());
					dailyB.setTrafficToBenefit(depositSlip.getAmount());

					dailyB = dailyBalanceService.save(dailyB);
					billForRecieveMoney.getDailyBalances().add(dailyB);				
				}
				else if(billForRecieveMoney.getDailyBalances().size() == 0) {
 					DailyBalance dailyB = dailyBalanceService.save(new DailyBalance());
					dailyB.getDepositSlips().add(depositSlip);
					dailyB.setPreviousState(dailyB.getNewState());
					dailyB.setNewState(dailyB.getNewState() + depositSlip.getAmount());
					dailyB.setTrafficToBenefit(depositSlip.getAmount());

					dailyB = dailyBalanceService.save(dailyB);
					billForRecieveMoney.getDailyBalances().add(dailyB);
				}
				else {
					Date currentTime = new Date();
					DailyBalance dailyB = billForRecieveMoney.getDailyBalances().get(billForRecieveMoney.getDailyBalances().size()-1);
					
					Calendar c1= Calendar.getInstance();
					Calendar c2= Calendar.getInstance();
					c1.setTime(currentTime);
					c2.setTime(dailyB.getDate());
					int yearDiff = c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR);
					int monthDiff = c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);
					int dayDiff = c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH);
					if(yearDiff == 0 && monthDiff == 0 && dayDiff == 0) {
						dailyB.getDepositSlips().add(depositSlip);
						dailyB.setPreviousState(dailyB.getNewState());
						dailyB.setNewState(dailyB.getNewState() + depositSlip.getAmount());
						dailyB.setTrafficToBenefit(depositSlip.getAmount());
					}
				}
			}
		}
		catch(Exception ex) {
			throw new BadRequestException();
		}
	}
	

	@PostMapping("/search")
	@ResponseStatus(HttpStatus.CREATED)
	public List<DepositSlip> searchDepositSlips(@RequestBody DepositSlip depositSlip) {
		double amount = depositSlip.getAmount();
		Type type = depositSlip.getType();
		String receiver = depositSlip.getBillOfReceiver();
		if(receiver==null){
			receiver="%";
		}else{
			receiver="%"+depositSlip.getBillOfReceiver()+"%";
		}
		String deptor = depositSlip.getBillOfDeptor();
		if(deptor==null){
			deptor="%";
		}else{
			deptor="%"+depositSlip.getBillOfDeptor()+"%";
		}

		if(type==Type.PAYMENTOUT){
			if(amount==0){
				if(depositSlip.getDepositSlipDate()==null){
					return depositSlipService.findByTypeAndBillOfReceiverLike(type, receiver);
				}else{
					return depositSlipService.findByDepositSlipDateAndTypeAndBillOfReceiverLike(depositSlip.getDepositSlipDate(), type, receiver);
				}
			}else{
				if(depositSlip.getDepositSlipDate()==null){
					return depositSlipService.findByTypeAndBillOfReceiverLikeAndAmount(type, receiver, amount);
				}else{
					return depositSlipService.findByDepositSlipDateAndTypeAndBillOfReceiverLikeAndAmount(depositSlip.getDepositSlipDate(), type, receiver, amount);
				}
			}
		}else if(type==Type.PAYOUT){
			if(amount==0){
				if(depositSlip.getDepositSlipDate()==null){
					return depositSlipService.findByTypeAndBillOfDeptorLike(type, deptor);
				}else{
					return depositSlipService.findByDepositSlipDateAndTypeAndBillOfDeptorLike(depositSlip.getDepositSlipDate(), type, deptor);
				}
			}else{
				if(depositSlip.getDepositSlipDate()==null){
					return depositSlipService.findByTypeAndBillOfDeptorLikeAndAmount(type, deptor, amount);
				}else{
					return depositSlipService.findByDepositSlipDateAndTypeAndBillOfDeptorLikeAndAmount(depositSlip.getDepositSlipDate(), type, deptor, amount);
				}
			}
		}else if(type==Type.PAYMENTIN || type==Type.TRANSFER){
			if(amount==0){
				if(depositSlip.getDepositSlipDate()==null){
					return depositSlipService.findByTypeAndBillOfReceiverLikeAndBillOfDeptorLike(type, receiver, deptor);
				}else{
					return depositSlipService.findByDepositSlipDateAndTypeAndBillOfReceiverLikeAndBillOfDeptorLike(depositSlip.getDepositSlipDate(), type, receiver, deptor);
				}
			}else{
				if(depositSlip.getDepositSlipDate()==null){
					return depositSlipService.findByTypeAndBillOfReceiverLikeAndBillOfDeptorLikeAndAmount(type, receiver, deptor, amount);
				}else{
					return depositSlipService.findByDepositSlipDateAndTypeAndBillOfReceiverLikeAndBillOfDeptorLikeAndAmount(depositSlip.getDepositSlipDate(), type, receiver, deptor, amount);
				}
			}
		}else{
			if(amount==0){
				if(depositSlip.getDepositSlipDate()==null){
					return depositSlipService.findByBillOfReceiverLikeAndBillOfDeptorLike(receiver, deptor);
				}else{
					return depositSlipService.findByDepositSlipDateAndBillOfReceiverLikeAndBillOfDeptorLike(depositSlip.getDepositSlipDate(), receiver, deptor);
				}
			}
		}
		return depositSlipService.findByDepositSlipDateAndBillOfReceiverLikeAndBillOfDeptorLikeAndAmount(depositSlip.getDepositSlipDate(), receiver, deptor, amount);
	}

}

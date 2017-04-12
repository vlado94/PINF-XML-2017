package app.exchangeRate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class ExchangeRate {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "EXCHANGERATE_ID")
	private Long id;
}

package app.user;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;

@Data
@MappedSuperclass
public class User {

	@NotBlank
	@Column
	private String userType;
	
	@Email
	@NotBlank
	@Column(unique = true)
	private String mail;

	@NotBlank
	@Column
	private String password;

	@NotBlank
	@Column
	private String firstname;

	@NotBlank
	@Column
	private String lastname;
	
	public void setAttributes (User user) {
		this.setFirstname(user.getFirstname());
		this.setLastname(user.getLastname());
		this.setMail(user.getMail());
		this.setPassword(user.getPassword());
	}


}

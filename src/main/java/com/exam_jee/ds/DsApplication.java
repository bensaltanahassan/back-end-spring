package com.exam_jee.ds;

import com.exam_jee.ds.model.Role;
import com.exam_jee.ds.model.User;
import com.exam_jee.ds.repositories.RoleRepo;
import com.exam_jee.ds.repositories.UserRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.UUID;

@SpringBootApplication
@CrossOrigin("*")
public class DsApplication {

	@Autowired
	private RoleRepo roleRep;
	@Autowired
	private UserRepo userRepository;
	@Autowired
	private PasswordEncoder encoder;

	public static void main(String[] args) {
		SpringApplication.run(DsApplication.class, args);
	}
	//@PostConstruct
	public void init() {
		// Create roles
		Role banquier = new Role();
		Role client = new Role();

		banquier.setName("BANQUIER");
		banquier.setDescription("banquier");

		client.setName("CLIENT");
		client.setDescription("client");

// Save roles
		banquier = roleRep.save(banquier);
		client = roleRep.save(client);

// Create and save SuperAdmin user
		User superAdmin = User.builder()
				.username("username")
				.email("username@gmail.com")
				.passwordd(encoder.encode("password"))  // Note: correct field name if it's 'password'
				.role(banquier)
				.enabled(true)
				.balance(0)
				.userId(UUID.randomUUID().toString())
				.build();
		userRepository.save(superAdmin);

// Loop to create and save 5 client users
		for (int i = 1; i <= 5; i++) {
			String username = "client" + i;
			String email = username + "@example.com";
			String password = "password" + i;

			User clientUser = User.builder()
					.username(username)
					.email(email)
					.passwordd(encoder.encode(password))
					.role(client)
					.enabled(true)
					.balance(0)  // You can set an initial balance if needed
					.userId(UUID.randomUUID().toString())
					.build();

			userRepository.save(clientUser);
		}

		System.out.println("SuperAdmin and 5 client users have been successfully saved.");

	}
}

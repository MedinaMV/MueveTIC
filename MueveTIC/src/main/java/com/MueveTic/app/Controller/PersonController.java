package com.MueveTic.app.Controller;

import java.util.Map;

import javax.management.InvalidAttributeValueException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.MueveTic.app.Services.EmailService;
import com.MueveTic.app.Services.PersonService;
import com.MueveTic.app.Utils.AuthResponse;

@RestController
@RequestMapping("person")
@CrossOrigin("*")
public class PersonController {
	
	@Autowired
	private PersonService personService;
	@Autowired
	private EmailService emailService;
	
	private static final String EMAIL = "email";
	
	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@RequestBody Map<String, Object> info) {
		try {
			this.personService.registerUser(info);
		}catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	@PostMapping("/register-admin")
	public ResponseEntity<String> registerAdmin(@RequestBody Map<String, Object> info) {
		try {
			this.personService.registerAdmin(info);
		}catch (InvalidAttributeValueException | DuplicateKeyException e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	@PostMapping("/register-mantenance")
	public ResponseEntity<String> registerMantenance(@RequestBody Map<String, Object> info) {
		try {
			this.personService.registerMantenance(info);
		}catch (InvalidAttributeValueException | DuplicateKeyException e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	@PutMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody Map<String, Object> info) {
		AuthResponse response;
		try {
			response = this.personService.login(info.get(EMAIL).toString(),info.get("password").toString());
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
		}
		if(response.getRole().equals("")) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Not valid User");
		}
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/resetEmail")
	public ResponseEntity<String> sendResetEmail(@RequestBody Map<String, Object> info) {
		try {
			emailService.sendResetEmail(info.get(EMAIL).toString());
		}catch(Exception e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	@PostMapping("/verify")
	public void sendVerifyEmail(@RequestBody Map<String, Object> info) {
		try {
			emailService.sendActivationEmail(info.get(EMAIL).toString());
		} catch (Exception e) { }
	}	
}
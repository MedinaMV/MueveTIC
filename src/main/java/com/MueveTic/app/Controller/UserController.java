package com.MueveTic.app.Controller;

import java.util.Map;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.MueveTic.app.Services.UserService;

@RestController
@RequestMapping("users")
@CrossOrigin("*")
public class UserController {

	@Autowired
	private UserService usersService;
	private static final String EMAIL = "email";
	
	@PutMapping("/deactivate")
	public ResponseEntity<String> deactivate(@RequestBody Map<String, Object> info) {
		try {
			this.usersService.deactivate(info.get(EMAIL).toString());
		} catch (UsernameNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PutMapping("/delete")
	public ResponseEntity<String> delete(@RequestBody Map<String, Object> info) {
		try {
			this.usersService.delete(info.get(EMAIL).toString());
		} catch (UsernameNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@GetMapping("/activate")
	public ResponseEntity<String> activate(@RequestParam String email) {
		try {
			this.usersService.activate(email);
		}catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping("/update")
	public ResponseEntity<String> update(@RequestBody Map<String, Object> info){
		try {
			JSONObject jso = new JSONObject(info);
			this.usersService.update(jso);
		}catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping("/resetPassword")
	public ResponseEntity<String> resetPassword(@RequestBody Map<String, Object> info) {
		try {
			this.usersService.resetPassword(info.get(EMAIL).toString(), info.get("password").toString());
		}catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
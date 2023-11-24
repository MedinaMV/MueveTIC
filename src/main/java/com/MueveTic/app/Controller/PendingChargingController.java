package com.MueveTic.app.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.MueveTic.app.Services.PendingChargingService;

@RestController
@RequestMapping("pendingCharging")
@CrossOrigin("*")
public class PendingChargingController {
	
	@Autowired
	private PendingChargingService pendingChargingService;

	@PostMapping("/reserveChargingVehicle")
	public ResponseEntity<String> reserveChargingVehicle(@RequestBody Map<String,Object> info) {
		try {
			this.pendingChargingService.reserveChargingVehicle(info.get("licensePlate").toString(),info.get("email").toString());
			return new ResponseEntity<>(HttpStatus.OK);
		}catch(Exception e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
		}
	}
	
	@GetMapping("/consultPendingChargingVehicle")
	public ResponseEntity<String> consultPendingChargingVehicle(@RequestParam String email) {
		this.pendingChargingService.consultPendingChargingVehicle(email);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@GetMapping("/removePendingChargingVehicle")
	public ResponseEntity<String> removePendingChargingVehicle(@RequestParam String licensePlate) {
		this.pendingChargingService.removePendingChargingVehicle(licensePlate);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}

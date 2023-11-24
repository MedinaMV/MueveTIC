package com.MueveTic.app.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.MueveTic.app.Entities.PendingCharging;
import com.MueveTic.app.Entities.PersonalMant;
import com.MueveTic.app.Entities.Vehicle;
import com.MueveTic.app.Repositories.ConfigParamsDAO;
import com.MueveTic.app.Repositories.PendingChargingDAO;

@Service
public class PendingChargingService {
	
	@Autowired
	private VehicleService vehicleService;
	@Autowired
	private PersonService personService;
	@Autowired
	private SequenceGeneratorService seqGenerator;
	@Autowired
	private PendingChargingDAO pendingChargingRepository;
	@Autowired
	private ConfigParamsDAO paramsRepository;
	
	public void reserveChargingVehicle(String licensePlate, String email) throws com.mongodb.MongoWriteException{
		int maxVehiclesCharging = paramsRepository.findById(1).get().getMaxVehiclesCharging();
		Vehicle v = vehicleService.consultVehicle(licensePlate);
		PersonalMant p = (PersonalMant) personService.searchPerson(email);
		if(v.isUnAvailable() && pendingChargingRepository.findByPersonalMant(p).size() < maxVehiclesCharging) {
			PendingCharging pc = new PendingCharging(v,p);
			v.setPendingCharging();
			vehicleService.changeStateVehicle(v);
			pc.setId(seqGenerator.getSequenceNumber(PendingCharging.SEQUENCE_NAME));
			this.pendingChargingRepository.insert(pc);
		}
	}
	
	public void removePendingChargingVehicle(String licensePlate) {
		this.pendingChargingRepository.delete(this.pendingChargingRepository.findByVehicle(vehicleService.consultVehicle(licensePlate)));
	}

	public void consultPendingChargingVehicle(String email) {
		this.pendingChargingRepository.findByPersonalMant((PersonalMant)personService.searchPerson(email));
	}
}
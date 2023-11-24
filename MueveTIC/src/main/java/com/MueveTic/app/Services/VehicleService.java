package com.MueveTic.app.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.MueveTic.app.Entities.Scooter;
import com.MueveTic.app.Entities.StateVehicle;
import com.MueveTic.app.Entities.Vehicle;
import com.MueveTic.app.Entities.Booking;
import com.MueveTic.app.Entities.Car;
import com.MueveTic.app.Entities.Motorcycle;
import com.MueveTic.app.Repositories.BookingDAO;
import com.MueveTic.app.Repositories.CarDAO;
import com.MueveTic.app.Repositories.MotorcycleDAO;
import com.MueveTic.app.Repositories.ScooterDAO;
import com.MueveTic.app.Utils.AdminCarResponse;
import com.MueveTic.app.Utils.AdminMotorcycleResponse;
import com.MueveTic.app.Utils.AdminScooterResponse;

@Service
public class VehicleService {

	@Autowired
	private MotorcycleDAO motorcycleRepository;
	@Autowired
	private CarDAO carRepository;
	@Autowired
	private ScooterDAO scooterRepository;
	@Autowired
	private BookingDAO bookingRepository;
	@Autowired
	private SequenceGeneratorService seqGenerator;

	private static final String NSEATS = "nSeats";
	private static final String HELMET = "helmet";
	private static final String MODEL = "model";
	private static final String LICENSEPLATE = "licensePlate";
	private static final String ADDRESS = "address";

	public void addVehicle(Map<String, Object> info) {
		if (info.get("type").toString().equals("car")) {
			if(!checkRepeated(info.get(LICENSEPLATE).toString())) {
				Car c = new Car(info.get("type").toString(), info.get(LICENSEPLATE).toString(), info.get(MODEL).toString(),
						info.get(ADDRESS).toString(), Integer.parseInt(info.get(NSEATS).toString()));
				c.setId(seqGenerator.getSequenceNumber(Car.SEQUENCE_NAME));
				carRepository.save(c);
			}else {
				throw new DuplicateKeyException("Vehicle already registered");
			}
		} else if (info.get("type").toString().equals("motorcycle")) {
			if(!checkRepeated(info.get(LICENSEPLATE).toString())) {
				Motorcycle m = new Motorcycle(info.get("type").toString(), info.get(LICENSEPLATE).toString(),
						info.get(MODEL).toString(), info.get(ADDRESS).toString(),
						Boolean.parseBoolean(info.get(HELMET).toString()));
				m.setId(seqGenerator.getSequenceNumber(Motorcycle.SEQUENCE_NAME));
				motorcycleRepository.save(m);
			}else {
				throw new DuplicateKeyException("Vehicle already registered");
			}
		} else {
			if(!checkRepeated(info.get(LICENSEPLATE).toString())) {
				Scooter s = new Scooter(info.get("type").toString(), info.get(LICENSEPLATE).toString(),
						info.get(MODEL).toString(), info.get(ADDRESS).toString(), info.get("color").toString());
				s.setId(seqGenerator.getSequenceNumber(Scooter.SEQUENCE_NAME));
				scooterRepository.save(s);
			}else {
				throw new DuplicateKeyException("Vehicle already registered");
			}
		}
	}
	
	public void update(JSONObject jso) {
		var car = carRepository.findByLicensePlate(jso.get(LICENSEPLATE).toString());
		var motorcycle = motorcycleRepository.findByLicensePlate(jso.get(LICENSEPLATE).toString());
		var scooter = scooterRepository.findByLicensePlate(jso.get(LICENSEPLATE).toString());
		if(car.isEmpty() && motorcycle.isEmpty() && scooter.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Vehicle doesn't exists");
		}
		if(car.isPresent() && car.get().isAvailable()) {
			Car newCar = car.get();
			newCar.setModel(jso.get(MODEL).toString());
			newCar.setAddress(jso.get(ADDRESS).toString());
			newCar.setnSeats(Integer.parseInt(jso.get(NSEATS).toString()));
			carRepository.save(newCar);
		}else if(motorcycle.isPresent() && motorcycle.get().isAvailable()) {
			Motorcycle newMoto = motorcycle.get();
			newMoto.setModel(jso.get(MODEL).toString());
			newMoto.setAddress(jso.get(ADDRESS).toString());
			newMoto.setHelmet(Boolean.parseBoolean(jso.get(HELMET).toString()));
			motorcycleRepository.save(newMoto);
		}else if(scooter.isPresent() && scooter.get().isAvailable()) {
			Scooter newScooter = scooter.get();
			newScooter.setModel(jso.get(MODEL).toString());
			newScooter.setAddress(jso.get(ADDRESS).toString());
			newScooter.setColor(jso.get("color").toString());
			scooterRepository.save(newScooter);
		}
	}

	public void removeVehicle(Map<String, Object> info) {
		var car = carRepository.findByLicensePlate(info.get(LICENSEPLATE).toString());
		var motorcycle = motorcycleRepository.findByLicensePlate(info.get(LICENSEPLATE).toString());
		var scooter = scooterRepository.findByLicensePlate(info.get(LICENSEPLATE).toString());

		if (car.isEmpty() && motorcycle.isEmpty() && scooter.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Vehicle doesn't exists");
		}
		if (car.isPresent() && !car.get().isDeactivated() ) {
			car.get().deactivateVehicle();
			carRepository.save(car.get());
		} else if (motorcycle.isPresent() && !motorcycle.get().isDeactivated()) {
			motorcycle.get().deactivateVehicle();
			motorcycleRepository.save(motorcycle.get());
		} else if (scooter.isPresent() && !scooter.get().isDeactivated()) {
			scooter.get().deactivateVehicle();
			scooterRepository.save(scooter.get());
		}
	}

	public Vehicle consultVehicle(String licensePlate) {
		Optional<Car> car = carRepository.findByLicensePlate(licensePlate);
		Optional<Motorcycle> motorcycle = motorcycleRepository.findByLicensePlate(licensePlate);
		Optional<Scooter> scooter = scooterRepository.findByLicensePlate(licensePlate);
		if (car.isEmpty() && motorcycle.isEmpty() && scooter.isEmpty()) { throw new NoSuchElementException("Vehicle doesn´t exists");}
		
		if (car.isPresent() && car.get().getType().equals("car")) {
			return car.get();
		} else if (motorcycle.isPresent() && motorcycle.get().getType().equals("motorcycle")) {
			return motorcycle.get();
		} else if (scooter.isPresent() && scooter.get().getType().equals("scooter")) {
			return scooter.get();
		}
		return null;
	}
	
	public void changeStateVehicle(Vehicle v) {
		this.saveVehicle(v);
	}
	
	public void chargeVehicle(String licensePlate) {
		Vehicle v = consultVehicle(licensePlate);
		v.setBattery(100);
		v.setAvailable();
		this.saveVehicle(v);
	}
	
	public List<Car> lowBatteryCar() {
		return this.carRepository.findByState(StateVehicle.UNAVAILABLE);
	}
	
	public List<Scooter> lowBatteryScooter() {
		return this.scooterRepository.findByState(StateVehicle.UNAVAILABLE);
	}
	
	public List<Motorcycle> lowBatteryMotorcycle() {
		return this.motorcycleRepository.findByState(StateVehicle.UNAVAILABLE);
	}
	
	public List<Car> availableCar() {
		return this.carRepository.findByState(StateVehicle.AVAILABLE);
	}

	public List<Scooter> availableScooter() {
		return this.scooterRepository.findByState(StateVehicle.AVAILABLE);
	}

	public List<Motorcycle> availableMotorcycle() {
		return this.motorcycleRepository.findByState(StateVehicle.AVAILABLE);
	}
	
	private void saveVehicle(Vehicle v) {
		if(v instanceof Car) {
			this.carRepository.save((Car)v);
		}else if(v instanceof Motorcycle) {
			this.motorcycleRepository.save((Motorcycle)v);
		}else {
			this.scooterRepository.save((Scooter) v);
		}
	}
	
	private boolean checkRepeated(String licensePlate) {
		return this.carRepository.findByLicensePlate(licensePlate).isPresent() || this.scooterRepository.findByLicensePlate(licensePlate).isPresent() 
				|| this.motorcycleRepository.findByLicensePlate(licensePlate).isPresent();
	}

	public List<AdminCarResponse> getAllCars() {
		List<AdminCarResponse> result = new ArrayList<>();
		List<Car> cars = this.carRepository.findAll();
		int sum = 0;
		int mean = 0;
		for(Car car : cars) {
			List<Booking> bookings = this.bookingRepository.findByVehicle(car);
			for(Booking booking : bookings) {
				sum += booking.getRating();
			}
			if(!bookings.isEmpty()) {
				mean = sum/bookings.size();
			}
			result.add(new AdminCarResponse(car,(int) mean+""));
		}
		return result;
	}
		
	public List<AdminMotorcycleResponse> getAllMotorcycle() {
		List<AdminMotorcycleResponse> result = new ArrayList<>();
		List<Motorcycle> motorcycles = this.motorcycleRepository.findAll();
		int sum = 0;
		int mean = 0;
		for(Motorcycle motorcycle : motorcycles) {
			List<Booking> bookings = this.bookingRepository.findByVehicle(motorcycle);
			for(Booking booking : bookings) {
				mean += booking.getRating();
			}
			if(!bookings.isEmpty()) {
				mean = sum/bookings.size();
			}
			result.add(new AdminMotorcycleResponse(motorcycle,(int) mean+""));
		}
		return result;
	}

	public List<AdminScooterResponse> getAllScooter() {
		List<AdminScooterResponse> result = new ArrayList<>();
		List<Scooter> scooters = this.scooterRepository.findAll();
		int sum = 0;
		int mean = 0;
		for(Scooter scooter : scooters) {
			List<Booking> bookings = this.bookingRepository.findByVehicle(scooter);
			for(Booking booking : bookings) {
				mean += booking.getRating();
			}
			if(!bookings.isEmpty()) {
				mean = sum/bookings.size();
			}
			result.add(new AdminScooterResponse(scooter,(int) mean+""));
		}
		return result;
	}
}
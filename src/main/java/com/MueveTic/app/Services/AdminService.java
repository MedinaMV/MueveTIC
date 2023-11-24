package com.MueveTic.app.Services;

import com.MueveTic.app.Repositories.AdminDAO;
import com.MueveTic.app.Repositories.BookingDAO;
import com.MueveTic.app.Repositories.CarDAO;
import com.MueveTic.app.Repositories.ConfigParamsDAO;
import com.MueveTic.app.Repositories.MotorcycleDAO;
import com.MueveTic.app.Repositories.PersonalMantDAO;
import com.MueveTic.app.Repositories.ScooterDAO;
import com.MueveTic.app.Repositories.UserDAO;
import com.MueveTic.app.Utils.AdminRatingResponse;
import com.MueveTic.app.Entities.Admin;
import com.MueveTic.app.Entities.Booking;
import com.MueveTic.app.Entities.Car;
import com.MueveTic.app.Entities.ConfigParams;
import com.MueveTic.app.Entities.Motorcycle;
import com.MueveTic.app.Entities.PersonalMant;
import com.MueveTic.app.Entities.Scooter;
import com.MueveTic.app.Entities.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.management.InvalidAttributeValueException;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

	@Autowired
	private UserDAO userRepository;
	@Autowired
	private AdminDAO adminRepository;
	@Autowired
	private PersonalMantDAO personalMantRepository;
	@Autowired
	private ConfigParamsDAO paramsRepository;
	@Autowired
	private CarDAO carRepository;
	@Autowired
	private MotorcycleDAO motorcycleRepository;
	@Autowired
	private ScooterDAO scooterRepository;
	@Autowired
	private BookingDAO bookingRepository;
	
	public List<User> consultUsers() {
		return this.userRepository.findAll();
	}
	public List<Admin> consultAdministrators() {
		return this.adminRepository.findAll();
	}
	
	public List<PersonalMant> consultMantenance() {
		return this.personalMantRepository.findAll();
	}
	
	public void deactivate(String email) {
		var admin = this.adminRepository.findByEmail(email);
		if (admin.isPresent() && (admin.get().getValidation() == 1)) {
			admin.get().setValidation((byte) 0);
			this.adminRepository.save(admin.get());
		}
	}
	
	public void activate(String email) {
		var admin = this.adminRepository.findByEmail(email);
		if (admin.isPresent() && (admin.get().getValidation() == 0)) {
			admin.get().setValidation((byte) 1);
			this.adminRepository.save(admin.get());
		}	
	}
	
	public void resetPassword(String email, String password) {
		var admin = this.adminRepository.findByEmail(email);
		if (admin.isPresent() && (admin.get().getValidation() == 1)) {
			admin.get().setPassword(DigestUtils.sha512Hex(password));
			this.adminRepository.save(admin.get());
		}else {
			throw new UsernameNotFoundException("Email not found: " + email);
		}
	}
	
	public void update(JSONObject jso) throws InvalidAttributeValueException {
		var admin = this.adminRepository.findByEmail(jso.get("email").toString());
		if(admin.isPresent()) {
			Admin newAdmin = admin.get();
			try {
				newAdmin.setName(jso.get("name").toString());
				newAdmin.setSurname(jso.get("surname").toString());
				newAdmin.setDni(jso.get("dni").toString());
				newAdmin.setPassword(jso.get("password").toString());
				newAdmin.setCity(jso.get("city").toString());
				this.adminRepository.save(newAdmin);
			} catch (InvalidAttributeValueException e) {
				throw new InvalidAttributeValueException("Wrong attribute format"); 
			}
		}else {
			throw new UsernameNotFoundException("Email not found: " + jso.get("email").toString());
		}
	}
	
	public List<AdminRatingResponse> consultRatingCar() {
		List<AdminRatingResponse> result = new ArrayList<>();
		List<Car> cars = this.carRepository.findAll();
		Map<String, Integer> sumModel = new HashMap<>();
		Map<String, Integer> sumTimes = new HashMap<>();
		for(Car car : cars) {
			List<Booking> bookings = this.bookingRepository.findByVehicle(car);
			for(Booking booking : bookings) {
				if(sumModel.containsKey(car.getModel())) {
					sumModel.put(car.getModel(), sumModel.get(car.getModel()) + booking.getRating());
					sumTimes.put(car.getModel(), sumTimes.get(car.getModel()) + 1);
				}else {
					sumModel.put(car.getModel(), (int)booking.getRating());
					sumTimes.put(car.getModel(), 1);
				}
			}
		}
		if(sumModel.size() != sumTimes.size()) {
			throw new Error("Something failed");
		}
		for (Map.Entry<String, Integer> entry : sumModel.entrySet()) {
			result.add(new AdminRatingResponse(entry.getKey(), entry.getValue(), sumTimes.get(entry.getKey())));
		}
		return result;
	}
	
	public List<AdminRatingResponse> consultRatingMotorcycle() {
		List<AdminRatingResponse> result = new ArrayList<>();
		List<Motorcycle> motorcycles = this.motorcycleRepository.findAll();
		Map<String, Integer> sumModel = new HashMap<>();
		Map<String, Integer> sumTimes = new HashMap<>();
		for(Motorcycle motorcycle : motorcycles) {
			List<Booking> bookings = this.bookingRepository.findByVehicle(motorcycle);
			for(Booking booking : bookings) {
				if(sumModel.containsKey(motorcycle.getModel())) {
					sumModel.put(motorcycle.getModel(), sumModel.get(motorcycle.getModel()) + booking.getRating());
					sumTimes.put(motorcycle.getModel(), sumTimes.get(motorcycle.getModel()) + 1);
				}else {
					sumModel.put(motorcycle.getModel(), (int)booking.getRating());
					sumTimes.put(motorcycle.getModel(), 1);
				}
			}
		}
		if(sumModel.size() != sumTimes.size()) {
			throw new Error("Something failed");
		}
		for (Map.Entry<String, Integer> entry : sumModel.entrySet()) {
			result.add(new AdminRatingResponse(entry.getKey(), entry.getValue(), sumTimes.get(entry.getKey())));
		}
		return result;
	}
	
	public List<AdminRatingResponse> consultRatingScooter() {
		List<AdminRatingResponse> result = new ArrayList<>();
		List<Scooter> scooters = this.scooterRepository.findAll();
		Map<String, Integer> sumModel = new HashMap<>();
		Map<String, Integer> sumTimes = new HashMap<>();
		for(Scooter scooter : scooters) {
			List<Booking> bookings = this.bookingRepository.findByVehicle(scooter);
			for(Booking booking : bookings) {
				if(sumModel.containsKey(scooter.getModel())) {
					sumModel.put(scooter.getModel(), sumModel.get(scooter.getModel()) + booking.getRating());
					sumTimes.put(scooter.getModel(), sumTimes.get(scooter.getModel()) + 1);
				}else {
					sumModel.put(scooter.getModel(), (int)booking.getRating());
					sumTimes.put(scooter.getModel(), 1);
				}
			}
		}
		if(sumModel.size() != sumTimes.size()) {
			throw new Error("Something failed");
		}
		for (Map.Entry<String, Integer> entry : sumModel.entrySet()) {
			result.add(new AdminRatingResponse(entry.getKey(), entry.getValue(), sumTimes.get(entry.getKey())));
		}
		return result;
	}
	
	public List<User> consultReviews(String vehicleModel) {
		List<User> result = new ArrayList<>();
		List<Booking> bookings = this.bookingRepository.findAll();
		for(Booking booking : bookings) {
			if(booking.getVehicle().getModel().equals(vehicleModel)) {
				result.add(booking.getUser());
			}
		}
		return result;
	}
	
	public List<Booking> consultUserBooking(String email, String vehicleModel) {
		List<Booking> result = new ArrayList<>();
		List<Booking> bookings = this.bookingRepository.findByUser(this.userRepository.findByEmail(email).get());
		for(Booking booking : bookings) {
			if(booking.getVehicle().getModel().equals(vehicleModel)) {
				result.add(booking);
			}
		}
		return result;
	}
	
	public ConfigParams consultParams(int id) {
		Optional<ConfigParams> params = this.paramsRepository.findById(id);
		if(params.isPresent()) {
			return params.get();
		}
		return null;
	}
	
	public void updateParams(JSONObject jso) {
		Optional<ConfigParams> params = this.paramsRepository.findById(1);
		if(params.isPresent()) {
			ConfigParams newParams = params.get();
			newParams.setMinBatteryPerTrip(Integer.parseInt(jso.get("minBatteryPerTrip").toString()));
			newParams.setMaxVehiclesCharging(Integer.parseInt(jso.get("maxVehiclesCharging").toString()));
			newParams.setFacturationPerTrip(Integer.parseInt(jso.get("facturationPerTrip").toString()));
			newParams.setBatteryPerTrip(Integer.parseInt(jso.get("batteryPerTrip").toString()));
			this.paramsRepository.save(newParams);
		}
	}
}
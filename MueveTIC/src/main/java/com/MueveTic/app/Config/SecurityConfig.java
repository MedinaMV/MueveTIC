package com.MueveTic.app.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.MueveTic.app.Jwt.JwtAuthenticationFilter;

import jakarta.servlet.Filter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, 
    		@Autowired AuthenticationProvider authProvider, 
    		@Autowired JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception
    {
        return http
            .csrf(csrf -> 
                csrf
                .disable())
            .authorizeHttpRequests(authRequest ->
              authRequest
                .requestMatchers("/admins/*").hasRole("ADMIN")
                .requestMatchers("/bookings/createBooking").hasRole("USER")
                .requestMatchers("/bookings/cancelUserBooking").hasRole("USER")
                .requestMatchers("/bookings/confirmBooking").hasRole("USER")
                .requestMatchers("/bookings/consultUserBooking").hasRole("USER")
                .requestMatchers("/bookings/consultHistoryUserBooking").hasRole("USER")
                .requestMatchers("/bookings/consultAllPersonsWithBookings").hasRole("ADMIN")
                .requestMatchers("/bookings/consultBooking").hasRole("ADMIN")
                .requestMatchers("/bookings/consultFacturation").hasRole("ADMIN")
                .requestMatchers("/bookings/consultFacturationCar").hasRole("ADMIN")
                .requestMatchers("/bookings/consultFacturationScooter").hasRole("ADMIN")
                .requestMatchers("/pendingCharging/*").hasRole("MANTENANCE")
                .requestMatchers("/personal/*").hasRole("ADMIN")
                .requestMatchers("/person/register-admin").hasRole("ADMIN")
                .requestMatchers("/person/register-mantenance").hasRole("ADMIN")
                .requestMatchers("/users/*").hasRole("USER")
                .requestMatchers("/vehicle/consultVehicle").hasRole("ADMIN")
                .requestMatchers("/vehicle/getAllCars").hasRole("ADMIN")
                .requestMatchers("/vehicle/getAllMotorcycle").hasRole("ADMIN")
                .requestMatchers("/vehicle/getAllScooter").hasRole("ADMIN")
                .requestMatchers("/vehicle/addVehicle").hasRole("ADMIN")
                .requestMatchers("/vehicle/update").hasRole("ADMIN")
                .requestMatchers("/vehicle/removeVehicle").hasRole("ADMIN")
                .requestMatchers("/vehicle/availableCar").hasRole("USER")
                .requestMatchers("/vehicle/availableScooter").hasRole("USER")
                .requestMatchers("/vehicle/availableMotorcycle").hasRole("USER")
                .requestMatchers("/vehicle/lowBatteryCar").hasRole("MANTENANCE")
                .requestMatchers("/vehicle/lowBatteryMotorcycle").hasRole("MANTENANCE")
                .requestMatchers("/vehicle/lowBatteryScooter").hasRole("MANTENANCE")
                .requestMatchers("/vehicle/chargeVehicle").hasRole("MANTENANCE")
                .requestMatchers("/vehiclesType/*").hasRole("ADMIN")
                .anyRequest().permitAll()
                )
            .sessionManagement(sessionManager->
                sessionManager 
                  .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authProvider)
            .addFilterBefore((Filter) jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
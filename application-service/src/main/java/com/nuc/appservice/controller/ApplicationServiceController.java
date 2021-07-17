package com.nuc.appservice.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nuc.appservice.entity.Application;
import com.nuc.appservice.service.ApplicationService;
import com.nuc.appservice.vo.ApplicationCreationResponse;

@RestController
@RequestMapping("/application")
public class ApplicationServiceController {

	@Autowired
	private ApplicationService applicationService;
	
	@PostMapping("/addApplication")
	public ResponseEntity<ApplicationCreationResponse> addApplication(@RequestBody Application application , Locale locale) {
		 return new ResponseEntity<ApplicationCreationResponse>(applicationService.createApplication(application ,locale)
				,HttpStatus.OK);
	}
	
	@GetMapping("/verifyCustomerId/{cuiid}")
	public ResponseEntity<String> verifyCustomerId(@PathVariable("cuiid") String cuiid , Locale locale){
		String isVerified = applicationService.verifyCustomerId(cuiid ,  locale ,null);
		return new ResponseEntity<String>(isVerified,HttpStatus.OK);
	}
	
	
}

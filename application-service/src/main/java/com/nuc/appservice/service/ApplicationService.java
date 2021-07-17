package com.nuc.appservice.service;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.nuc.appservice.entity.Application;
import com.nuc.appservice.vo.ApplicationCreationResponse;

@Component
@Qualifier("applicationService")
public interface ApplicationService {

	String verifyCustomerId(String cuiid , Locale locale,StringBuilder errorMessage);
	ApplicationCreationResponse createApplication(Application application , Locale locale);
}

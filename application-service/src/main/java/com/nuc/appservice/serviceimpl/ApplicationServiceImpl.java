package com.nuc.appservice.serviceimpl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.nuc.appservice.application.constants.ApplicationConstants;
import com.nuc.appservice.entity.Application;
import com.nuc.appservice.service.ApplicationService;
import com.nuc.appservice.vo.ApplicationCreationResponse;
import com.nuc.appservice.vo.DemographicSaveRequest;
import com.nuc.appservice.vo.HttpReponseStatus;

import lombok.extern.slf4j.Slf4j;
@Service
@Transactional
@Slf4j
public class ApplicationServiceImpl implements ApplicationService{

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private MessageSource messageSource;

	@Override
	public String verifyCustomerId(String cuiid , Locale locale,StringBuilder errorMessage) {
		return isCustomerIdValid(cuiid,errorMessage) ?  ApplicationConstants.SUCCESS : ApplicationConstants.FAILED;
	}

	private boolean isCustomerIdValid(String cuiid,StringBuilder errorMessage) {
		if(cuiid.length() < 15 ) {
			errorMessage.append("application.creation.error.msg_2");
			return false;
		}else if(cuiid.length() > 15) {
			errorMessage.append("application.creation.error.msg_3");
			return false;
		}else if(!cuiid.matches("[0-9]+")) {//only number allowed as CUIID
			errorMessage.append("application.creation.error.msg_4");
			return false;
		}
		return true;
	}

	@Override
	public ApplicationCreationResponse createApplication(Application application , Locale locale) {
		ApplicationCreationResponse applicationCreationResponse = null;
		//Step 1 : verify Customer Id
		StringBuilder errorMessage = new StringBuilder(ApplicationConstants.BLANK_STRING);
		if(validateApplicationCreationRequest(application , errorMessage ) && ApplicationConstants.SUCCESS.equals(verifyCustomerId(application.getCuiid(), locale ,errorMessage))) {
			applicationCreationResponse = applicationCreationWorkFlow(application,locale);
		}else {
			applicationCreationResponse = new ApplicationCreationResponse();
			applicationCreationResponse.setResponseStatus
			(setHttpResponseStatus(ApplicationConstants.FAILED, errorMessage.toString(),locale,true));
		}
		return applicationCreationResponse;
	}
	private ApplicationCreationResponse applicationCreationWorkFlow(Application application , Locale locale) {
		ApplicationCreationResponse applicationCreationResponse = new ApplicationCreationResponse();
		//calltheInterfaces for refreshing the data
		Map<Boolean , String > interfaceResult = callInterfacesRefresh(application.getCuiid() , ApplicationConstants.APPLICATION_CREATION_FLOW,locale);
		if(interfaceResult.containsKey(true)) {//means we got the response as needed for application creation flow
			Map<Boolean , String > saveDemographicResultMap = saveDemographicData(application.getCuiid(),application.getApplicationNumber(),
												   ApplicationConstants.getInterfaceListByApplicationEvent(ApplicationConstants.APPLICATION_CREATION_FLOW) ,locale);
			if(saveDemographicResultMap.containsKey(true)) {
			applicationCreationResponse.setApplicationNumber(new Random().nextLong());
			applicationCreationResponse.setCuiid(application.getCuiid());
			applicationCreationResponse.setResponseStatus
			(setHttpResponseStatus(ApplicationConstants.SUCCESS, saveDemographicResultMap.get(true) , locale , true));
			}else {
				String errorKey = saveDemographicResultMap.get(false);
				applicationCreationResponse.setResponseStatus
				(setHttpResponseStatus(ApplicationConstants.FAILED,errorKey ,locale,false));
			}
		}else {
			//error response need to send to application
			String errorKey = interfaceResult.get(false);
			applicationCreationResponse.setResponseStatus
			(setHttpResponseStatus(ApplicationConstants.FAILED,errorKey ,locale,false));
		}
		return applicationCreationResponse;
	}
	
	private HttpReponseStatus setHttpResponseStatus(String status , String statusDesc , Locale locale , boolean isMessageSourceRequest) {
		HttpReponseStatus httpReponseStatus = new HttpReponseStatus();
		httpReponseStatus.setStatus(status);
		if(isMessageSourceRequest)
		httpReponseStatus.setStatusDesc(messageSource.getMessage(statusDesc, null, locale));
		else
		httpReponseStatus.setStatusDesc(statusDesc);
		return httpReponseStatus;
	}
	
	private boolean validateApplicationCreationRequest(Application application , StringBuilder errorMessage) {
		if(application.getCuiid() == null || application.getCuiid().isEmpty()) {
			errorMessage.append("application.creation.error.msg_1");
			return false;
		}
		return true;
	}

	private Map<Boolean , String> callInterfacesRefresh(String cuiid,String applicationEvent ,Locale locale) {
		//rest template will call to another MicroServices for refreshing the interface data
		List<String> interfacesList = ApplicationConstants.getInterfaceListByApplicationEvent(applicationEvent);
		log.info("**************callInterfaces***************************"+interfacesList);
		//TODO
		//call interfaces and validate the response for saving the data in customer data service
		return validateInterfacesResultForWorkFlow(locale);
	}
	private Map<Boolean , String> validateInterfacesResultForWorkFlow(Locale locale) {
		boolean allInterfacesResult = true;
		Map<Boolean , String> resultValidateMap = new LinkedHashMap<>();
		resultValidateMap.put(allInterfacesResult, null);
		return resultValidateMap;
	}
	
	
	private Map<Boolean , String > saveDemographicData(String uuid , Long applicationNumber , List<String> interfacesListForDemoSave , Locale locale) {
		HttpHeaders headers = new HttpHeaders();
		
		headers.setContentType(MediaType.APPLICATION_JSON);
		List<Locale> localeList = new ArrayList<>();
		localeList.add(locale);
		headers.setAcceptLanguageAsLocales(localeList);
		//prepare request for demographic save
		DemographicSaveRequest demographicSaveRequest = new DemographicSaveRequest();
		demographicSaveRequest.setApplicationNumber(applicationNumber);
		demographicSaveRequest.setCuiid(uuid);
		demographicSaveRequest.setInterfacesListToSaveDemographicData(interfacesListForDemoSave);
		
		HttpEntity<?> entity = new HttpEntity<DemographicSaveRequest>(demographicSaveRequest, headers);

		ResponseEntity<HttpReponseStatus> responseEntity = restTemplate.postForEntity("http://localhost:9011/customerData/saveDemographicData", entity, HttpReponseStatus.class);
		log.info("demographic service called"+responseEntity.getBody());
		//validate demographic save event for application creation event
		return validateDemographicServiceResponse(responseEntity);
	}
	//TODO
	private Map<Boolean , String > validateDemographicServiceResponse(ResponseEntity<HttpReponseStatus> entity) {
		Map<Boolean , String > resultMap = new LinkedHashMap<>();
		HttpReponseStatus responseStatus = entity.getBody();
		if(ApplicationConstants.SUCCESS.equalsIgnoreCase(responseStatus.getStatus())) {
			resultMap.put(true, "application.creation.success.msg");
		}else {
			resultMap.put(false, responseStatus.getStatusDesc());
		}
		return resultMap;
	}
	
}

package com.nuc.customer.data.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.nuc.customer.data.constants.CustomerDataDetailsConstants;
import com.nuc.customer.data.entity.Addresses;
import com.nuc.customer.data.entity.DemographicDetails;
import com.nuc.customer.data.repository.AddressesRepository;
import com.nuc.customer.data.repository.DemographicReposotory;
import com.nuc.customer.data.service.CustomerDataDetailsService;
import com.nuc.customer.data.vo.AddressVO;
import com.nuc.customer.data.vo.DemographicSaveRequest;
import com.nuc.customer.data.vo.HttpReponseStatus;
import com.nuc.customer.data.vo.PersonInterfaceDataResponse;
import com.nuc.customer.data.vo.ResponseDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class CustomerDataDetailsServiceImpl implements CustomerDataDetailsService{

	@Autowired
	private DemographicReposotory demographicReposotory;
	
	@Autowired
	private AddressesRepository addressesRepository;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private MessageSource messageSource;

	@Override
	public DemographicDetails getDemographicDetailsByCuiidAndApplicationNumber(DemographicDetails demographicDetails , Locale locale) {
		return demographicReposotory.findByApplicationNumberAndCuiid(demographicDetails.getApplicationNumber(), demographicDetails.getCuiid());
	}

	@Override
	public HttpReponseStatus saveDemographicDetails(DemographicSaveRequest demographicSaveRequest , Locale locale) {
		HttpReponseStatus httpReponseStatus = new HttpReponseStatus();
		//it will call getInterfaces data and will save interfaces related tables according to that//TODO
		Map<String , Object > toSaveMapForInterfaces = getInterfaceDataForIncomingRequestEvent(demographicSaveRequest, locale);
		if(toSaveMapForInterfaces.containsKey(CustomerDataDetailsConstants.INTERFACE_NAME_PERSONAL_ERROR)) {
			 httpReponseStatus.setStatus(CustomerDataDetailsConstants.FAILED);
			 httpReponseStatus.setStatusDesc((String)toSaveMapForInterfaces.get(CustomerDataDetailsConstants.INTERFACE_NAME_PERSONAL_ERROR)); 
		}else if(toSaveMapForInterfaces.containsKey(CustomerDataDetailsConstants.INTERFACE_NAME_FAMILY_ERROR)) {
			//TODO
		}else {
			//if there is not error found in any of interfaces then start save interfaces data
			if(toSaveMapForInterfaces.containsKey(CustomerDataDetailsConstants.INTERFACE_NAME_PERSONAL_INFO)) {
				savePersonalInterfaceData(demographicSaveRequest, (PersonInterfaceDataResponse)toSaveMapForInterfaces.get(CustomerDataDetailsConstants.INTERFACE_NAME_PERSONAL_INFO));
			}//TODO for other interfaces
			httpReponseStatus.setStatus(CustomerDataDetailsConstants.SUCCESS);
			httpReponseStatus.setStatusDesc(messageSource.getMessage("application.demographic.data.saved", null, locale));
		}
		return httpReponseStatus;
	}

	private Map<String , Object> getInterfaceDataForIncomingRequestEvent(DemographicSaveRequest demographicSaveRequest , Locale locale) {
		List<String> interfacesList = demographicSaveRequest.getInterfacesListToSaveDemographicData();
		Map<String , Object> interfaceMapDataForSave = new LinkedHashMap<>();
		if(interfacesList.contains(CustomerDataDetailsConstants.INTERFACE_NAME_PERSONAL_INFO)) {
			PersonInterfaceDataResponse personalDataResponse  =  getPersonInterfaceData(demographicSaveRequest.getApplicationNumber(),
					demographicSaveRequest.getCuiid(), locale);
			if(validatePersonData(personalDataResponse)) {
				interfaceMapDataForSave.put(CustomerDataDetailsConstants.
						INTERFACE_NAME_PERSONAL_INFO, personalDataResponse);
			}else {
				interfaceMapDataForSave.put(CustomerDataDetailsConstants.INTERFACE_NAME_PERSONAL_ERROR, personalDataResponse != null ? 
						personalDataResponse.getMessage() : messageSource.getMessage("application.demographic.interface.data.error_2", null, locale));
			}
		}else {
			//TODO
		}
		return interfaceMapDataForSave;
	}

	private PersonInterfaceDataResponse getPersonInterfaceData(Long applicationNumber , String cuiid , Locale locale) {
		PersonInterfaceDataResponse dataResponse = null;
		try {
			ResponseEntity<PersonInterfaceDataResponse> responseEntity =
					restTemplate.getForEntity(CustomerDataDetailsConstants.URI_FOR_PERSONAL_INFO, PersonInterfaceDataResponse.class);

			if(responseEntity != null && responseEntity.getBody() != null) {
				dataResponse = responseEntity.getBody();
			}
		} catch (RestClientException exception) {
			log.error("******************Exception caught in calling of getPersonInterfaceData*****************"+exception+"***"+exception.getMessage());
		}
		return dataResponse;
	}
	public void savePersonalInterfaceData(DemographicSaveRequest demographicSaveRequest , PersonInterfaceDataResponse personInterfaceDataResponse ) {

		DemographicDetails demographicDetails = demographicReposotory.findByApplicationNumberAndCuiid(demographicSaveRequest.getApplicationNumber(), 
				demographicSaveRequest.getCuiid());
		if(demographicDetails == null)
			demographicDetails = new  DemographicDetails();
		ResponseDTO responseDTO = personInterfaceDataResponse.getResponseTO();
		List<AddressVO> addressVOs = responseDTO.getAddresses();
		log.info("address*****************"+addressVOs);
		
		demographicDetails.setBirthDate(responseDTO.getBirthDate());
		demographicDetails.setBirthPlaceCode(responseDTO.getBirthPlaceCode());
		demographicDetails.setCitizenshipCode(responseDTO.getCitizenshipCode());
		demographicDetails.setFName(responseDTO.getFirstName());
		demographicDetails.setLName(responseDTO.getLastName());
		demographicDetails.setGenderCode(responseDTO.getGenderCode());
		demographicDetails.setMaritalStatusCode(responseDTO.getMaritalStatusCode());
		populateAddress(addressVOs, demographicDetails);
		//saving the data in Parent table demographic
		demographicReposotory.save(demographicDetails);

	}

	private void populateAddress(List<AddressVO> addressVO , DemographicDetails demographicDetails) {
		if(addressVO != null) {
			List<Addresses> addressesList = new ArrayList<>();
			for(AddressVO vo : addressVO) {
				Addresses addresses = addressesRepository.findByFlatNoAndBuildingNameAndPincodeAndAddressTypeCode
						(vo.getFlatNo(), vo.getBuildingName(), vo.getPincode(), vo.getAddressTypeCode());
				if(null == addresses)		
					addresses =	new Addresses();
				addresses.setAddressTypeCode(vo.getAddressTypeCode());
				addresses.setArea(vo.getArea());
				addresses.setBuildingName(vo.getBuildingName());
				addresses.setCityCode(vo.getCityCode());
				addresses.setDemographicDetails(demographicDetails);
				addresses.setFlatNo(vo.getFlatNo());
				addresses.setPincode(vo.getPincode());
				addresses.setStateCode(vo.getStateCode());
				addresses.setStreet(vo.getStreet());
				addressesList.add(addresses);
				demographicDetails.setAddresses(addressesList);
			}
			
			
		}
	}
	private boolean validatePersonData(PersonInterfaceDataResponse personInterfaceDataResponse) {
		if(personInterfaceDataResponse == null) {
			return false;
		}else if(CustomerDataDetailsConstants.STATUS_FOUND.equalsIgnoreCase(personInterfaceDataResponse.getStatus())) {
			return true;
		}
		return false;
	}

}

package com.nuc.appservice.application.constants;

import java.util.ArrayList;
import java.util.List;
public interface ApplicationConstants {
public static final String SUCCESS = "SUCCESS";
public static final String FAILED  = "FAILED";
public static final String APPLICATION_CREATION_FLOW="APPLICATION_CREATION_FLOW";
public static final String INTERFACE_NAME_PERSONAL_INFO="PERSONAL";
public static final String BLANK_STRING = "";

 static List<String> getInterfaceListByApplicationEvent(String applicationEvent){
	List<String> interfacesList = new ArrayList<>();
	if(APPLICATION_CREATION_FLOW.equalsIgnoreCase(applicationEvent)) {
		interfacesList.add(INTERFACE_NAME_PERSONAL_INFO);
	}else {
		System.out.println("Pending for others Application Event");
	}
	return interfacesList;
}
}

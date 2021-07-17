package com.nuc.customer.data.vo;

import java.time.Instant;
import java.util.List;

import lombok.Data;

@Data
 public class ResponseDTO{
   	private String uniqueId;

   	private String firstName;

   	private String lastName;

   	private Instant  birthDate;

   	private Integer   birthPlaceCode;
   	private Integer   genderCode;
   	private Integer   maritalStatusCode;
   	private Integer   citizenshipCode;
   	
   	private List<AddressVO> addresses;
   	

	}
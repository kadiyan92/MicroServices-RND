package com.nuc.customer.data.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "CU_DEMOGRAPHIC_DETAILS")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemographicDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long demographicId;
	private Long applicationNumber;
	private Long applicationId;
	@Column(name = "CUIID")
	private String cuiid;
	
	private String fName;
	private String lName;
	private Instant birthDate;
	private Integer birthPlaceCode;
	private Integer citizenshipCode;
	private Integer genderCode;
	private Integer maritalStatusCode;
	
	@OneToMany(mappedBy = "demographicDetails", cascade = CascadeType.ALL)
	private List<Addresses> addresses;
	
	


}

package com.nuc.customer.data.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "CU_ADDRESSES")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Addresses {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long addressId;
	
	private String addressTypeCode;
	private String flatNo;
	private String buildingName;
	private String street;
	private String  area;
	private Integer cityCode;
	private Integer stateCode;
	private Integer pincode;
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "DEMOGRAPHIC_ID")
	private DemographicDetails demographicDetails;
}

package com.nuc.customer.data.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AddressVO {

	private String addressTypeCode;
	private String flatNo;
	private String buildingName;
	private String street;
	private String  area;
	private Integer cityCode;
	private Integer stateCode;
	private Integer pincode;
}

package com.nuc.appservice.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationCreationResponse {
	@JsonInclude(value = Include.NON_NULL)
	private Long applicationNumber;
	@JsonInclude(value = Include.NON_NULL)
	private String cuiid;
	private HttpReponseStatus responseStatus;
}

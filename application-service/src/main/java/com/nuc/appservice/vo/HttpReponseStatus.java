package com.nuc.appservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HttpReponseStatus {

	private String status;
	private String statusDesc;
}

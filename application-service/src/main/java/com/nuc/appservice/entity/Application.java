package com.nuc.appservice.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "M_APPLICATION")
public class Application{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "APPLICATION_ID")
	private Long applicationId;
	@Column(name = "CUIID")
	private String cuiid;
	@Column(name = "APPLICATION_NUMBER")
	private Long applicationNumber;


}

package com.nuc.customer.data.vo;

import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DemographicSaveRequest {
private String cuiid;
private Long applicationNumber;
private List<String> interfacesListToSaveDemographicData;

}

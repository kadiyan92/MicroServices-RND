package com.nuc.customer.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nuc.customer.data.entity.Addresses;

@Repository
public interface AddressesRepository extends JpaRepository<Addresses, Long>{
	Addresses findByFlatNoAndDemographicDetailsApplicationNumberAndDemographicDetailsCuiid(String flatNo , Long applicationNumber , String cuiid);
	Addresses findByFlatNoAndBuildingNameAndPincodeAndAddressTypeCode(String flatNo,String buildingName , Integer pinCode,String addressTypeCode);
}

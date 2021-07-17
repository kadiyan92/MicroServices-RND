package com.nuc.appservice.repositroy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nuc.appservice.entity.Application;


@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long>{

	Application findByCuiidAndApplicationNumber(String cuiid , Long applicationNumber);
}

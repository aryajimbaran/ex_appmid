package com.numazu.export.util;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;

public class APNSInitialize {

	public static ApnsService initialize(ApnsService service) {
		System.out.println("Trying to initialize apns service");
		if (service == null) {
			// service =
			// APNS.newService().withCert("/home/Apns_Prod_Terbaik.p12",
			// "1234").withProductionDestination()
			// .build();
			service = APNS.newService().withCert("Apns_Prod_Terbaik.p12", "1234").withProductionDestination().build();
		}
		return service;
	}

	public static ApnsService initialize() {
		ApnsService service;
		System.out.println("Trying to initialize apns service");
		service = APNS.newService().withCert("Apns_Prod_Terbaik.p12", "1234").withProductionDestination().build();
		return service;
	}
}

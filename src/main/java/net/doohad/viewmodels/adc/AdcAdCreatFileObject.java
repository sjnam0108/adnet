package net.doohad.viewmodels.adc;

import net.doohad.models.adc.AdcAdCreative;

public class AdcAdCreatFileObject {

	private AdcAdCreative adCreat;
	private AdcJsonFileObject jsonFileObject;
	
	
	public AdcAdCreatFileObject(AdcAdCreative adCreat, AdcJsonFileObject jsonFileObject) {
		
		this.adCreat = adCreat;
		this.jsonFileObject = jsonFileObject;
	}

	
	public AdcAdCreative getAdCreat() {
		return adCreat;
	}

	public void setAdCreat(AdcAdCreative adCreat) {
		this.adCreat = adCreat;
	}

	public AdcJsonFileObject getJsonFileObject() {
		return jsonFileObject;
	}

	public void setJsonFileObject(AdcJsonFileObject jsonFileObject) {
		this.jsonFileObject = jsonFileObject;
	}
	
}

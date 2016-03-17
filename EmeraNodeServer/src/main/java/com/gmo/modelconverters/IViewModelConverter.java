package com.gmo.modelconverters;

public interface IViewModelConverter<VIEW,MODEL> {

	public VIEW buildViewModelObject(MODEL input);
	
	public MODEL buildDataModelObject(VIEW input);
	
}

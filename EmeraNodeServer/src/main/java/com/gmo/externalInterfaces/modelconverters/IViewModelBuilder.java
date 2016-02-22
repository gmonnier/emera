package com.gmo.externalInterfaces.modelconverters;

public interface IViewModelBuilder<VIEW,MODEL> {

	public VIEW buildViewModelObject(MODEL input);
	
}

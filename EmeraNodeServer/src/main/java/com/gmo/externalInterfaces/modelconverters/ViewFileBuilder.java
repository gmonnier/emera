package com.gmo.externalInterfaces.modelconverters;

import com.gmo.model.processconfig.files.ModelFileStored;

import processorNode.model.ViewFile;
import processorNode.model.ViewFileOrigin;

public class ViewFileBuilder implements IViewModelBuilder<ViewFile, ModelFileStored> {

	@Override
	public ViewFile buildViewModelObject(ModelFileStored input) {

		return new ViewFile(ViewFileOrigin.STORED, input.getName(), input.getId(), input.getLastModified().getTime(), input.getSize());

	}

}

package com.gmo.modelconverters;

import processorNode.viewmodel.ViewFile;
import processorNode.viewmodel.ViewFileOrigin;

import com.gmo.model.inputs.ModelFileStored;

public class FileStoredConverter implements IViewModelConverter<ViewFile, ModelFileStored> {

	@Override
	public ViewFile buildViewModelObject(ModelFileStored input) {

		return new ViewFile(ViewFileOrigin.STORED, input.getName(), input.getId(), input.getLastModified().getTime(), input.getSize());

	}

	@Override
	public ModelFileStored buildDataModelObject(ViewFile input) {
		throw new UnsupportedOperationException();
	}

}

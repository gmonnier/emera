package com.gmo.modelconverters;

import com.gmo.processorNode.viewmodel.ViewFile;
import com.gmo.processorNode.viewmodel.ViewFileOrigin;
import com.gmo.sharedobjects.model.inputs.ModelFileStored;

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

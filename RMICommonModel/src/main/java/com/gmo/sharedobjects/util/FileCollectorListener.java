package com.gmo.sharedobjects.util;

import com.gmo.sharedobjects.model.inputs.InputType;
import com.gmo.sharedobjects.model.inputs.ModelFileStored;

public interface FileCollectorListener {

	public void fileCollectionDone();
	
	public void fileCollected(InputType type, ModelFileStored mfs);

	public void collectionFailed();
	
}

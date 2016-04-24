package com.gmo.sharedobjects.util;

import com.gmo.sharedobjects.model.inputs.ModelFileStored;

public interface FileCollectorListener {

	public void fileCollected(ModelFileStored modelFile);
	
	public void checkCollectedFiles();
	
}

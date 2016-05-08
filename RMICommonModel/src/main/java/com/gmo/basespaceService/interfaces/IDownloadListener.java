package com.gmo.basespaceService.interfaces;

import java.io.File;

import com.gmo.basespaceService.model.FastQFile;

public interface IDownloadListener {

	public void downloadFailed(FastQFile inputFile);

	public void downloadSuccess(FastQFile inputFile, File output);

	public void downloadProgress(int percentage, FastQFile inputFile);

}

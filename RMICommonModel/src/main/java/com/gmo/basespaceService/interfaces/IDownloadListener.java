package com.gmo.basespaceService.interfaces;

import com.gmo.basespaceService.model.FastQFile;

public interface IDownloadListener {

	public void downloadFailed(FastQFile inputFile);

	public void downloadSuccess(FastQFile inputFile, String outputPath);

	public void downloadProgress(int percentage, FastQFile inputFile);

}

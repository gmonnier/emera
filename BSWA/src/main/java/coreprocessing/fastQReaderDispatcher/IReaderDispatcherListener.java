package coreprocessing.fastQReaderDispatcher;

public interface IReaderDispatcherListener {

	public void readProgress(int lineRead, int percent);

	public void readDone(int totalCount);
	
}

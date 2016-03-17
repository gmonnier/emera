package com.gmo.modelconverters;

import com.gmo.coreprocessing.fastQReaderSplitter.DataSplitterModel;
import com.gmo.processorNode.viewmodel.analyses.preprocessing.ViewDataSplitterModel;

public class DataSplitterConverter implements IViewModelConverter<ViewDataSplitterModel, DataSplitterModel> {

	@Override
	public ViewDataSplitterModel buildViewModelObject(DataSplitterModel splitterModel) {

		ViewDataSplitterModel viewModel = new ViewDataSplitterModel();

		viewModel.setRegexp(splitterModel.getPattern().toString());
		viewModel.setOutputName(splitterModel.getOutputName());
		viewModel.setAlias(splitterModel.getAlias());

		return viewModel;
	}

	@Override
	public DataSplitterModel buildDataModelObject(ViewDataSplitterModel input) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

}

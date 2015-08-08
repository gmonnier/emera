package model;

import java.util.ArrayList;
import java.util.List;

import logger.Log4JLogger;
import main.BaseSpacePlatformManager;

import org.apache.logging.log4j.Logger;

import basespaceObjects.FastQFile;
import basespaceObjects.UserInfo;
import basespaceObjects.UserRun;

import com.illumina.basespace.ApiClient;
import com.illumina.basespace.entity.FileCompact;
import com.illumina.basespace.entity.RunCompact;
import com.illumina.basespace.entity.SampleCompact;
import com.illumina.basespace.entity.User;
import com.illumina.basespace.param.FileParams;
import com.illumina.basespace.param.QueryParams;
import com.illumina.basespace.response.GetUserResponse;

public class BaseSpaceUserModel {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	public BaseSpaceUserModel() {

	}

	public List<UserRun> getListRunsCurrentUser() {

		LOG.info("Retrieve list of users runs");

		ApiClient client = BaseSpacePlatformManager.getInstance().getClientBS();
		List<UserRun> listRuns = new ArrayList<UserRun>();

		RunCompact[] list = client.getRuns(new QueryParams(20)).items();
		LOG.info(list.length + "runs extracted");
		for (int i = 0; i < list.length; i++) {
			
			LOG.debug("RUN ----> " + list[i].getName());
			
			UserRun currRun = new UserRun();
			currRun.setName(list[i].getName());
			currRun.setId(list[i].getId());
			currRun.setDateCreated(list[i].getDateCreated());
			currRun.setStatus(list[i].getStatus());
			currRun.setTotalSize(list[i].getTotalSize());

			SampleCompact[] sc = client.getSamples(list[i], null).items();
			FileParams fileParams = new FileParams(new String[] { ".fastq" });
			
			for (int sci = 0; sci < sc.length; sci++) {
				
				SampleCompact scc = sc[sci];
				LOG.debug("  SAMPLE ----> " + scc);
				if(!scc.getName().startsWith("Unindexed")) {
					FileCompact[] fcs = client.getFiles(scc, null).items();
					for (int j = 0; j < fcs.length; j++) {
						FileCompact fileComp = fcs[j];
						FastQFile newFile = new FastQFile(fileComp.getId());
						newFile.setContentType(fileComp.getContentType());
						newFile.setDateCreated(fileComp.getDateCreated());
						newFile.setPath(fileComp.getPath());
						newFile.setName(fileComp.getName());
						newFile.setSize(fileComp.getSize());
						
						currRun.getListFilesData().add(newFile);
						LOG.debug("         FILE --> " + fileComp.getName() + " size = " + fileComp.getSize());
					}
				} else {
					LOG.debug("Unindexed ingored sample.");
				}
			}
			
			listRuns.add(currRun);
		}

		return listRuns;
	}

	public UserInfo getCurrentUserInfo() {
		LOG.info("Retrieve current user info");

		ApiClient client = BaseSpacePlatformManager.getInstance().getClientBS();
		GetUserResponse ur = client.getCurrentUser();
		User user = ur.get();
		return new UserInfo(user.getName(), user.getId());
	}

}

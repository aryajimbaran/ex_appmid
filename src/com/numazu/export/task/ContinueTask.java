package com.numazu.export.task;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.numazu.export.Application;
import com.numazu.export.common.ConfigPool;
import com.numazu.export.util.TimeUtil;

public class ContinueTask implements Tasklet{
	private static final Logger LOGGER = LogManager.getLogger(ContinueTask.class);
	
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext arg1)
			throws Exception {
		
//		LOGGER.info("Start Continue Task = " + TimeUtil.currentDate());
		
		if (1 == ConfigPool.getResultAfterCreateFile()) {
//			LOGGER.info("Continue Update Data exist");
			ConfigPool.setResultAfterCreateFile(0);
			ConfigPool.setCheckFileTxt(0);
		} else {
//			LOGGER.info("update save point : "+ConfigPool.getValueMaxRow());
			ConfigPool.setCheckFileTxt(0);
			ConfigPool.setResultAfterCreateFile(0);
		}
		
//		LOGGER.info("Finish Continue Task = " + TimeUtil.currentDate());
		
		return RepeatStatus.FINISHED;
	}

}

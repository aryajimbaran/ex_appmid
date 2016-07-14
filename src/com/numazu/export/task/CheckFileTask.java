package com.numazu.export.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.numazu.export.bl.DummyDataBL;
import com.numazu.export.common.ConfigPool;
import com.numazu.export.model.Model;
import com.numazu.export.model.ServiceBean;
import com.numazu.export.util.TimeUtil;

public class CheckFileTask implements Tasklet{
	private static final Logger LOGGER = LogManager.getLogger(CheckFileTask.class);
	private static List<Model> modelsIdMax;
	private static Integer resultMaxValue = 0;
	private static Integer resultMaxId;
	private BufferedReader br;
	private String sCurrentLine = null;
	private String urlFileTxt = "last-row.txt";
	static Integer seq = 0;
	
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext arg1)
			throws Exception {

//		LOGGER.info("Start Check File = " + TimeUtil.currentDate());
		DummyDataBL dataBL = new DummyDataBL();
//		modelsIdMax = dataBL.getMaxId();
//		modelsIdMax = dataBL.getMaxId2();
//		resultMaxId = modelsIdMax.get(0).getId();
		resultMaxId = dataBL.getMaxIdMapper();
		ConfigPool.setValueMaxRow(resultMaxId);
		
		
		
		
//		Gson gson = new Gson();
//		String serviceBean = "{'serviceId':440343,'serviceType':'GR_TOPIC_UPDATE','serviceValue':'<@+@(%%)&%(?!?'?'/$+#($>','serviceValue2':'GRGapapa08479','serviceValue3':'0','serviceValue4':'0','serviceValue5':'0'}";
////		ServiceBean service = gson.fromJson(serviceBean, ServiceBean.class);
//		
//		
//		try {
//			JsonReader reader = new JsonReader(new StringReader(serviceBean));
//			reader.setLenient(true);
//			ServiceBean service = gson.fromJson(reader, ServiceBean.class);
//		} catch (JsonSyntaxException e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
		try {
//			LOGGER.info("Read file txt");
			File fileTxt = new File(urlFileTxt);
			if (fileTxt.exists()) {
				
				try {
					String keyRedis = ConfigPool.getJedis().get(ConfigPool.getKeySeq());
					seq = Integer.parseInt(keyRedis);
//					System.out.println("Sequence : " + seq);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					seq = 0;
				}
				if(seq == 0){
					br  = new BufferedReader(new FileReader(urlFileTxt));
					sCurrentLine = br.readLine();
					seq = Integer.valueOf(sCurrentLine);
					ConfigPool.getJedis().set(ConfigPool.getKeySeq(), Integer.toString(seq));
				}
				ConfigPool.setSeq(seq);
				ConfigPool.setValueNextCreate(seq);
				if (seq < resultMaxId) {
					ConfigPool.setCheckFileTxt(1);
					ConfigPool.setResultAfterCreateFile(1);
					ConfigPool.setUpdateDate(1);
				}
			} else {
				ConfigPool.setCheckFileTxt(1);
				ConfigPool.setResultAfterCreateFile(1);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		
		if (ConfigPool.getResultId() < resultMaxId && 1 == ConfigPool.getCheckFileTxt()) {
//			LOGGER.info("prepare load and writing data from line "+resultMaxValue+" to "+resultMaxId);
			ConfigPool.setResultAfterCreateFile(1);
			File dir = new File("exported");
		    if (!dir.exists()) dir.mkdir();
		} else {
//			LOGGER.info("Skip");
		}
		
//		LOGGER.info("Finish Check File = " + TimeUtil.currentDate());
		
		return  RepeatStatus.FINISHED;
	}
}

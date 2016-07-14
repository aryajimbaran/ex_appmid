package com.numazu.export.task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.notnoop.apns.ApnsService;
import com.numazu.export.bl.DummyDataBL;
import com.numazu.export.common.ConfigPool;
import com.numazu.export.model.Model;
import com.numazu.export.util.PushIOSThread;
import com.numazu.export.util.TimeUtil;

public class CreateFileTask implements Tasklet {
	private static final Logger LOGGER = LogManager.getLogger(CreateFileTask.class);
	private static List<Model> modelsIdMax;
	private static int resultMaxId;

	private static long resultTimeThread1;
	private static long resultTimeUpdate;

	// private static Integer resultData1 = 0;

	private static List<Model> listModels;
	private static File fileTxt;
	private static String resultTxt;
	private static String resultValue;
	private static Integer counter = 0;
	static int counterApp = 0;
	static ThreadPoolExecutor executor;
	static ApnsService serviceIOS;

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {

		// LOGGER.info("Start Create File = " + TimeUtil.currentDate());

		// DummyDataBL dataBL = new DummyDataBL();

		if (1 == ConfigPool.getResultAfterCreateFile()) {
			// modelsIdMax = dataBL.getMaxId();
			// resultMaxId = modelsIdMax.get(0).getId();

			// resultMaxId = dataBL.getMaxIdMapper();
			resultMaxId = ConfigPool.getValueMaxRow();

			checkStepRunning();
		} else {
			// LOGGER.info("Skip");
		}

		// LOGGER.info("Finish Create File = " + TimeUtil.currentDate());

		return RepeatStatus.FINISHED;
	}

	public static void checkStepRunning() throws SQLException {
		// LOGGER.info("Load Data & Creating");
		createFile();
	}

	public static void createFile() throws SQLException {
		DummyDataBL dataBL = new DummyDataBL();
		int valueNextCreate = ConfigPool.getValueNextCreate();
		// listModels = dataBL.getSelectAllBetween(valueNextCreate,
		// valueNextCreate + 5);
		// listModels = dataBL.getSelectAll(valueNextCreate);
		// listModels = dataBL.getSelectAll2(valueNextCreate);
		// listModels = dataBL.getSelectAllLimit(valueNextCreate, 1001);
		listModels = dataBL.getSelectAllMapper(valueNextCreate);
//		 listModels = dataBL.getSelectAllMapperLimit10(valueNextCreate);
		int size = listModels.size();
		counter = 0;
		counterApp = 0;

		if (size > 10000) {
			counterApp = size / 100;
		} else if (size > 1000) {
			counterApp = size / 10;
		}else if (size > 100) {
			counterApp = 100;
		}
		// System.out.println("CounterApp1 = " + counterApp);

		// int resultMappingThread1 = 1;

		File dir = new File("exported");
		if (!dir.exists())
			dir.mkdir();

		if (1 != ConfigPool.getUpdateDate()) {

			final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			final List<Future<Model>> results = new LinkedList<Future<Model>>();

			// Theard 1
			long startTimeThread1 = System.currentTimeMillis();
			// for (Model modelTheard1 : listModels) {
			// doLoopSendToJms2(modelTheard1, counterApp);
			// }

			listModels.stream().parallel().forEachOrdered(t -> doLoopSendToJms2(t, counterApp));

			// resultMaxId = listModels.get(listModels.size()-1).getId() + 1;

			long endTimeThread1 = System.currentTimeMillis();
			resultTimeThread1 = (resultTimeThread1 + endTimeThread1 - startTimeThread1);

		} else {
			long startTimeUpdate = System.currentTimeMillis();
			// for (Model model : listModels) {
			// doLoopSendToJms2(model, counterApp);
			// }

			listModels.stream().parallel().forEachOrdered(t -> doLoopSendToJms2(t, counterApp));

			// resultMaxId = listModels.get(listModels.size()-1).getId() + 1;

			long endTimeUpdate = System.currentTimeMillis();
			resultTimeUpdate = (resultTimeUpdate + endTimeUpdate - startTimeUpdate);
			ConfigPool.setUpdateDate(0);
		}
		updateSeq(resultMaxId);

	}

	private static void doLoopSendToJms(Model modelTheard1, Integer counterApp) {
		// System.out.println("CounterApp = " + counterApp);
		// System.out.println("Counter = " + counter);

		if (counter.equals(counterApp)) {
			updateSeqFile(modelTheard1.getId());
			counter = 0;
			// System.out.println("Counter = " + counter + " Seq = " +
			// ConfigPool.getJedis().get(ConfigPool.getKeySeq()));
		}

		// else{
		// Boolean b = counter == counterApp;
		// System.out.println("Result = " + b.toString() + " Counter = " +
		// counter + "_" + "CounterApp = " + counterApp);
		// }

		// MessagingTask.doSendQueue(modelTheard1.getContent(),
		// modelTheard1.getUserId());
		MessagingTask.doSendRabbitMQ(modelTheard1.getContent(), modelTheard1.getUserId(),
				ConfigPool.getConfigRabbitMQ().getChannel());
		System.out.println("Proses id = " + modelTheard1.getId() + " Seq = "
				+ ConfigPool.getJedis().get(ConfigPool.getKeySeq()) + " Waktu = " + TimeUtil.currentDate());
		counter = counter + 1;
	}

	private static void doLoopSendToJms2(Model modelTheard1, Integer counterApp) {
		if (counter.equals(counterApp)) {
			updateSeqFile(modelTheard1.getId());
			counter = 0;
			MessagingTask.doSendRabbitMQ(modelTheard1.getContent(), modelTheard1.getUserId(),
					ConfigPool.getConfigRabbitMQ().getChannelRabbitMQ());
			System.out.println("Proses id = " + modelTheard1.getId() + " Seq = "
					+ ConfigPool.getJedis().get(ConfigPool.getKeySeq()) + " Waktu = " + TimeUtil.currentDate());
			PushIOSThread pushIOSThread = new PushIOSThread(null, null, null, 0, null, ConfigPool.getDataSource());
			try {
				if (pushIOSThread.getDeviceToken(modelTheard1.getUserId()) != null) {
					MessagingTask.sendPush(modelTheard1.getUserId(), modelTheard1.getContent(),
							String.valueOf(modelTheard1.getId()), ConfigPool.getExecutor(), ConfigPool.getServiceIOS()); // plain
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			MessagingTask.doSendRabbitMQ(modelTheard1.getContent(), modelTheard1.getUserId(),
					ConfigPool.getConfigRabbitMQ().getChannelRabbitMQ());
			System.out.println("Proses id = " + modelTheard1.getId() + " Seq = " + ConfigPool.getSeq() + " Waktu = "
					+ TimeUtil.currentDate());
			PushIOSThread pushIOSThread = new PushIOSThread(null, null, null, 0, null, ConfigPool.getDataSource());
			try {
				if (pushIOSThread.getDeviceToken(modelTheard1.getUserId()) != null) {
					MessagingTask.sendPush(modelTheard1.getUserId(), modelTheard1.getContent(),
							String.valueOf(modelTheard1.getId()), ConfigPool.getExecutor(), ConfigPool.getServiceIOS()); // plain
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		counter = counter + 1;
	}

	private static void updateSeq(Integer resultMaxId) {
		updateSeqFile(resultMaxId);

		ConfigPool.setResultId(resultMaxId);
	}

	private static void updateSeqFile(Integer resultMaxId) {
		try {
			FileWriter fw = new FileWriter("last-row.txt");
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(resultMaxId.toString());
			bw.close();

			ConfigPool.getJedis().set(ConfigPool.getKeySeq(), Integer.toString(resultMaxId));
			ConfigPool.setSeq(resultMaxId);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
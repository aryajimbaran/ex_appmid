package com.numazu.export.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.numazu.export.common.ConfigPool;
import com.numazu.export.model.FileDownloadBean;
import com.numazu.export.model.MessageBean;
import com.numazu.export.util.EncryptDecryptTfs.EncryptMode;

public class PushIOSThread implements Runnable{
	ApnsService service;
	Connection con;
	private String msisdn;
	private String messageId;
	private String serviceId;
	private int type = 0; // 0 message, 1 other
	private DataSource dataSource;
	private JdbcTemplate myTemplate;
	public PushIOSThread(ApnsService service, String msisdn, String messageId, int type, 
			String serviceId, DataSource dataSource){
		this.msisdn = msisdn;
		this.messageId = messageId;
		this.type = type;
		this.service = service;
		this.serviceId = serviceId;
		this.dataSource = dataSource;
		this.myTemplate = new JdbcTemplate(dataSource);
		this.myTemplate.setFetchSize(30000);
	}

	private void initializeCon() throws SQLException{
		con = dataSource.getConnection();
	}

	private void push(String msisdn,String token, String message) throws SQLException{
		try {
			if (service == null) {
				//			APNSInitialize.initialize();
				ConfigPool.getServiceIOS();
			}
			addTokenCount(msisdn);
			String payload = APNS.newPayload().alertBody(message).sound("suling.caf")
					.badge(Integer.parseInt(getTokenCount(msisdn))).customField("type", 1).customField("msisdn", msisdn)
					.build();
			System.out.println("Payload = " + payload);
			service.push(token, payload);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void push(String msisdn, String token, String message, String message_id, String username) throws SQLException{
		if(service == null){
			APNSInitialize.initialize(service);
		}
		addTokenCount(msisdn);
		String payload = APNS.newPayload()
				.alertBody(message)
				.sound("suling.caf")
				.badge(Integer.parseInt(getTokenCount(msisdn)))
				.customField("iosid", message_id)
				.customField("type", 0)
				.customField("username", username)
				.customField("msisdn", msisdn)
				.build();
		System.out.println("Payload = "+payload);
		service.push(token, payload);
	}

	public String getDeviceToken(String msisdn) throws SQLException{
		String result = null;
		PreparedStatement preparedStatement = null;
		try {
			initializeCon();
			String selectSQLMax = "SELECT TOKEN FROM IOS_TOKEN WHERE MSISDN = ?";
			preparedStatement = con.prepareStatement(selectSQLMax);
			preparedStatement.setString(1, msisdn);
			ResultSet rsMax = preparedStatement.executeQuery();
			while (rsMax.next()) {
				result = rsMax.getString(1);
			}
			preparedStatement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			System.out.println("Failed to get Token, error = "+e);
		} finally{
			con.close();
		}
		return result;
	}

	private MessageBean getMessage(String messageID) throws SQLException{
		MessageBean messageBean = new MessageBean();
		PreparedStatement preparedStatementMax = null;
		try {
			initializeCon();
			String selectSQLMax = "SELECT * FROM MESSAGE_HO_SELECT WHERE MESSAGE_ID = ?";
			preparedStatementMax = con.prepareStatement(selectSQLMax);
			preparedStatementMax.setString(1, messageID);
			ResultSet rsMax = preparedStatementMax.executeQuery();
			while (rsMax.next()) {
				String messageid = rsMax.getString(1);
				String message = rsMax.getString(2);
				String msisdnsource = rsMax.getString(3);
				String msisdntarget = rsMax.getString(4);
				Integer status = rsMax.getInt(5);
				String date = String.valueOf(rsMax.getDate(6));
				Integer flag = rsMax.getInt(7);
				Integer messagetype = rsMax.getInt(8);

				try{
					EncryptDecryptTfs encryption = new EncryptDecryptTfs();
					String key = encryption.SHA256(VariablesUtil.keyEncryption256, 32);
					String messagedecrypt = encryption.encryptDecrypt(message, 
							key, EncryptMode.DECRYPT, 
							VariablesUtil.ivEcnryption256);

					messageBean.setMessageId(messageid);
					messageBean.setMessage(messagedecrypt);
					messageBean.setSource(msisdnsource);
					messageBean.setTarget(msisdntarget);
					messageBean.setStatus(status);
					messageBean.setDate(date);
					messageBean.setHistoryFlag(flag);
					messageBean.setMessageType(messagetype);

				}catch(Exception e){
					e.printStackTrace();
					System.out.println("Failed to decrypt "+e);
				}
			}
			preparedStatementMax.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			System.out.println("Failed to get message, error = "+e);
		} finally{
			con.close();
		}
		return messageBean;
	}
	
	private FileDownloadBean getFile(String messageID) throws SQLException{
		FileDownloadBean fileBean = new FileDownloadBean();
		PreparedStatement preparedStatementMax = null;
		try {
			initializeCon();
			String selectSQLMax = "SELECT FILE_PROCESS_ID,MSISDN_SOURCE,MSISDN_TARGET"
					+ ",FILE_NAME,FILE_SIZE,MESSAGE_TYPE,FILE_COUNT FROM FILE_TRANS_NEW WHERE FILE_PROCESS_ID = ?";
			preparedStatementMax = con.prepareStatement(selectSQLMax);
			preparedStatementMax.setString(1, messageID);
			ResultSet rsMax = preparedStatementMax.executeQuery();
			while (rsMax.next()) {
				String fileProcessID = rsMax.getString(1);
				String sourceId = rsMax.getString(2);
				String targetId = rsMax.getString(3);
				String fileName = rsMax.getString(4);
				Long fileSize = rsMax.getLong(5);
				Long messageType = rsMax.getLong(6);
				Long fileCount = rsMax.getLong(7);

				fileBean.setFileProcessID(fileProcessID);
				fileBean.setSourceId(sourceId);
				fileBean.setTargetId(targetId);
				fileBean.setFileName(fileName);
				fileBean.setFileSize(fileSize);
				fileBean.setMessageType(messageType);
				fileBean.setFileCount(fileCount);

			}
			preparedStatementMax.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			System.out.println("Failed to get file, error = "+e);
		} finally{
			con.close();
		}
		return fileBean;
	}
	
	private String getUserName(String msisdn) throws SQLException{
		String username = null;
		PreparedStatement preparedStatementMax = null;
		try {
			initializeCon();
			String selectSQLMax = "SELECT USERNAME FROM USER_DATA WHERE MSISDN = ?";
			preparedStatementMax = con.prepareStatement(selectSQLMax);
			preparedStatementMax.setString(1, msisdn);
			ResultSet rsMax = preparedStatementMax.executeQuery();
			while (rsMax.next()) {
				username = rsMax.getString(1);
			}
			preparedStatementMax.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			System.out.println("Failed to get message, error = "+e);
		} finally{
			con.close();
		}
		return username;
	}

	private String getGroupName(String groupname) throws SQLException{
		String username = null;
		PreparedStatement preparedStatementMax = null;
		try {
			initializeCon();
			String selectSQLMax = "SELECT GROUP_NAME FROM GROUP_NAME WHERE GROUP_ID = ?";
			preparedStatementMax = con.prepareStatement(selectSQLMax);
			preparedStatementMax.setString(1, groupname);
			ResultSet rsMax = preparedStatementMax.executeQuery();
			while (rsMax.next()) {
				username = rsMax.getString(1);
			}
			preparedStatementMax.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			System.out.println("Failed to get message, error = "+e);
		} finally{
			con.close();
		}
		return username;
	}
	
	private boolean sendNotif(String msisdn, String message) throws SQLException{
		String token = getDeviceToken(msisdn);
		if(token != null){
			System.out.println("Token = "+token);
			push(msisdn,token, message);
			return true;
		}else{
			System.out.println("Token is null");
			return false;			
		}
	}

	private boolean sendNotifMessage(String msisdn, String messageId) throws SQLException{
		String token = getDeviceToken(msisdn);
		String message = null;
		String username = null;
//		String groupname = null;
		
		if(token != null){
			System.out.println("Token = "+token);
			MessageBean messageBean = getMessage(messageId);
			
			
			if ((messageBean.getTarget()).startsWith("GR")){
				username = getGroupName(messageBean.getTarget());
				message = username+" : "+messageBean.getMessage();
			}else{
				username = getUserName(messageBean.getSource());
				message = username+" : "+messageBean.getMessage();
			}
			if(message.length() > 253)
			{
				message = message.substring(0, 250) + "...";
				push(msisdn,token, message, messageBean.getMessageId(), username);
//				push(token, message, messageBean.getMessageId());

			}
			else{
				push(msisdn,token, message, messageBean.getMessageId(), username);
//				push(token, message, messageBean.getMessageId());

			}
			return true;
		}else{
			System.out.println("Token is null");
			return false;		
		}
	}
	
	private boolean sendNotifFile(String msisdn, String messageId) throws SQLException{
		String token = getDeviceToken(msisdn);
		String message = null;
		if(token != null){
			System.out.println("Token = "+token);
			FileDownloadBean fileBean = getFile(messageId);
			String username = getUserName(fileBean.getSourceId());

			if ((fileBean.getTargetId()).startsWith("GR")){
				String groupname = getGroupName(fileBean.getTargetId());
				message = username+" has send Image to "+groupname;
			}else{
				message = username+" has sent an Image";
			}
			if(message.length() > 253)
			{
				message = message.substring(0, 250) + "...";
				push(msisdn, token, message, fileBean.getFileProcessID(), username);

			}
			else{
				push(msisdn, token, message, fileBean.getFileProcessID(), username);

			}
			return true;
		}else{
			System.out.println("Token is null");
			return false;		
		}
	}
	
	public void addTokenCount(String msisdn) throws SQLException{
		String result = null;
		PreparedStatement preparedStatement = null;
		try {
			initializeCon();
			String selectSQLMax = "CALL exec_ios_token2(?)";
			preparedStatement = con.prepareStatement(selectSQLMax);
			preparedStatement.setString(1, msisdn);
			preparedStatement.executeQuery();
			preparedStatement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			System.out.println("Failed to add Token, error = "+e);
		} finally{
			con.close();
		}
	}
	
	public String getTokenCount(String msisdn) throws SQLException{
		String result = null;
		PreparedStatement preparedStatement = null;
		try {
			initializeCon();
			String selectSQLMax = "SELECT ApnsCount FROM IOS_TOKEN WHERE MSISDN = ?";
			preparedStatement = con.prepareStatement(selectSQLMax);
			preparedStatement.setString(1, msisdn);
			ResultSet rsMax = preparedStatement.executeQuery();
			while (rsMax.next()) {
				result = rsMax.getString(1);
			}
			preparedStatement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			System.out.println("Failed to get Token, error = "+e);
		} finally{
			con.close();
		}
		return result;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(type == 0){
			try {
				if(sendNotifMessage(msisdn, messageId)){
					System.out.println("Success send push apns for Service Id "+serviceId);
				}else{
					System.out.println("Failed send push apns for Service Id "+serviceId);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if (type == 1){
			try {
				if(sendNotif(msisdn, messageId)){
					System.out.println("Success send push apns for Service Id "+serviceId);
				}else{
					System.out.println("Failed send push apns for Service Id "+serviceId);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			try {
				if(sendNotifFile(msisdn, messageId)){
					System.out.println("Success send push apns for Service Id "+serviceId);
				}else{
					System.out.println("Failed send push apns for Service Id "+serviceId);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

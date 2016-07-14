package com.numazu.export.model;

import java.io.Serializable;

public class FileDownloadBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
		private String fileProcessID;
		private String sourceId;
		private String targetId;
		private String fileName;
		private long fileSize;
		private long messageType;
		private long fileCount;
		
		public String getFileProcessID() {
			return fileProcessID;
		}

		public void setFileProcessID(String fileProcessID) {
			this.fileProcessID = fileProcessID;
		}

		public String getSourceId() {
			return sourceId;
		}

		public void setSourceId(String sourceId) {
			this.sourceId = sourceId;
		}

		public String getTargetId() {
			return targetId;
		}

		public void setTargetId(String targetId) {
			this.targetId = targetId;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public long getFileSize() {
			return fileSize;
		}

		public void setFileSize(long fileSize) {
			this.fileSize = fileSize;
		}

		public long getMessageType() {
			return messageType;
		}

		public void setMessageType(long messageType) {
			this.messageType = messageType;
		}

		public long getFileCount() {
			return fileCount;
		}

		public void setFileCount(long fileCount) {
			this.fileCount = fileCount;
		}


}
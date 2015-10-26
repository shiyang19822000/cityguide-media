package com.cy.cityguide.media.constant;

public class Constant {

	public static final String ROOT_NODE = "root";
	public static final Integer LIMIT = 15;

	public static class ResourceType {
		public static final Integer TEXT = 1;
		public static final Integer PICTURE = 2;
		public static final Integer AUDIO = 3;
		public static final Integer VIDEO = 4;
		public static final Integer OTHER = 5;
		
	}

	public static final String PUBLIC_KEY = "ucloudyulong.huang@visualchina.com14361775380001915261323";
	public static final String PRIVATE_KEY = "d1cb70801f0bc4ddb7a7cf5f75f5c7b0a1d30e29";
	public static final String BUCKET_NAME = System.getenv("bucket_name");
	public static final String IMAGE_URL = "http://"+BUCKET_NAME+".ufile.ucloud.cn/";
	public static final String IMAGE_URL_COM = "http://"+BUCKET_NAME+".ufile.ucloud.com.cn/";

	public static final String AUTHMESSAGE_PREFIX = "UCloud" + " " + PUBLIC_KEY + ":";

	public static final String AUTHORIZATION = "Authorization";

	public static final String IMAGE_CONTENT_TYPE = "image/jpeg";
	public static final String TXT_CONTENT_TYPE = "text/plain";
	public static final String AUDIO_CONTENT_TYPE ="audio/mpeg3";
	public static final String VIDEO_CONTENT_TYPE = "video/mp4";
	public static final String OTHER_CONTENT_TYPE = "application/octet-stream";

	public static final Integer ETAG_OUTDATE_TIME = 4;

	public static class UploadStatus {
		public static final Integer READY = 0;
		public static final Integer FINISH = 1;
		public static final Integer ABORT = 2;
		public static final Integer OUTDATE = 3;
	}
	
	public static final String VALIDATION_UPDATE="UPDATE";
	public static final String VALIDATION_CREATE="CREATE";
	
}

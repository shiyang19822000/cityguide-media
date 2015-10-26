package com.cy.cityguide.media.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cy.cityguide.media.constant.Constant;
import com.cy.cityguide.media.dao.NodeDao;
import com.cy.cityguide.media.dao.ResourceDao;
import com.cy.cityguide.media.exception.BadRequestBusinessException;
import com.cy.cityguide.media.parameter.CreateNodeParameter;
import com.cy.cityguide.media.parameter.CreateResourceParameter;
import com.cy.cityguide.media.parameter.FinishUploadParameter;
import com.cy.cityguide.media.parameter.UploadMsgParameter;
import com.cy.cityguide.media.result.FinishUploadResult;
import com.cy.cityguide.media.result.InitUploadResult;
import com.cy.cityguide.media.result.Node;
import com.cy.cityguide.media.result.UploadMsgResult;
import com.cy.cityguide.media.service.UploadService;
import com.cy.cityguide.media.tool.Authorization;
import com.cychina.platform.id.IdGenerator;

@Service
@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
public class UploadServiceImpl implements UploadService {

	private Logger logger = Logger.getLogger(UploadServiceImpl.class);

	@Autowired
	private NodeDao nodeDao;

	@Autowired
	private ResourceDao resourceDao;

	@Autowired
	private IdGenerator idGenerator;

	private String getContentType(String content) {
		if (content.equals("1")) {
			return Constant.TXT_CONTENT_TYPE;
		} else if (content.equals("2")) {
			return Constant.IMAGE_CONTENT_TYPE;
		} else if (content.equals("3")) {
			return Constant.AUDIO_CONTENT_TYPE;
		} else if (content.equals("4")) {
			return Constant.VIDEO_CONTENT_TYPE;
		} else if (content.equals("5")) {
			return Constant.OTHER_CONTENT_TYPE;
		} else {
			return "";
		}
	}

	private int getResourceType(String content) {
		System.err.println(content);
		if (content.equals("1")) {
			return Constant.ResourceType.TEXT;
		} else if (content.equals("2")) {
			return Constant.ResourceType.PICTURE;
		} else if (content.equals("3")) {
			return Constant.ResourceType.AUDIO;
		} else if (content.equals("4")) {
			return Constant.ResourceType.VIDEO;
		} else if (content.equals("5")) {
			return Constant.ResourceType.OTHER;
		}else{
			return 0;
		}
	}

	@Override
	public InitUploadResult initiateMultipartUpload(String path, String contentType)
			throws BadRequestBusinessException {
		logger.info("init parameter: path=" + path + " contentType=" + contentType);
		if (path == null || path.trim().equals("")) {
			throw new BadRequestBusinessException("path is null");
		}
		if (path.startsWith("/")) {
			throw new BadRequestBusinessException("path cannot be start with /");
		}
		if (!path.contains("/")) {
			throw new BadRequestBusinessException("path must can be split by /");
		}
		if (contentType == null || contentType.trim().equals("")) {
			throw new BadRequestBusinessException("contentType is null");
		}
		String content = getContentType(contentType);
		if (content.equals("")) {
			throw new BadRequestBusinessException("error contentType ");
		}
		InitUploadResult init = new InitUploadResult();
		String authMessage = Authorization.getAuthorizationMessage("POST", "", path);
		String authMessageReturn = Authorization.getAuthorizationMessage("PUT", content, path);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost post = new HttpPost(Constant.IMAGE_URL + path + "?uploads");
		post.setHeader("Authorization", authMessage);
		HttpResponse response = null;
		try {
			response = httpClient.execute(post);
			HttpEntity entityOfInitUpload = response.getEntity();
			String initStr = EntityUtils.toString(entityOfInitUpload);
			com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(initStr);
			String uploadId = jsonObject.getString("UploadId");
			Integer blkSize = jsonObject.getInteger("BlkSize");
			if ((uploadId == null || uploadId.equals("")) && (blkSize == null || blkSize.toString().equals(""))) {
				throw new BadRequestBusinessException("failed: " + initStr);
			}
			init.setUploadId(uploadId);
			UploadMsgParameter uploadMsgParameter = new UploadMsgParameter();
			uploadMsgParameter.setUploadId(uploadId);
			uploadMsgParameter.setStatus(Constant.UploadStatus.READY);
			uploadMsgParameter.setType(getResourceType(contentType));
			uploadMsgParameter.setInitPath(path);
			resourceDao.createUploadMsg(uploadMsgParameter);
			init.setBlockSize(blkSize);
			init.setAuthorization(authMessageReturn);
			init.setContentType(content);
			init.setUploadPartUrl(Constant.IMAGE_URL + path);
			init.setBucketName(Constant.BUCKET_NAME);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return init;
	}

	@Override
	public FinishUploadResult finishMultipartUpload(FinishUploadParameter parameter)
			throws BadRequestBusinessException {
		logger.info("finish parameter: parameter= " + parameter);
		String uploadId = parameter.getUploadId();
		UploadMsgResult resultMsg = resourceDao.getUploadMsg(uploadId);
		String path = resultMsg.getInitPath();
		String newPath = path + "_new";
		List<String> listEtags = parameter.getEtags();
		if (uploadId == null || uploadId.trim().equals("")) {
			throw new BadRequestBusinessException("uploadId is null");
		}
		if (listEtags == null || listEtags.isEmpty() || listEtags.size() == 0) {
			throw new BadRequestBusinessException("listEtags is null");
		}
		int etagIndex = 1;
		for (String etag : listEtags) {
			if (etag == null || etag.trim().equals("")) {
				throw new BadRequestBusinessException("etag " + etagIndex + " is null");
			}
			etagIndex++;
		}

		String eTags = "";
		for (int i = 0; i < listEtags.size(); i++) {
			if (i == 0) {
				eTags += listEtags.get(i);
			} else {
				eTags += "," + listEtags.get(i);
			}
		}
		Integer strStatus = resourceDao.getUploadMsg(uploadId).getStatus();
		if (strStatus != Constant.UploadStatus.READY) {
			throw new BadRequestBusinessException("uploadId 已经失效");
		}
		String authMessage = Authorization.getAuthorizationMessage("POST", "text/plain", path);
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost post = new HttpPost(Constant.IMAGE_URL + path + "?uploadId=" + uploadId + "&newKey=" + newPath);
		post.setHeader(Constant.AUTHORIZATION, authMessage);
		post.setHeader("Content-Type", "text/plain");
		HttpEntity sEntity = null;
		FinishUploadResult result = new FinishUploadResult();
		String resultKey = "";
		String bucket = "";
		try {
			sEntity = new StringEntity(eTags);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		post.setEntity(sEntity);
		try {
			HttpResponse response = client.execute(post);
			String str = EntityUtils.toString(response.getEntity(), "utf8");
			JSONObject jsonObject = JSON.parseObject(str);
			Long fileSize = jsonObject.getLong("FileSize");
			resultKey = jsonObject.getString("Key");
			bucket = jsonObject.getString("Bucket");
			if ((fileSize == null || fileSize.toString().trim().equals(""))
					&& (resultKey == null || resultKey.trim().equals(""))) {
				throw new BadRequestBusinessException("failed: " + str);
			} else {
				String[] nodesName = path.split("/");
				// website/test/aa.jpg
				if (nodesName.length > 2) {
					String firstNodeName = nodesName[0];
					List<Node> findFirstNodes = nodeDao.findByName(firstNodeName);
					String firstNodeId = "";
					if (findFirstNodes == null || findFirstNodes.isEmpty() || findFirstNodes.size() == 0) {
						CreateNodeParameter createFirstNode = new CreateNodeParameter();
						firstNodeId = idGenerator.next().toString();
						createFirstNode.setId(firstNodeId);
						createFirstNode.setName(firstNodeName);
						createFirstNode.setParentId("root");
						createFirstNode.setPath(firstNodeName);
						nodeDao.create(createFirstNode);
					} else {
						firstNodeId = findFirstNodes.get(0).getId();
					}
					String secondNodeName = nodesName[1];
					List<Node> findSecondNodes = nodeDao.findByName(secondNodeName);
					String secondNodeId = "";
					if (findSecondNodes == null || findSecondNodes.isEmpty() || findSecondNodes.size() == 0) {
						CreateNodeParameter createSecondNode = new CreateNodeParameter();
						secondNodeId = idGenerator.next().toString();
						createSecondNode.setId(secondNodeId);
						createSecondNode.setName(secondNodeName);
						createSecondNode.setPath(firstNodeName + "/" + secondNodeName);
						createSecondNode.setParentId(firstNodeId);
						nodeDao.create(createSecondNode);
					} else {
						secondNodeId = findSecondNodes.get(0).getId();
					}
					CreateResourceParameter resource = new CreateResourceParameter();
					String resId = idGenerator.next().toString();
					resource.setId(resId);
					resource.setNodeId(secondNodeId);
					resource.setName(path);
					resource.setKeyWord(resultKey);
					resource.setType(resultMsg.getType());
					resource.setBucket(bucket);
					resourceDao.create(resource);
					result.setResourceId(resId);
					result.setFileSize(fileSize);
					result.setKey(resultKey);
					result.setResourceUrl(Constant.IMAGE_URL_COM + resultKey);
					UploadMsgParameter updateUploadMsg = new UploadMsgParameter(uploadId, Constant.UploadStatus.FINISH);
					resourceDao.modifyUploadMsgStatus(updateUploadMsg);
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public void abortMultipartUpload(String uploadId) throws BadRequestBusinessException {
		UploadMsgResult result = resourceDao.getUploadMsg(uploadId);
		String key = result.getInitPath();
		String authMessage = Authorization.getAuthorizationMessage("DELETE", "", key);
		CloseableHttpClient client = HttpClients.createDefault();
		HttpDelete delete = new HttpDelete(Constant.IMAGE_URL + key + "?uploadId=" + uploadId);
		delete.setHeader(Constant.AUTHORIZATION, authMessage);
		try {
			HttpResponse response = client.execute(delete);
			String str = EntityUtils.toString(response.getEntity(), "utf8");
			if (!response.getStatusLine().toString().contains("200")) {
				JSONObject jsonObject = JSON.parseObject(str);
				String errMsg = jsonObject.getString("ErrMsg");
				throw new BadRequestBusinessException("failed: " + errMsg);
			} else {
				UploadMsgParameter parameter = new UploadMsgParameter();
				parameter.setUploadId(uploadId);
				parameter.setStatus(Constant.UploadStatus.ABORT);
				resourceDao.modifyUploadMsgStatus(parameter);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Integer getUploadMessage(String uploadId) {
		UploadMsgResult uploadMsg = resourceDao.getUploadMsg(uploadId);
		Long createTime = uploadMsg.getCreateTime().getTime();
		Long today = new Date().getTime();
		Long pastTime = today - createTime;
		Long outDate = (long) (1000 * 60 * 60 * 24 * Constant.ETAG_OUTDATE_TIME);
		if (pastTime > outDate) {
			if (!uploadMsg.getStatus().equals(Constant.UploadStatus.OUTDATE)) {
				uploadMsg.setStatus(Constant.UploadStatus.OUTDATE);
				resourceDao.modifyUploadMsgStatus(uploadMsg);
			}
		}
		return uploadMsg.getStatus();
	}

	@Override
	public boolean deleteFile(String key) {
		String authMessage = Authorization.getAuthorizationMessage("DELETE", "", key);
		CloseableHttpClient client = HttpClients.createDefault();
		HttpDelete delete = new HttpDelete(Constant.IMAGE_URL + key);
		delete.setHeader("Authorization", authMessage);
		try {
			HttpResponse response = client.execute(delete);
			if (response.getStatusLine().getStatusCode() == 204) {
				return true;
			} else {
				return false;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

}

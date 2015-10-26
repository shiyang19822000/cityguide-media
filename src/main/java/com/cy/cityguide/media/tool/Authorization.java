package com.cy.cityguide.media.tool;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.HmacUtils;

import com.cy.cityguide.media.constant.Constant;

public class Authorization {
	/**
	 * @param contentType
	 *            上传类型:image/jpeg.text/plain
	 * @param key
	 *            上传的路径
	 * @return authorization 签名串
	 */
	public static String getAuthorizationMessage(String requestMethod, String contentType, String key) {
		String CanonicalizedResource = "/" + Constant.BUCKET_NAME + "/" + key;
		String stringToSign = requestMethod + "\n" + "" + "\n" + contentType + "\n" + "" + "\n" + ""
				+ CanonicalizedResource;
		String signature = "";
		try {
			signature = Base64.encodeBase64String(
					HmacUtils.hmacSha1(Constant.PRIVATE_KEY.getBytes("utf8"), stringToSign.getBytes("utf8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String authorization = Constant.AUTHMESSAGE_PREFIX + signature;
		return authorization;
	}
}

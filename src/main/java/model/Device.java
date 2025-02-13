package model;

import java.util.Date;

public class Device {
	// フィールド ------------------------------------------------------------------
	private String deviceId;
	private String password;
	private String accessToken;
	private Date accessTokenLimit;
	private String refreshToken;
	private Date refreshTokenLimit;
	private String owner;
	private Date updateDateTime;
	
	// コンストラクタ ----------------------------------------------------------------
	public Device() {
	}
	
	public Device(String deviceId, String password, 
			String accessToken, Date accessTokenLimit,
			String refreshToken, Date refreshTokenLimit,
			String owner,
			Date updateDateTime) {
		this.setDeviceId(deviceId);
		this.setPassword(password);
		this.setAccessToken(accessToken);
		this.setAccessTokenLimit(accessTokenLimit);
		this.setRefreshToken(refreshToken);
		this.setRefreshTokenLimit(refreshTokenLimit);
		this.setOwner(owner);
		this.setUpdateDateTime(updateDateTime);
	}

	// getter/setter ----------------------------------------------------------
	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public Date getAccessTokenLimit() {
		return accessTokenLimit;
	}

	public void setAccessTokenLimit(Date accessTokenLimit) {
		this.accessTokenLimit = accessTokenLimit;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public Date getRefreshTokenLimit() {
		return refreshTokenLimit;
	}

	public void setRefreshTokenLimit(Date refreshTokenLimit) {
		this.refreshTokenLimit = refreshTokenLimit;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Date getUpdateDateTime() {
		return updateDateTime;
	}

	public void setUpdateDateTime(Date updateDateTime) {
		this.updateDateTime = updateDateTime;
	}
}

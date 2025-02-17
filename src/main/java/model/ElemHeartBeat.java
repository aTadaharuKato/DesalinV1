package model;

import java.util.Date;

public final class ElemHeartBeat {
	private int heartbeat;
	private Date datetime;
	public ElemHeartBeat(int heartbeat, Date datetime) {
		this.heartbeat = heartbeat;
		this.datetime = datetime;
	}
	public int getHeartbeat() {
		return heartbeat;
	}
	public Date getDatetime() {
		return datetime;
	}
}
package model;

import java.util.Date;

public final class ElemTemperature {
	private double temperature;
	private Date datetime;
	public ElemTemperature(double temperature, Date datetime) {
		this.temperature = temperature;
		this.datetime = datetime;
	}
	
	public double getTemperature() {
		return temperature;
	}
	public Date getDatetime() {
		return datetime;
	}
}

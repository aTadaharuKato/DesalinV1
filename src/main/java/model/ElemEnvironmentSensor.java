package model;

import java.util.Date;

public class ElemEnvironmentSensor {
	private double temperature;
	private int humidity;
	private double pressure;
	private Date datetime;
	public ElemEnvironmentSensor(double temperature, int humidity, double pressure, Date datetime) {
		this.temperature = temperature;
		this.humidity = humidity;
		this.pressure = pressure;
		this.datetime = datetime;
	}
	
	public double getTemperature() {
		return temperature;
	}
	public int getHumidity() {
		return humidity;
	}
	public double getPressure() {
		return pressure;
	}
	public Date getDatetime() {
		return datetime;
	}
}

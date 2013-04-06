package at.ahls.light.data;

public class LightState {

	private boolean _on;
	// use the Integer-object. Things have to be null-able again.
	private Integer _hue;
	private Integer _brightness;
	private Integer _saturation;
	
	public LightState() {
		_on = false;
		_hue = null;
		_brightness = null;
		_saturation = null;
	}
	
	public LightState(boolean on, Integer hue, Integer brightness, Integer saturation) {
		_on = on;
		_hue = hue;
		_brightness = brightness;
		_saturation = saturation;
	}

	public boolean isOn() {
		return _on;
	}

	public void setOn(boolean on) {
		_on = on;
	}

	public Integer getHue() {
		return _hue;
	}

	public void setHue(Integer hue) {
		_hue = hue;
	}

	public Integer getBrightness() {
		return _brightness;
	}

	public void setBrightness(Integer brightness) {
		_brightness = brightness;
	}

	public Integer getSaturation() {
		return _saturation;
	}

	public void setSaturation(Integer saturation) {
		_saturation = saturation;
	}
	
	public String toJSON() {
		StringBuilder sb = new StringBuilder();
		
		/*
		 * {
		 * 	"hue": 5000,
		 * 	"on": true,
		 * 	"bri": 200
		 * }
		 * 
		 */
		
		sb.append("{\n");
			sb.append("\t\"on\": " + _on);
		if (_hue != null) {
			sb.append(",\n");
			sb.append("\t\"hue\": " + _hue);
		}
		if (_brightness != null) {
			sb.append(",\n");
			sb.append("\t\"bri\": " + _brightness);
		}
		if (_saturation != null) {
			sb.append(",\n");
			sb.append("\t\"sat\": " + _saturation + "\n");
		}
		sb.append("}\n");
		
		return sb.toString();
	}
}

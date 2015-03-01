package japicmp.test.serialversion.unchanged;

import java.io.Serializable;

public class SerialVersionUnchanged implements Serializable {
	private int intField = 0;

	public SerialVersionUnchanged(int intField) {
		this.intField = intField;
	}

	public int getIntField() {
		return intField;
	}

	public void setIntField(int intField) {
		this.intField = intField;
	}
}

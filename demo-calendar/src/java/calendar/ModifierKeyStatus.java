package calendar;

public class ModifierKeyStatus {
	public boolean ctrlKey;
	public boolean altKey;
	public boolean shiftKey;
	
	public void reset()
	{
		ctrlKey = false;
		altKey = false;
		shiftKey = false;

		return;
	}
}

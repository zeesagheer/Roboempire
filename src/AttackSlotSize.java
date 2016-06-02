
public class AttackSlotSize {
	private int flank;
	private int front;
	public int getFlank() {
		return flank;
	}
	public void setFlank(double flank) {
		this.flank = (int) Math.ceil(flank);
	}
	public int getFront() {
		return front;
	}
	public void setFront(double front) {
		this.front = (int) Math.ceil(front);
	}
}

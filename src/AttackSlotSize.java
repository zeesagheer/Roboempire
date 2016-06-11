
public class AttackSlotSize {
	private double flank;
	private double front;
	public int getFlank() {
		return (int) Math.ceil(flank);
	}
	public void setFlank(double flank) {
		this.flank = flank;
	}
	public int getFront() {
		return (int) Math.ceil(front);
	}
	public void setFront(double front) {
		this.front = front;
	}
}

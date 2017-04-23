/**
 * Disc objects hold information about the colour of a disc. An enum
 * holds these colours.
 */
public class Disc {
	public enum Colour {
		RED, YELLOW, PIKA, DOGE, GOOGLE, FACEBOOK,
		ANDROID, APPLE, BAT, IRON, SPIDER, SUPER
	}
	
	private Colour colour;
	
	/**
	 * Constructor for disc
	 * @param colour The colour of this disc.
	 */
	public Disc(Colour colour) {
		this.colour = colour;
	}
	
	/**
	 * Returns the colour of this object.
	 * @return The colour of this object.
	 */
	public Colour getColour() {
		return colour;
	}
	
	@Override
	/**
	 * Two Disc objects are equal iff they are the same colour.
	 */
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!(o instanceof Disc)) return false;
		
		Disc d = (Disc) o;

		return this.colour.equals(d.colour);
	}
}
	

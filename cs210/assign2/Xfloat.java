// Xfloat.java

/**
 * Abstract class Xfloat serves as a base class for implementing 
 * the "fat" floating point class.  Extensions must implement xadd 
 * and xmult.
 *
 * @author   Richard M. Salter
 */

public abstract class Xfloat {
  final static int SMASK = 0x80000000;
  final static int EMASK = 0x7f800000;
  final static int MMASK = 0x007fffff;
  final static int BMASK = 0x00800000;

  byte sign;
  byte exp;
  int man;
  
  /**
   * Constructs Xfloat(0.0)
   */
  public Xfloat(){this((float)0.0);}

  /**
   * Constructs an Xfloat out of the given sign, exponent and 
   * mantissa.
   */
  public Xfloat(byte sign, byte exp, int man)
    {this.sign = sign; this.exp = exp; this.man = man;}

  /**
   * Constructs an Xfloat equivalent to the given float
   */
  public Xfloat(float f) {
    int y = Float.floatToIntBits(f);
    this.sign = (byte)((y & SMASK) >>> 31);
    this.exp = (byte)((y & EMASK) >>> 23);
    this.man = (int)(y & MMASK);
  }

  /**
   * Abstract addition operator.
   * @return the <tt>Xfloat</tt> which is the sum of <tt>y</tt> and 
   * this <tt>Xfloat</tt>.
   */
  public abstract Xfloat xadd(Xfloat y);

  /**
   * Abstract multiplication operator.
   * @return the <tt>Xfloat</tt> which is the product of <tt>y</tt> and 
   * this <tt>Xfloat</tt>.
   */
  public abstract Xfloat xmult(Xfloat y);

  /**
   * Formats an output line for printing.  Should be overridden.
   * @return the empty String
   */
  public String printline() {return new String();}

  /**
   * Returns an elaborate representation of this Xfloat.
   * @return an elaborate representation of this Xfloat.
   */
  public String toString() {
    int SMASK = 1;
    int EMASK = 0x000000ff;
    return Integer.toHexString(toIntBits())+
      " ("+bitDisplay()+") s: "+
      Integer.toHexString(sign & SMASK)+" e: "+
      Integer.toHexString(exp & EMASK)+" m: "+
      Integer.toHexString(man);
  }

  /* Returns a string displaying the bit fields in this Xfloat.
   * Sign, exponent and mantissa fields are separated.
   * @return a binary <tt>String</tt> display of this 
   * <tt>Xfloat</tt>.
   */
  protected String bitDisplay() {
    return new String(toBinaryString(sign, 1) + " " +
		      toBinaryString(exp, 8) + " " +
		      toBinaryString(man, 23));
  }

  /**
   * Returns the equivalent floating point value.
   * @return the equivalent <tt>float</tt> value.
   */
  public float toFloat() {
    return Float.intBitsToFloat(toIntBits());
  }
  /**
   * Returns the bit representation of the equivalent float.
   * @return the <tt>int</tt> which has the same bits as the 
   * equivalent <tt>float</tt>.
   */
  public int toIntBits() {
    return (((int)sign << 31) & SMASK) | (((int)exp << 23) & EMASK) | man;
  }

  /**
   * @return a binary <tt>String</tt> of length <tt>len</tt> * representing the given <tt>val</tt>.
   */
  private String toBinaryString(int val, int len) {
    len = Math.min(len, 32);
    char[] ans = new char[len];
    int mask = 1;
    for (int i = 0; i < len; i++) {
      ans[len-(i+1)] = (char)(((val & mask) >>> i) + (int)'0');
      mask <<= 1;
    }
    return new String(ans);
  }
}
    


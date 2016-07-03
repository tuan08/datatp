package net.datatp.storage.batchdb ;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class Bytes {
  static Charset UTF8 = Charset.forName("UTF-8") ;

  public static final int SIZEOF_BOOLEAN = Byte.SIZE/Byte.SIZE;
  public static final int SIZEOF_BYTE = SIZEOF_BOOLEAN;
  public static final int SIZEOF_CHAR = Character.SIZE/Byte.SIZE;
  public static final int SIZEOF_DOUBLE = Double.SIZE/Byte.SIZE;
  public static final int SIZEOF_FLOAT = Float.SIZE/Byte.SIZE;
  public static final int SIZEOF_INT = Integer.SIZE/Byte.SIZE;
  public static final int SIZEOF_LONG = Long.SIZE/Byte.SIZE;
  public static final int SIZEOF_SHORT = Short.SIZE/Byte.SIZE;


  public static String toString(final byte [] b) {
    if (b == null) {
      return null;
    }
    return toString(b, 0, b.length);
  }

  public static String toString(final byte [] b, int off, int len) {
    if(b == null) {
      return null;
    }
    if(len == 0) {
      return "";
    }
    String result = new String(b, off, len, UTF8);
    return result;
  }

  public static String toStringBinary(final byte [] b) {
    return toStringBinary(b, 0, b.length);
  }

  public static String toStringBinary(final byte [] b, int off, int len) {
    StringBuilder result = new StringBuilder();
    try {
      String first = new String(b, off, len, "ISO-8859-1");
      for (int i = 0; i < first.length() ; ++i ) {
        int ch = first.charAt(i) & 0xFF;
        if ( (ch >= '0' && ch <= '9')
            || (ch >= 'A' && ch <= 'Z')
            || (ch >= 'a' && ch <= 'z')
            || ch == ','
            || ch == '_'
            || ch == '-'
            || ch == ':'
            || ch == ' '
            || ch == '<'
            || ch == '>'
            || ch == '='
            || ch == '/'
            || ch == '.') {
          result.append(first.charAt(i));
        } else {
          result.append(String.format("\\x%02X", ch));
        }
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return result.toString();
  }

  private static boolean isHexDigit(char c) {
    return
        (c >= 'A' && c <= 'F') ||
        (c >= '0' && c <= '9');
  }

  /**
   * Takes a ASCII digit in the range A-F0-9 and returns
   * the corresponding integer/ordinal value.
   * @param ch  The hex digit.
   * @return The converted hex value as a byte.
   */
  public static byte toBinaryFromHex(byte ch) {
    if ( ch >= 'A' && ch <= 'F' )
      return (byte) ((byte)10 + (byte) (ch - 'A'));
    // else
    return (byte) (ch - '0');
  }

  public static byte [] toBytesBinary(String in) {
    // this may be bigger than we need, but lets be safe.
    byte [] b = new byte[in.length()];
    int size = 0;
    for (int i = 0; i < in.length(); ++i) {
      char ch = in.charAt(i);
      if (ch == '\\') {
        // begin hex escape:
        char next = in.charAt(i+1);
        if (next != 'x') {
          // invalid escape sequence, ignore this one.
          b[size++] = (byte)ch;
          continue;
        }
        // ok, take next 2 hex digits.
        char hd1 = in.charAt(i+2);
        char hd2 = in.charAt(i+3);

        // they need to be A-F0-9:
        if ( ! isHexDigit(hd1) ||
            ! isHexDigit(hd2) ) {
          // bogus escape code, ignore:
          continue;
        }
        // turn hex ASCII digit -> frequency
        byte d = (byte) ((toBinaryFromHex((byte)hd1) << 4) + toBinaryFromHex((byte)hd2));

        b[size++] = d;
        i += 3; // skip 3
      } else {
        b[size++] = (byte) ch;
      }
    }
    // resize:
    byte [] b2 = new byte[size];
    System.arraycopy(b, 0, b2, 0, size);
    return b2;
  }

  public static byte[] toBytes(String s) {
    if (s == null) {
      throw new IllegalArgumentException("string cannot be null");
    }
    byte [] result = s.getBytes(UTF8);
    return result;
  }

  public static byte [] toBytes(final boolean b) {
    byte [] bb = new byte[1];
    bb[0] = b? (byte)-1: (byte)0;
    return bb;
  }

  public static boolean toBoolean(final byte [] b) {
    if (b == null || b.length > 1) {
      throw new IllegalArgumentException("Array is wrong size");
    }
    return b[0] != (byte)0;
  }

  public static byte[] toBytes(long val) {
    byte [] b = new byte[8];
    for(int i=7;i>0;i--) {
      b[i] = (byte)(val);
      val >>>= 8;
    }
    b[0] = (byte)(val);
    return b;
  }

  public static long toLong(byte[] bytes) {
    return toLong(bytes, 0);
  }

  public static long toLong(byte[] bytes, int offset) {
    return toLong(bytes, offset, SIZEOF_LONG);
  }

  public static long toLong(byte[] bytes, int offset, final int length) {
    if (bytes == null || length != SIZEOF_LONG ||
        (offset + length > bytes.length)) {
      return -1L;
    }
    long l = 0;
    for(int i = offset; i < (offset + length); i++) {
      l <<= 8;
      l ^= (long)bytes[i] & 0xFF;
    }
    return l;
  }

  public static int putLong(byte[] bytes, int offset, long val) {
    if (bytes == null || (bytes.length - offset < SIZEOF_LONG)) {
      return offset;
    }
    for(int i=offset+7;i>offset;i--) {
      bytes[i] = (byte)(val);
      val >>>= 8;
    }
    bytes[offset] = (byte)(val);
    return offset + SIZEOF_LONG;
  }

  public static float toFloat(byte [] bytes) {
    return toFloat(bytes, 0);
  }

  public static float toFloat(byte [] bytes, int offset) {
    int i = toInt(bytes, offset);
    return Float.intBitsToFloat(i);
  }

  public static int putFloat(byte [] bytes, int offset, float f) {
    int i = Float.floatToRawIntBits(f);
    return putInt(bytes, offset, i);
  }

  public static byte [] toBytes(final float f) {
    int i = Float.floatToRawIntBits(f);
    return Bytes.toBytes(i);
  }

  public static double toDouble(final byte [] bytes) {
    return toDouble(bytes, 0);
  }

  public static double toDouble(final byte [] bytes, final int offset) {
    long l = toLong(bytes, offset);
    return Double.longBitsToDouble(l);
  }

  public static int putDouble(byte [] bytes, int offset, double d) {
    long l = Double.doubleToLongBits(d);
    return putLong(bytes, offset, l);
  }

  public static byte [] toBytes(final double d) {
    // Encode it as a long
    long l = Double.doubleToRawLongBits(d);
    return Bytes.toBytes(l);
  }

  public static byte[] toBytes(int val) {
    byte [] b = new byte[4];
    for(int i = 3; i > 0; i--) {
      b[i] = (byte)(val);
      val >>>= 8;
    }
    b[0] = (byte)(val);
    return b;
  }

  public static int toInt(byte[] bytes) {
    return toInt(bytes, 0);
  }

  public static int toInt(byte[] bytes, int offset) {
    return toInt(bytes, offset, SIZEOF_INT);
  }

  public static int toInt(byte[] bytes, int offset, final int length) {
    if (bytes == null || length != SIZEOF_INT ||
        (offset + length > bytes.length)) {
      return -1;
    }
    int n = 0;
    for(int i = offset; i < (offset + length); i++) {
      n <<= 8;
      n ^= bytes[i] & 0xFF;
    }
    return n;
  }

  public static int putInt(byte[] bytes, int offset, int val) {
    if (bytes == null || (bytes.length - offset < SIZEOF_INT)) {
      return offset;
    }
    for(int i= offset+3; i > offset; i--) {
      bytes[i] = (byte)(val);
      val >>>= 8;
    }
    bytes[offset] = (byte)(val);
    return offset + SIZEOF_INT;
  }

  public static byte[] toBytes(short val) {
    byte[] b = new byte[SIZEOF_SHORT];
    b[1] = (byte)(val);
    val >>= 8;
    b[0] = (byte)(val);
    return b;
  }

  public static short toShort(byte[] bytes) {
    return toShort(bytes, 0);
  }

  public static short toShort(byte[] bytes, int offset) {
    return toShort(bytes, offset, SIZEOF_SHORT);
  }

  public static short toShort(byte[] bytes, int offset, final int length) {
    if (bytes == null || length != SIZEOF_SHORT ||
        (offset + length > bytes.length)) {
      return -1;
    }
    short n = 0;
    n ^= bytes[offset] & 0xFF;
    n <<= 8;
    n ^= bytes[offset+1] & 0xFF;
    return n;
  }

  public static int putShort(byte[] bytes, int offset, short val) {
    if (bytes == null || (bytes.length - offset < SIZEOF_SHORT)) {
      return offset;
    }
    bytes[offset+1] = (byte)(val);
    val >>= 8;
    bytes[offset] = (byte)(val);
    return offset + SIZEOF_SHORT;
  }

  public static byte[] toBytes(char val) {
    byte[] b = new byte[SIZEOF_CHAR];
    b[1] = (byte) (val);
    val >>= 8;
    b[0] = (byte) (val);
    return b;
  }

  public static char toChar(byte[] bytes) {
    return toChar(bytes, 0);
  }

  public static char toChar(byte[] bytes, int offset) {
    return toChar(bytes, offset, SIZEOF_CHAR);
  }

  public static char toChar(byte[] bytes, int offset, final int length) {
    if (bytes == null || length != SIZEOF_CHAR ||
        (offset + length > bytes.length)) {
      return (char)-1;
    }
    char n = 0;
    n ^= bytes[offset] & 0xFF;
    n <<= 8;
    n ^= bytes[offset + 1] & 0xFF;
    return n;
  }

  public static int putChar(byte[] bytes, int offset, char val) {
    if (bytes == null || (bytes.length - offset < SIZEOF_CHAR)) {
      return offset;
    }
    bytes[offset + 1] = (byte) (val);
    val >>= 8;
    bytes[offset] = (byte) (val);
    return offset + SIZEOF_CHAR;
  }

  public static byte[] toBytes(char[] val) {
    byte[] bytes = new byte[val.length * 2];
    putChars(bytes,0,val);
    return bytes;
  }

  public static char[] toChars(byte[] bytes) {
    return toChars(bytes, 0, bytes.length);
  }

  public static char[] toChars(byte[] bytes, int offset) {
    return toChars(bytes, offset, bytes.length-offset);
  }

  public static char[] toChars(byte[] bytes, int offset, final int length) {
    int max = offset + length;
    if (bytes == null || (max > bytes.length) || length %2 ==1) {
      return null;
    }

    char[] chars = new char[length / 2];
    for (int i = 0, j = offset; i < chars.length && j < max; i++, j += 2) {
      char c = 0;
      c ^= bytes[j] & 0xFF;
      c <<= 8;
      c ^= bytes[j + 1] & 0xFF;
      chars[i] = c;
    }
    return chars;
  }

  public static int putChars(byte[] bytes, int offset, char[] val) {
    int max = val.length * 2 + offset;
    if (bytes == null || (bytes.length < max)) {
      return offset;
    }
    for (int i=0,j=offset; i<val.length && j<max;i++, j+=2){
      char c = val[i];
      bytes[j + 1] = (byte) (c);
      bytes[j] = (byte) (c >>>8);
    }

    return offset + SIZEOF_CHAR;
  }


  public static int compareTo(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
    // Bring WritableComparator code local
    int end1 = s1 + l1;
    int end2 = s2 + l2;
    for (int i = s1, j = s2; i < end1 && j < end2; i++, j++) {
      int a = (b1[i] & 0xff);
      int b = (b2[j] & 0xff);
      if (a != b) {
        return a - b;
      }
    }
    return l1 - l2;
  }
}

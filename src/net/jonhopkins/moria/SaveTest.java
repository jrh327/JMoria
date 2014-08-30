package net.jonhopkins.moria;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class SaveTest {
	private static File fileptr;
	private static InputStream fis;
	private static OutputStream fos;
	private static byte[] bytes;
	private static int b_ptr;
	private static byte xor_byte;
	
	private static final int SIZEOF_BYTE = 1;
	private static final int SIZEOF_CHAR = 2;
	private static final int SIZEOF_INT  = 4;
	private static final int SIZEOF_LONG = 8;
	
	private static boolean sv_write() {
		try {
			fos = new BufferedOutputStream(new FileOutputStream(""));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		
		b_ptr = 0;
		bytes = new byte[512];
		
		// write some stuff into `bytes`
		
		try {
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	private static boolean sv_read() {
		try {
			fis = new BufferedInputStream(new FileInputStream(""));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		
		/*try {
			bytes = fis.readAllBytes();
			
			// read from `bytes`
			
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		try {
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;	/* not reached, unless on mac */
	}
	
	private static void wr_byte(byte c) {
		if (b_ptr >= bytes.length) {
			bytes = Arrays.copyOf(bytes, bytes.length * 2);
		}
		xor_byte ^= c;
		bytes[b_ptr] = xor_byte;
		b_ptr++;
	}
	
	private static void wr_char(char c) {
		for (int i = 0; i < SIZEOF_CHAR; i++) {
			wr_byte((byte)(c >> (8 * i)));
		}
	}
	
	private static void wr_int(int s) {
		for (int i = 0; i < SIZEOF_INT; i++) {
			wr_byte((byte)(s >> (8 * i)));
		}
	}
	
	private static void wr_long(long l) {
		for (int i = 0; i < SIZEOF_LONG; i++) {
			wr_byte((byte)(l >> (8 * i)));
		}
	}
	
	private static void wr_bytes(byte[] c, int count) {
		for (int i = 0; i < count; i++) {
			wr_byte(c[i]);
		}
	}
	
	private static void wr_string(String str, int len) {
		char[] c = str.toCharArray();
		int i;
		
		for (i = 0; i < c.length; i++) {
			if (i == len) {
				break;
			}
			wr_char(c[i]);
		}
		for (; i < len; i++) {
			wr_char('\0');
		}
	}
	
	private static void wr_ints(int[] s, int count) {
		int i;
		
		for (i = 0; i < s.length; i++) {
			if (i == count) {
				break;
			}
			wr_int(s[i]);
		}
		for (; i < count; i++) {
			wr_int(0);
		}
	}
	
	private static byte getNextByte() {
		if (b_ptr >= bytes.length) {
			return 0;
		}
		byte b = bytes[b_ptr];
		b_ptr++;
		return b;
	}
	
	private static byte rd_byte() {
		byte c, ptr;
		
		c = getNextByte();
		ptr = (byte)((c ^ xor_byte) & 0xFF);
		xor_byte = c;
		
		return ptr;
	}
	
	private static char rd_char() {
		char c = 0;
		byte b = 0;
		
		for (int i = 0; i < SIZEOF_CHAR; i++) {
			c |= (rd_byte() & 0xFF) << (8 * i);
		}
		
		return c;
	}
	
	private static int rd_int() {
		int c, s;
		s = 0;
		c = 0;
		
		for (int i = 0; i < SIZEOF_INT; i++) {
			s |= (rd_byte() & 0xFF) << (8 * i);
		}
		
		return s;
	}
	
	private static long rd_long() {
		long l = 0;
		int c = 0;
		
		for (int i = 0; i < SIZEOF_LONG; i++) {
			l |= (rd_byte() & 0xFF) << (8 * i);
		}
		
		return l;
	}
	
	private static void rd_bytes(byte[] ch_ptr, int count) {
		int i;
		if (count > ch_ptr.length) {
			count = ch_ptr.length;
		}
		for (i = 0; i < ch_ptr.length; i++) {
			ch_ptr[i] = rd_byte();
		}
		for (; i < count; i++) {
			rd_byte();
		}
	}
	
	private static String rd_string(int len) {
		char[] str = new char[len];
		int i;
		
		for (i = 0; i < len; i++) {
			char c = rd_char();
			if (c == '\0') {
				break;
			}
			str[i] = c;
		}
		if (i < len) {
			// truncate empty characters
			str = Arrays.copyOf(str, i);
		}
		for (; i < len; i++) {
			rd_char();
		}
		
		return new String(str);
	}
	
	private static void rd_ints(int[] ptr, int count) {
		int i;
		
		for (i = 0; i < ptr.length; i++) {
			if (i == count) {
				break;
			}
			ptr[i] = rd_int();
		}
		for (; i < count; i++) {
			rd_int();
		}
	}
	
	public static void main(String[] args) {
		b_ptr = 0;
		bytes = new byte[512];
		xor_byte = 0;
		
		write_test();
		
		b_ptr = 0;
		xor_byte = 0;
		
		read_test();
	}
	
	private static void write_test() {
		byte bytetest = 8;
		int inttest = 52136;
		int[] intarrtest = new int[] { 1, 2, 3, 4, 24, 4 };
		long longtest = 52136;
		
		wr_byte(bytetest);
		wr_int(inttest);
		wr_ints(intarrtest, 10);
		wr_long(longtest);
		wr_string("This is a test", 20);
	}
	private static void read_test() {
		byte bytetest = rd_byte();
		int inttest = rd_int();
		int[] intarrtest = new int[6]; rd_ints(intarrtest, 10);
		long longtest = rd_long();
		String strtest = rd_string(20);
		
		System.out.println("rd_byte = " + bytetest);
		System.out.println("rd_int = " + inttest);
		System.out.println("rd_ints = " + toString(intarrtest));
		System.out.println("rd_long = " + longtest);
		System.out.println("'" + strtest + "'");
	}
	
	private static String toString(int[] arr) {
		String str = "";
		for (int i = 0; i < arr.length; i++) {
			str += arr[i] + " ";
		}
		return str;
	}
}
/*
 * Rnd.java: random number generator
 * 
 * Copyright (C) 1989-2008 James E. Wilson, Robert A. Koeneke, 
 *                         David J. Grabiner
 * 
 * This file is part of Umoria.
 * 
 * Umoria is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Umoria is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with Umoria.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.jonhopkins.moria;

public class Rnd {
	/* This alg uses a prime modulus multiplicative congruential generator
	   (PMMLCG), also known as a Lehmer Grammer, which satisfies the following
	   properties
	
	   (i)	 modulus: m - a large prime integer
	   (ii)	 multiplier: a - an integer in the range 2, 3, ..., m - 1
	   (iii) z[n+1] = f(z[n]), for n = 1, 2, ...
	   (iv)	 f(z) = az mod m
	   (v)	 u[n] = z[n] / m, for n = 1, 2, ...

	   The sequence of z's must be initialized by choosing an initial seed
	   z[1] from the range 1, 2, ..., m - 1.  The sequence of z's is a pseudo-
	   random sequence drawn without replacement from the set 1, 2, ..., m - 1.
	   The u's form a psuedo-random sequence of real numbers between (but not
	   including) 0 and 1.

	   Schrage's method is used to compute the sequence of z's.
	   Let m = aq + r, where q = m div a, and r = m mod a.
	   Then f(z) = az mod m = az - m * (az div m) =
		     = gamma(z) + m * delta(z)
	   Where gamma(z) = a(z mod q) - r(z div q)
	   and	 delta(z) = (z div q) - (az div m)
	
	   If r < q, then for all z in 1, 2, ..., m - 1:
	   (1) delta(z) is either 0 or 1
	   (2) both a(z mod q) and r(z div q) are in 0, 1, ..., m - 1
	   (3) absolute value of gamma(z) <= m - 1
	   (4) delta(z) = 1 iff gamma(z) < 0
	
	   Hence each value of z can be computed exactly without overflow as long
	   as m can be represented as an integer.
	 */
	
	/* a good random number generator, correct on any machine with 32 bit
	   integers, this algorithm is from:
	
	Stephen K. Park and Keith W. Miller, "Random Number Generators:
		Good ones are hard to find", Communications of the ACM, October 1988,
		vol 31, number 10, pp. 1192-1201.
	
	   If this algorithm is implemented correctly, then if z[1] = 1, then
	   z[10001] will equal 1043618065
	
	   Has a full period of 2^31 - 1.
	   Returns integers in the range 1 to 2^31-1.
	 */
	
	private static final long RNG_M = 2147483647; // m = 2^31 - 1
	private static final long RNG_A = 16807;
	private static final long RNG_Q = 127773;     // m div a
	private static final long RNG_R = 2836;       // m mod a
	
	// 32 bit seed
	private static long rndSeed;
	
	private Rnd() { }
	
	public static long getRandomSeed() {
		return rndSeed;
	}
	
	public static void setRandomSeed(long seedval) {
		/* set seed to value between 1 and m-1 */
		rndSeed = (seedval % (RNG_M - 1)) + 1;
	}
	
	/**
	 * Returns a pseudo-random number from set 1, 2, ..., RNG_M - 1
	 * 
	 * @return A random number
	 */
	public static long randomNumber() {
		long high = rndSeed / RNG_Q;
		long low = rndSeed % RNG_Q;
		long test = RNG_A * low - RNG_R * high;
		
		if (test > 0) {
			rndSeed = test;
		} else {
			rndSeed = test + RNG_M;
		}
		return rndSeed;
	}
	
	/**
	 * Gets a new random seed for the random number generator
	 * 
	 * @param seed - Used to seed the RNG
	 */
	public static void initSeeds(long seed) {
		long clock_var;
		
		if (seed == 0) {
			clock_var = System.currentTimeMillis();
		} else {
			clock_var = seed;
		}
		
		Variable.randesSeed = clock_var;
		
		clock_var += 8762;
		Variable.townSeed = clock_var;
		
		clock_var += 113452L;
		setRandomSeed(clock_var);
		
		// make it a little more random
		for (clock_var = randomInt(100); clock_var != 0; clock_var--) {
			randomNumber();
		}
	}
	
	// holds the previous rnd state
	private static long oldSeed;
	
	/**
	 * Change to different random number generator state
	 * 
	 * @param seed - Used to seed the RNG
	 */
	public static void setSeed(long seed) {
		oldSeed = getRandomSeed();
		
		// want reproducible state here
		setRandomSeed(seed);
	}
	
	/**
	 * Restore the normal random generator state
	 */
	public static void resetSeed() {
		setRandomSeed(oldSeed);
	}
	
	/**
	 * Generates a random integer x where 1<=X<=MAXVAL -RAK-
	 * 
	 * @param maxval - The maximum value to be returned
	 * @return A random integer x where 1 <= x <= maxval
	 */
	public static int randomInt(int maxval) {
		long randval = randomNumber();
		return (int)(randval % maxval) + 1;
	}
	
	/**
	 * Generates a random integer number of NORMAL distribution -RAK-
	 * 
	 * @param mean
	 * @param stand 
	 * @return 
	 */
	public static int randomIntNormalized(int mean, int stand) {
		int tmp = randomInt(Constants.MAX_SHORT);
		
		// off scale, assign random value between 4 and 5 times SD
		if (tmp == Constants.MAX_SHORT) {
			int offset = 4 * stand + randomInt(stand);
			
			// one half are negative
			if (randomInt(2) == 1) {
				offset = -offset;
			}
			
			return mean + offset;
		}
		
		// binary search normal normal_table to get index that matches tmp
		// this takes up to 8 iterations
		int low = 0;
		int index = Constants.NORMAL_TABLE_SIZE >> 1;
		int high = Constants.NORMAL_TABLE_SIZE;
		
		while (true) {
			if ((Tables.normalTable[index] == tmp) || (high == (low + 1))) {
				break;
			}
			
			if (Tables.normalTable[index] > tmp) {
				high = index;
				index = low + ((index - low) >> 1);
			} else {
				low = index;
				index += ((high - index) >> 1);
			}
		}
		
		// might end up one below target, check that here
		if (Tables.normalTable[index] < tmp) {
			++index;
		}
		
		// normal_table is based on SD of 64, so adjust the index value here,
		// round the half way case up
		int offset = ((stand * index) + (Constants.NORMAL_TABLE_SD >> 1))
				/ Constants.NORMAL_TABLE_SD;
		
		// one half should be negative
		if (randomInt(2) == 1) {
			offset = -offset;
		}
		
		return mean + offset;
	}
	
	public static void test() {
		setRandomSeed(0L);
		
		for (int i = 1; i < 10000; i++) {
			randomNumber ();
		}
		
		long random = randomNumber();
		System.out.printf("z[10001] = %d, should be 1043618065\n", random);
		if (random == 1043618065L) {
			System.out.println("success!!!\n");
		}
	}
}

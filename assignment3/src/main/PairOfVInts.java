/*
 * Cloud9: A Hadoop toolkit for working with big data
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */



import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.io.WritableUtils;
//import org.apache.hadoop.io.WritableComparable;
//import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.io.WritableComparable;

/**
 * WritableComparable representing a pair of VInts. The elements in the pair
 * are referred to as the left and right elements. The natural sort order is:
 * first by the left element, and then by the right element.
 * 
 * @author Kent Wills
 *  */
public class PairOfVInts implements WritableComparable<PairOfVInts> {
	private int leftElement, rightElement;

	/**
	 * Creates a pair.
	 */
	public PairOfVInts() {}

	/**
	 * Creates a pair.
	 *
	 * @param left the left element
	 * @param right the right element
	 */
	public PairOfVInts(int left, int right) {
		set(left, right);
	}
	
	public void readFields(DataInput in) throws IOException {
		leftElement =WritableUtils.readVInt(in);
		rightElement = WritableUtils.readVInt(in);
	}

	
	public void write(DataOutput out) throws IOException {
		 WritableUtils.writeVInt(out, leftElement);
		 WritableUtils.writeVInt(out, rightElement);
	}

	/**
	 * Returns the key (left element).
	 *
	 * @return the LeftElement
	 */
	public int getLeftElement() {
		return leftElement;			
	}

	/**
	 * Returns the RightElement
	 *
	 * @return the value
	 */
	public int getRightElement() {
		return rightElement;			
	}

	/**
	 * Sets the right and left elements of this pair.
	 *
	 * @param left the left element
	 * @param right the right element
	 */
	public void set(int left, int right) {
		leftElement = left;
		rightElement = right;	
	}

	/**
	 * Checks two pairs for equality.
	 *
	 * @param obj object for comparison
	 * @return <code>true</code> if <code>obj</code> is equal to this object, <code>false</code> otherwise
	 */
	public boolean equals(Object obj) {
		PairOfVInts pair = (PairOfVInts) obj;
		return getLeftElement() == pair.getLeftElement() && getRightElement()  == pair.getRightElement();
	}

	/**
	 * Defines a natural sort order for pairs. Pairs are sorted first by the
	 * left element, and then by the right element.
	 *
	 * @return a value less than zero, a value greater than zero, or zero if
	 *         this pair should be sorted before, sorted after, or is equal to
	 *         <code>obj</code>.
	 */
	public int compareTo(PairOfVInts pair) {
		int pl = pair.getLeftElement();
		int pr = pair.getRightElement();

		if (getLeftElement() == pl) {
			if (getRightElement() < pr)
				return -1;
			if (getRightElement() > pr)
				return 1;
			return 0;
		}

		if (getLeftElement() < pl)
			return -1;

		return 1;
	}

	/**
	 * Returns a hash code value for the pair.
	 *
	 * @return hash code for the pair
	 */
	public int hashCode() {
		return getLeftElement() + getRightElement();
	}

	/**
	 * Generates human-readable String representation of this pair.
	 *
	 * @return human-readable String representation of this pair
	 */
	public String toString() {
		return "(" + getLeftElement() + ", " +  getRightElement() + ")";
	}

	/**
	 * Clones this object.
	 *
	 * @return clone of this object
	 */
	public PairOfVInts clone() {
		return new PairOfVInts(this.leftElement, this.rightElement);
	}

	/** Comparator optimized for <code>PairOfVInts</code>.**/ 
	public static class Comparator extends WritableComparator {

		/**
		 * Creates a new Comparator optimized for <code>PairOfInts</code>.
		 */
		public Comparator() {
			super(PairOfVInts.class);
		}

		/**
		 * Optimization hook.
		 */
		public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
			int thisLeftValue = readInt(b1, s1);
			int thatLeftValue = readInt(b2, s2);

			if (thisLeftValue == thatLeftValue) {
				int thisRightValue = readInt(b1, s1 + 4);
				int thatRightValue = readInt(b2, s2 + 4);

				return (thisRightValue < thatRightValue ? -1
						: (thisRightValue == thatRightValue ? 0 : 1));
			}

			return (thisLeftValue < thatLeftValue ? -1 : (thisLeftValue == thatLeftValue ? 0 : 1));
		}
	}

	static { // register this comparator
		WritableComparator.define(PairOfVInts.class, new Comparator());
	}



}

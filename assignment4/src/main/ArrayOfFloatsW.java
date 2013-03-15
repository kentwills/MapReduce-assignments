
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;

/**
 * An array of floats that implements Writable class.
 * 
 * @author Ferhan Ture
 */
public class ArrayOfFloatsW implements WritableComparable<ArrayOfFloatsW> {
	float[] array;

	/**
	 * Constructor with no arguments.
	 */
	public ArrayOfFloatsW() {		
		super();				
	}

	/**
	 * Constructor take in a one-dimensional array.
	 * 
	 * @param array
	 *            input float array
	 */
	public ArrayOfFloatsW(float[] array) {
		this.array=new float[array.length];
		this.array = array;
	}

	/**
	 * Constructor that takes the size of the array as an argument.
	 * 
	 * @param size
	 *            number of floats in array
	 */
	public ArrayOfFloatsW(int size) {
		super();
		array = new float[size];
	}

	public void readFields(DataInput in) throws IOException {
		int size = in.readInt();
		array = new float[size];
		for (int i = 0; i < size; i++) {
			set(i, in.readFloat());
		}
	}

	public void write(DataOutput out) throws IOException {
		out.writeInt(size());
		for (int i = 0; i < size(); i++) {
			out.writeFloat(get(i));
		}
	}
	
	/**
	 * Get a deep copy of the array.
	 * 
	 * @return a clone of the array
	 */
	public float[] getClone() {
		return array.clone();
	}

	/**
	 * Get a shallow copy of the array.
	 * 
	 * @return a pointer to the array
	 */
	public float[] getArray() {
		return array;
	}

	/**
	 * Set the array.
	 * 
	 * @param array
	 */
	public void setArray(float[] array) {
		this.array=new float[array.length];
		this.array = array;
	}

	/**
	 * Returns the float value at position <i>i</i>.
	 * 
	 * @param i
	 *            index of float to be returned
	 * @return float value at position <i>i</i>
	 */
	public float get(int i) {
		return array[i];
	}

	/**
	 * Sets the float at position <i>i</i> to <i>f</i>.
	 * 
	 * @param i
	 *            position in array
	 * @param f
	 *            float value to be set
	 */
	public void set(int i, float f) {
		array[i] = f;
	}

	/**
	 * Returns the size of the float array.
	 * 
	 * @return size of array
	 */
	public int size() {
		return array.length;
	}

	public String toString() {
		String s = "[";
		for (int i = 0; i < size(); i++) {
			s += get(i) + ",";
		}
		s += "]";
		return s;
	}

	@Override
	public int compareTo(ArrayOfFloatsW arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
}

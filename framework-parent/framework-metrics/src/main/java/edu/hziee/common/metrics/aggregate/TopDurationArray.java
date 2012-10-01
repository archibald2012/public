/**
 * 
 */
package edu.hziee.common.metrics.aggregate;

/**
 * For holding the top list of durations in the aggregation.
 * 
 * @author Administrator
 * 
 */
public class TopDurationArray {
	private final int			arraySize;
	private int						count	= 0;
	private final long[]	dataArray;

	public TopDurationArray(int size) {
		dataArray = new long[size];
		arraySize = size;
	}

	public boolean check(long duration) {
		// check if the first one
		if (count == 0) {
			dataArray[0] = duration;
			count++;
			return true;
		} else {
			// check duration in array
			for (int i = 0; i < count; i++) {
				if (duration > dataArray[i]) {
					// insert duration into the array
					int start = (count == arraySize) ? arraySize - 1 : count;
					for (int j = start; j > i; j--) {
						dataArray[j] = dataArray[j - 1];
					}
					dataArray[i] = duration;
					if (count < arraySize) {
						count++;
					}
					return true;
				}
			}
		}
		return false;
	}
}

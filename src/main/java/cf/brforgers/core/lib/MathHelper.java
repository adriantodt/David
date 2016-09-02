package cf.brforgers.core.lib;

public class MathHelper {
	public static int roundToPowerOf2(int value) {
		value--;
		value |= value >> 1;
		value |= value >> 2;
		value |= value >> 4;
		value |= value >> 8;
		return value + 1;
	}

	public static int getNextPowerOfTwo(int value) {
		return value << 1;
	}

	public static int previousPowerOfTwo(int value) {
		return value >> 1;
	}

	public static long roundToPowerOf2(long value) {
		value--;
		value |= value >> 1;
		value |= value >> 2;
		value |= value >> 4;
		value |= value >> 8;
		return value + 1;
	}

	public static long getNextPowerOfTwo(long value) {
		return value << 1;
	}

	public static long previousPowerOfTwo(long value) {
		return value >> 1;
	}
}

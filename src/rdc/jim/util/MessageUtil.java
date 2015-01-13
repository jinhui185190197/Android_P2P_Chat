package rdc.jim.util;

/**
 * 处理消息编码 添加1个字节表示消息类型
 * 
 * @author 橘子哥
 *
 */

public class MessageUtil {

	public static final String TAG = "MessageUtil";
	public static final int MESSAGE_TYPE_STR = 0;
	public static final int MESSAGE_TYPE_FILE = 1;

	/**
	 * 对消息进行编码
	 * 
	 * @param messageType
	 * @param buffer
	 * @return
	 */
	public static byte[] encodeByte(int messageType, byte[] buffer) {
		byte typeB;
		byte[] lenghtB = intToBytes2(buffer.length);
		byte[] msgBuffer = new byte[5 + buffer.length];
		if (messageType == MESSAGE_TYPE_STR) {
			typeB = 0;
		} else {
			typeB = 1;
		}
		msgBuffer[0] = typeB;
		for (int j = 1; j <= 4; j++) {
			msgBuffer[j] = lenghtB[j - 1];
		}
		for (int i = 5; i < msgBuffer.length; i++) {
			msgBuffer[i] = buffer[i - 5];
		}

		return msgBuffer;

	}

	/**
	 * 将整形转换成字节 （存储文件大小）
	 * 
	 * @param n
	 * @return
	 */
	public static byte[] intToBytes2(int n) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (n >> (24 - i * 8));
		}
		return b;
	}

	/**
	 * 字节数组转换成整形（不支持负数）
	 * 
	 * @param bytes
	 * @return 返回-1时失败
	 */
	public static int byteToInt(byte[] b) {
		if (b != null) {
			int mask = 0xff;
			int temp = 0;
			int n = 0;
			for (int i = 0; i < 4; i++) {
				n <<= 8;
				temp = b[i] & mask;
				n |= temp;
			}
			return n;

		}
		return -1;
	}

	/**
	 * 分割字节数组
	 * 
	 * @param bytes
	 * @param offSet
	 * @param lenght
	 * @return
	 */
	public static byte[] slieBytes(byte[] bytes, int offSet, int lenght) {
		if (offSet >= 0 && lenght >= 0) {
			byte[] result = new byte[lenght];
			for (int i = 0; i < lenght; i++) {
				result[i] = bytes[offSet + i];

			}
			return result;
		}

		return null;

	}

	/**
	 * 拼接字节数组
	 * 
	 * @param result
	 * @param offSet
	 * @param lenght
	 * @param src
	 */
	public static void ConBytes(byte[] result, int tail, int offSet,
			int lenght, byte[] src) {

		for (int i = 0; i < lenght; i++) {
			result[tail+i] = src[i+offSet];
		}
	}

}

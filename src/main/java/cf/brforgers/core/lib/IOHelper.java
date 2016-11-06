package cf.brforgers.core.lib;

import cf.adriantodt.David.commands.utils.Statistics;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * IO Helping classes
 *
 * @author AdrianTodt
 */
public class IOHelper {
	/**
	 * Get the content from a InputStream and outputs a String
	 *
	 * @param stream the InputStream going to be used
	 * @return Content from InputStream
	 */
	public static String toString(InputStream stream) {
		if (stream == null) return null;
		try {
			return IOUtils.toString(stream, "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get the Content from a File and outputs a String
	 *
	 * @param file the File going to be read
	 * @return Content from File
	 */
	public static String toString(File file) {
		return toString(toStream(file));
	}

	public static InputStream toStream(File file) {
		try {
			return new FileInputStream(file);
		} catch (Exception e) {
			return null;
		}
	}

	public static InputStream toStream(URL url) {
		Statistics.wgets++;
		try {
			URLConnection c = url.openConnection();
			c.setRequestProperty("User-Agent", System.getProperty("java.version"));
			c.connect();
			return c.getInputStream();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get the Content from a URL and outputs a String
	 *
	 * @param url the URL to Get Content to
	 * @return Content from the URL
	 */
	public static String toString(URL url) {
		return toString(toStream(url));
	}

	public static String toString(String url) {
		return toString(newURL(url));
	}

	public static URL newURL(String url) {
		try {
			return new URL(url);
		} catch (Exception e) {
			return null;
		}
	}
}

package base;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.StringTokenizer;

import compatibility.Log;

public class Utility {
	private static ThreadLocal<StringBuilder> LocalStringBuilder = new ThreadLocal<StringBuilder>() {
		@Override
		protected StringBuilder initialValue() {
			return new StringBuilder(64);
		}

		@Override
		public StringBuilder get() {
			StringBuilder b = super.get();
			b.setLength(0);
			return b;
		}
	};

	/**
	 * Reuses a thread-specific StringBuilder instance to make a string out of
	 * the input arguments. Only the final returned string will invoke a memory
	 * allocation.
	 * 
	 * @param args
	 *            arguments of any type
	 * @return
	 */
	public static String toString(Object... args) {
		StringBuilder builder = LocalStringBuilder.get();
		for (int i = 0; i < args.length; i++)
			builder.append(args[i]);
		return builder.toString();
	}

	/**
	 * Returns a thread-specific StringBuilder instance to reuse.
	 * 
	 * @return a thread-specific StringBuilder instance
	 */
	public static StringBuilder getStringBuilder() {
		return LocalStringBuilder.get();
	}
	
	public static <T> String join(T[] params) {
		return join(params, ",");
	}
	
	public static <T> String join(T[] params, String delimiter) {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < params.length; i++)
			builder.append(params[i]).append(i == params.length - 1 ? "" : delimiter);
		
		return builder.toString();
	}

	public static void browse(String url) throws IOException {
		try {
			int delimiter = url.indexOf('?') + 1;
	        String address = url.substring(0, delimiter);
	        String query = url.substring(delimiter);
	        StringTokenizer tokens = new StringTokenizer(query, "&");
	        while(tokens.hasMoreTokens()) {
	        	String param = tokens.nextToken();
	        	int splitter = param.indexOf("=") + 1;
	        	String name = param.substring(0, splitter);
	        	String value = param.substring(splitter);
	        	value = value.replace('+', ' ');
	        	value = value.replace("%7C", "|");
	        	value = value.replace("%26", "&");
	        	value = value.replace("%28", "(");
	        	value = value.replace("%29", ")");
	        	value = value.replace("%2C", ",");
	        	value = value.replace("%2F", "/");
	        	value = value.replace("%3A", ":");
	        	address += name + URLEncoder.encode(value, "UTF8") + "&";
	        }
			
	        Method m = Class.forName("com.googlecode.charts4j.UrlUtil").getDeclaredMethod("normalize", String.class);
	        Object desktop = Class.forName("java.awt.Desktop").getDeclaredMethod("getDesktop").invoke(null);
			desktop.getClass().getDeclaredMethod("browse", URI.class).invoke(desktop, URI.create(m.invoke(null, address).toString()));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public static int shell(String cmd) {
        try {
            int exitVal;
            Runtime rt = Runtime.getRuntime();
            Process process = rt.exec(cmd);
            System.out.println("shell: " + cmd);
            exitVal = process.waitFor();
            System.out.println("returns " + exitVal);
            return exitVal;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }
	
    /**
     * Print the MD5 (hash value) of a byte array.
     * 
     * @param message
     */
    public static void printMD5(byte[] bytes) {
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(bytes);

            byte messageDigest[] = algorithm.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            }
            // String foo = messageDigest.toString();
            Log.d("utility", "md5 version is: %s", hexString.toString());
        } catch (NoSuchAlgorithmException nsae) {
            Log.d("No such hash algorithm.");
        }
    }

    public static void clearDirectory(File dir) {
	if (!dir.isDirectory())
	    return;
	File[] files = dir.listFiles();
	if (files == null)
	    return;
	for (int i = 0; i < files.length; i++)
	    files[i].delete();
    }
}

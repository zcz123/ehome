package cc.wulian.smarthomev5.collect;

import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Provides static methods for creating mutable {@code Maps} instances easily.
 */
public class Maps
{
	/**
	 * Creates a {@code HashMap} instance.
	 * 
	 * @return a newly-created, initially-empty {@code HashMap}
	 */
	public static <K, V> HashMap<K, V> newHashMap(){
		return new HashMap<K, V>();
	}

	/**
	 * Creates a {@code TreeMap} instance.
	 * 
	 * @return a newly-created, initially-empty {@code TreeMap}
	 */
	public static <K, V> TreeMap<K, V> newTreeMap(){
		return new TreeMap<K, V>();
	}

	/**
	 * Creates a {@code TreeMap} instance with {@code Comparator}
	 * 
	 * @return a newly-created, initially-empty {@code TreeMap} with {@code Comparator}
	 */
	public static <K, V> TreeMap<K, V> newTreeMap( Comparator<? super K> comparator ){
		return new TreeMap<K, V>(comparator);
	}
}

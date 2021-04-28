package rebue.wheel.core;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 按put放入顺序排列的Properties
 * 
 * @author Unmi
 * 2012-12-07
 */
public class OrderedProperties extends Properties {

	private static final long serialVersionUID = -4627607243846121965L;

	private final LinkedHashMap<Object, Object> map = new LinkedHashMap<Object, Object>();

	@Override
	public Enumeration<Object> keys() {
		return Collections.<Object> enumeration(map.keySet());
	}

	@Override
	public Object put(Object key, Object value) {
		map.put(key, value);
		return super.put(key, value);
	}

	@Override
	public Set<Object> keySet() {
		return map.keySet();
	}

	@Override
	public Set<String> stringPropertyNames() {
		Set<String> set = new LinkedHashSet<String>();

		for (Object key : this.map.keySet()) {
			set.add((String) key);
		}

		return set;
	}

	@Override
	public Set<Map.Entry<Object, Object>> entrySet() {
		return map.entrySet();
	}
}
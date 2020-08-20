package com.autocoding.level;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

class DemoLevelObject implements ILevelObject {

	private Integer id;
	private Integer pid;
	private int level;

	/**
	 * @param id
	 * @param pid
	 */
	public DemoLevelObject(Integer id, Integer pid) {
		this.id = id;
		this.pid = pid;
	}

	@Override
	public Serializable getId() {
		return this.id;
	}

	@Override
	public Serializable getPId() {

		return this.pid;
	}

	@Override
	public int getLevel() {
		return this.level;
	}

	@Override
	public void setLevel(int level) {
		this.level = level;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DemoLevelObject other = (DemoLevelObject) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public static Map<Serializable, ILevelObject> map = new HashMap<Serializable, ILevelObject>();
	static {
		DemoLevelObject demoLevelObject1 = new DemoLevelObject(1, null);
		DemoLevelObject demoLevelObject11 = new DemoLevelObject(11, 1);
		DemoLevelObject demoLevelObject111 = new DemoLevelObject(111, 11);
		map.put(demoLevelObject1.getId(), demoLevelObject1);
		map.put(demoLevelObject11.getId(), demoLevelObject11);
		map.put(demoLevelObject111.getId(), demoLevelObject111);
	}

}

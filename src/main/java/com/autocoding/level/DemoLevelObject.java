package com.autocoding.level;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * ILevelObject的一个示例
 * 
 * @ClassName: DemoLevelObject
 * @Description: 用一句话描述该文件做什么
 * @author: QiaoLi
 * @date: Aug 20, 2020 4:31:59 PM
 */
class DemoLevelObject extends AbstractLevelObject<DemoLevelObject> {

	public DemoLevelObject(Integer id, Integer pid) {
		super(id, pid);
	}

	public static Map<Serializable, DemoLevelObject> map = new HashMap<Serializable, DemoLevelObject>();
	static {
		DemoLevelObject demoLevelObject1 = new DemoLevelObject(1, null);
		DemoLevelObject demoLevelObject11 = new DemoLevelObject(11, 1);
		DemoLevelObject demoLevelObject111 = new DemoLevelObject(111, 11);
		map.put(demoLevelObject1.getId(), demoLevelObject1);
		map.put(demoLevelObject11.getId(), demoLevelObject11);
		map.put(demoLevelObject111.getId(), demoLevelObject111);
	}

}

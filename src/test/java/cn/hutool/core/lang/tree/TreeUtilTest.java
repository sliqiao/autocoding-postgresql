package cn.hutool.core.lang.tree;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.List;

import org.junit.Test;

import com.alibaba.fastjson.JSON;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TreeUtilTest {

	@Test
	public void test() throws ParseException, InterruptedException {
		final TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
		treeNodeConfig.setIdKey("id");
		treeNodeConfig.setParentIdKey("parentId");
		treeNodeConfig.setChildrenKey("children");
		treeNodeConfig.setDeep(2);
		final List<City> initCityList = this.initCityList();
		final List<Tree<Integer>> list = TreeUtil.build(initCityList, 0, treeNodeConfig,
				(city, tree) -> {
					// 也可以使用 tree.setId(object.getId());等一些默认值
					final Field[] fields = ReflectUtil.getFieldsDirectly(city.getClass(), true);
					for (final Field field : fields) {
						final String fieldName = field.getName();
						final Object fieldValue = ReflectUtil.getFieldValue(city, field);
						tree.putExtra(fieldName, fieldValue);
					}
					//如果不想全部字段可以按照下面的写法添加
					/*tree.putExtra("id", object.getId());
			    tree.putExtra("parentId", object.getParentId());
			    tree.putExtra("cityName", object.getCityName());*/
				});
		System.out.println(JSON.toJSON(list));

	}

	private List<City> initCityList() {
		final List<City> cities = CollUtil.newArrayList();
		cities.add(new City(1, "广东省", 0));
		cities.add(new City(2, "广州市", 1));
		cities.add(new City(3, "南沙区", 2));
		cities.add(new City(4, "万顷沙镇", 3));
		cities.add(new City(5, "黄阁镇", 3));
		cities.add(new City(6, "湖南省", 0));
		cities.add(new City(7, "长沙市", 6));
		cities.add(new City(8, "芙蓉区", 7));
		return cities;
	}

	@Getter
	@Setter
	private static class City {
		private final Integer id;
		private final String cityName;
		private final Integer parentId;
		private List<City> children;

		public City(Integer id, String cityName, Integer parentId) {
			this.id = id;
			this.cityName = cityName;
			this.parentId = parentId;
		}
	}

}

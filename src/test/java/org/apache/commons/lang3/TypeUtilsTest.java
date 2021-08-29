package org.apache.commons.lang3;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.junit.Assert;
import org.junit.Test;

public class TypeUtilsTest {
	@Test
	public void testGetTypeArguments() {
		Map<TypeVariable<?>, Type> typeVarAssigns;
		TypeVariable<?> treeSetTypeVar;
		Type typeArg;

		typeVarAssigns = TypeUtils.getTypeArguments(Integer.class, Comparable.class);
		treeSetTypeVar = Comparable.class.getTypeParameters()[0];
		Assert.assertTrue("Type var assigns for Comparable from Integer: " + typeVarAssigns,
				typeVarAssigns.containsKey(treeSetTypeVar));
		typeArg = typeVarAssigns.get(treeSetTypeVar);
		Assert.assertEquals("Type argument of Comparable from Integer: " + typeArg, Integer.class,
				typeVarAssigns.get(treeSetTypeVar));

		typeVarAssigns = TypeUtils.getTypeArguments(int.class, Comparable.class);
		treeSetTypeVar = Comparable.class.getTypeParameters()[0];
		Assert.assertTrue("Type var assigns for Comparable from int: " + typeVarAssigns,
				typeVarAssigns.containsKey(treeSetTypeVar));
		typeArg = typeVarAssigns.get(treeSetTypeVar);
		Assert.assertEquals("Type argument of Comparable from int: " + typeArg, Integer.class,
				typeVarAssigns.get(treeSetTypeVar));

		final Collection<Integer> col = Arrays.asList(new Integer[0]);
		typeVarAssigns = TypeUtils.getTypeArguments(List.class, Collection.class);
		treeSetTypeVar = Comparable.class.getTypeParameters()[0];
		Assert.assertFalse("Type var assigns for Collection from List: " + typeVarAssigns,
				typeVarAssigns.containsKey(treeSetTypeVar));

	}

}

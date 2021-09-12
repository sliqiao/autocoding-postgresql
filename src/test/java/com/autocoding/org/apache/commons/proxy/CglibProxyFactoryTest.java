package com.autocoding.org.apache.commons.proxy;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.proxy.Interceptor;
import org.apache.commons.proxy.Invocation;
import org.apache.commons.proxy.ProxyFactory;
import org.apache.commons.proxy.factory.cglib.CglibProxyFactory;
import org.apache.commons.proxy.provider.BeanProvider;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CglibProxyFactoryTest {

	@Test
	public void test() {
		final ProxyFactory proxyFactory = new CglibProxyFactory();
		final Object target = new BeanProvider(User.class).getObject();
		final Class<?>[] proxyClasses = CglibProxyFactoryTest.proxyClasses();
		if (!proxyFactory.canProxy(proxyClasses)) {
			throw new RuntimeException("请选择合适的代理工厂进行代理，该工厂无法生成代理对象");
		}
		final User userProxy = (User) proxyFactory.createInterceptorProxy(target,
				new MyInterceptor(), proxyClasses);
		userProxy.print();
	}

	private static Class<?>[] proxyClasses() {
		final List<Class<?>> proxyClasses = new LinkedList<>();
		proxyClasses.add(User.class);
		return proxyClasses.toArray(new Class[0]);
	}

	public static class User {
		public void print() {
			System.out.println("调用user.print()方法");
		}

	}

	private static class MyInterceptor implements Interceptor {

		@Override
		public Object intercept(Invocation invocation) throws Throwable {

			try {
				return invocation.proceed();
			} catch (final Exception e) {
				CglibProxyFactoryTest.log.error("异常逻辑处理", e);
			} finally {
				CglibProxyFactoryTest.log.info("后置逻辑处理");
			}
			return null;
		}

	}

}

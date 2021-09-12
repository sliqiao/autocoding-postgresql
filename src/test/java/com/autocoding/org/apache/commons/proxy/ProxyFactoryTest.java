package com.autocoding.org.apache.commons.proxy;

import org.apache.commons.proxy.Interceptor;
import org.apache.commons.proxy.Invocation;
import org.apache.commons.proxy.ProxyFactory;
import org.apache.commons.proxy.ProxyUtils;
import org.apache.commons.proxy.provider.BeanProvider;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProxyFactoryTest {

	@Test
	public void test() {
		final ProxyFactory proxyFactory = new ProxyFactory();
		final Object target = new BeanProvider(User.class).getObject();
		final Class<?>[] proxyClasses = ProxyUtils.getAllInterfaces(User.class);
		if (!proxyFactory.canProxy(proxyClasses)) {
			throw new RuntimeException("请选择合适的代理工厂进行代理，该工厂无法生成代理对象");
		}
		final IUser userProxy = (IUser) proxyFactory.createInterceptorProxy(target,
				new MyInterceptor(), proxyClasses);
		userProxy.print();
	}

	public static class User implements IUser {
		@Override
		public void print() {
			System.out.println("调用user.print()方法");
		}

	}

	public static interface IUser {
		void print();

	}

	private static class MyInterceptor implements Interceptor {

		@Override
		public Object intercept(Invocation invocation) throws Throwable {

			try {
				return invocation.proceed();
			} catch (final Exception e) {
				ProxyFactoryTest.log.error("异常逻辑处理", e);
			} finally {
				ProxyFactoryTest.log.info("后置逻辑处理");
			}
			return null;
		}

	}

}

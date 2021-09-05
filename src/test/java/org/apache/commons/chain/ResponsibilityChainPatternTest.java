package org.apache.commons.chain;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.Filter;
import org.apache.commons.chain.impl.ChainBase;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.commons.collections.MapUtils;
import org.junit.Test;

import com.github.rholder.retry.BlockStrategy;
import com.github.rholder.retry.BlockStrategyTest.MyBlockStrategy;

import lombok.extern.slf4j.Slf4j;

/**
 * 模拟一个场景：订单支付成功之后，需要一系列的后续处理，为了方便后面扩展，可以方便的增加后续的处理步骤，现使用责任链模式进行设计，
 * <p>
 * 优势：
 * 1、命令的提交与命令的执行进行解耦，非常方便扩展，增加新的命令，处理一个请示，对代码进行扩展
 * 缺点：
 * 1、功能设计增加了一定的复杂度
 * 2、每个命令都需求设计一 个新的类Command
 * </p>
 * @author Administrator
 *
 */
@Slf4j
public class ResponsibilityChainPatternTest {

	@Test
	public void test() throws Exception {
		ResponsibilityChainPatternTest.log.info("责任链开始运行...");
		final ChainBase doOrderChain = new ChainBase();
		//step 1 :更新订单表信息
		doOrderChain.addCommand(new UpdateOrderInfoCommand());
		//step 2 :发送预订大表
		doOrderChain.addCommand(new SendBookingBigTableCommand());
		//step 3 :发送申请单大表
		doOrderChain.addCommand(new SendApplyBillingBigTableCommand());
		//step 4 :后置处理器
		doOrderChain.addCommand(new PostFilter());
		final Context context = new ContextBase();
		context.put("orderNo", "123");
		doOrderChain.execute(context);
		ResponsibilityChainPatternTest.log.info("责任链结束运行...");
	}

	@Slf4j
	//更新订单表信息
	private static class UpdateOrderInfoCommand implements Command {

		@Override
		public boolean execute(Context context) throws Exception {
			final String orderNo = MapUtils.getString(context, "orderNo");
			UpdateOrderInfoCommand.log.info("开始:更新订单信息,orderNo:{}", orderNo);
			final BlockStrategy blockStrategy = new MyBlockStrategy();
			blockStrategy.block(1000 * 10);
			UpdateOrderInfoCommand.log.info("结束:更新订单信息,orderNo:{}", orderNo);
			return false;
		}

	}

	//发送预订大表命令
	@Slf4j
	private static class SendBookingBigTableCommand implements Command {

		@Override
		public boolean execute(Context context) throws Exception {
			final String orderNo = MapUtils.getString(context, "orderNo");
			SendBookingBigTableCommand.log.info("开始:发送预订大表信息,orderNo:{}", orderNo);
			final BlockStrategy blockStrategy = new MyBlockStrategy();
			blockStrategy.block(1000 * 10);
			SendBookingBigTableCommand.log.info("结束:发送预订大表信息,orderNo:{}", orderNo);
			return false;
		}

	}

	//发送申请单大表命令
	@Slf4j
	private static class SendApplyBillingBigTableCommand implements Command {

		@Override
		public boolean execute(Context context) throws Exception {
			final String orderNo = MapUtils.getString(context, "orderNo");
			SendBookingBigTableCommand.log.info("开始:发送申请单大表信息,orderNo:{}", orderNo);
			final BlockStrategy blockStrategy = new MyBlockStrategy();
			blockStrategy.block(1000 * 10);
			SendBookingBigTableCommand.log.info("结束:发送申请单大表信息,orderNo:{}", orderNo);
			return false;
		}

	}

	//进行后置处理Fitler
	@Slf4j
	private static class PostFilter implements Filter {

		@Override
		public boolean execute(Context context) throws Exception {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean postprocess(Context context, Exception exception) {
			final String orderNo = MapUtils.getString(context, "orderNo");
			SendBookingBigTableCommand.log.info("开始:进行后置处理,orderNo:{}", orderNo);
			final BlockStrategy blockStrategy = new MyBlockStrategy();
			try {
				blockStrategy.block(1000 * 10);
			} catch (final InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SendBookingBigTableCommand.log.info("结束:进行后置处理,orderNo:{}", orderNo);
			return false;
		}

	}
}

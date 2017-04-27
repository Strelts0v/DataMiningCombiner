package evm.dmc.core.arithmetic;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import evm.dmc.core.AbstractFramework;
import evm.dmc.core.FrameworkContext;
import evm.dmc.core.InContextExecutable;
import evm.dmc.core.data.Data;
import evm.dmc.core.data.IntegerData;
import evm.dmc.core.function.DMCFunction;

@Service
@ArithmeticFW
public class ArithmeticFramework extends AbstractFramework {
	@Autowired
	@ArithmeticFWContext
	private FrameworkContext frameworkContext;

	public ArithmeticFramework() {
		super(AbstractArithmeticFunction.class);
	}

	@Override
	@PostConstruct
	public void initFramework() {
		super.initFrameworkForType(AbstractArithmeticFunction.class);
	}

	/**
	 * @return the frameworkContext
	 */
	public FrameworkContext getFrameworkContext() {
		return frameworkContext;
	}

	@Override
	public DMCFunction getDMCFunction(String descriptor) {
		DMCFunction function = super.getDMCFunction(descriptor);
		((InContextExecutable) function).setContext(frameworkContext);
		return function;
	}

	@Override
	public Data getData(Number num) {
		IntegerData data = (IntegerData) instantiateData();
		data.setData((Integer) num);
		return data;
	}

	@Override
	public Data getData(Data otherFormat) {
		IntegerData data = (IntegerData) instantiateData();
		Class<? extends Number> cls = data.getData().getClass();
		data.setData((Integer) cls.cast(otherFormat.getData()));
		return null;
	}

	private Data instantiateData() {
		return super.instantiateData(IntegerData.class);
	}

}

package evm.dmc.core.arithmetic;

import org.springframework.stereotype.Component;

import evm.dmc.core.data.Data;
import evm.dmc.core.data.IntegerData;

/**
 * @author id23cat
 * Offers biargument Multiplication function
 */
@Component
public class MulFunction extends AbstractArithmeticFunction<Integer> {

	public MulFunction() {
		super();
		super.setName("Mul function");
		super.setArgsCount(2);
		
		// The most important setting
		super.setFunction(this::mul);
	}
	
	/**
	 * Execute multiplication: {@code (a * b) / context}
	 * @param a
	 * @param b
	 * @return 
	 */	
	public Integer mul(Integer a, Integer b){
		return a * b;
	}
	
	public IntegerData mul(Data<Integer> a, Data<Integer> b) {
		return new IntegerData( mul(a.getData(), b.getData()) );
	}	

}

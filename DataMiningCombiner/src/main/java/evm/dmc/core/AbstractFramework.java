package evm.dmc.core;

import javax.annotation.PostConstruct;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import evm.dmc.core.data.Data;
import evm.dmc.core.function.DMCFunction;

/**
 * Each final extender must implement initFramework method based on example:
 * 
 * <pre>
 * <code> 
 * &#64;Override
 * &#64;PostConstruct
 * public void initFramework() {
 * 		super.initFrameworkForType(AbstractPythonFunction.class);
 * }
 * </code>
 * </pre>
 * 
 * TODO Hard wired to Spring Have to be changed with consideration to:
 * 
 * @see https://spring.io/blog/2004/08/06/method-injection/
 * @see http://docs.spring.io/spring/docs/current/spring-framework-reference/html/beans.html#beans-java-method-injection
 * @see http://docs.spring.io/spring/docs/current/spring-framework-reference/html/beans.html#beans-factory-method-injection
 */
public abstract class AbstractFramework implements Framework, DataFactory {

	private ApplicationContext applicationContext;
	private Set<String> funcIDs = new HashSet<String>();
	// private Map<String, String> functionsMap = new ConcurrentHashMap<>();
	private Class abstractFunctionClass;

	public AbstractFramework() {
	}

	public AbstractFramework(Class abstractFunctionClass) {
		initFrameworkForType(abstractFunctionClass);

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// TODO
		this.applicationContext = applicationContext;
		initFramework();
	}

	protected abstract Class getFunctionClass();

	@PostConstruct
	protected void postInit() {
		Class cls = getFunctionClass();
		initFrameworkForType(cls);
	}

	/**
	 * Initialize the framework for a function type. This type will be used as
	 * main class type for instantiating functions by name in
	 * {@link #getDMCFunction(String)}
	 *
	 * @param functionsClass
	 *            the functions class
	 */
	public void initFrameworkForType(Class functionsClass) {
		// TODO
		funcIDs.addAll(Arrays.asList(applicationContext.getBeanNamesForType(functionsClass)));

		// for (String name : funcIDs) {
		//
		// }

	}

	@Override
	public Set<String> getFunctionDescriptors() {
		return funcIDs;
	}

	@Override
	public DMCFunction getDMCFunction(String descriptor) {
		// TODO
		@SuppressWarnings({ "unchecked", "rawtypes" })
		DMCFunction function = (DMCFunction) applicationContext.getBean(descriptor, abstractFunctionClass);
		return function;
	}

	@Override
	public Data getData(Class type) {
		return this.instantiateData(type);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Data instantiateData(Class dataType) {
		return (Data) applicationContext.getBean(dataType);
	}

}
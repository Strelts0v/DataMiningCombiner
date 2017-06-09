package evm.dmc.core;

import evm.dmc.core.function.DMCFunction;

/**
 * The Interface Algorithm.
 */
public interface Algorithm {
	
	/**
	 * Adds the command to the chain of algorithm
	 *
	 * @param dMCFunction the command
	 */
	void addCommand(DMCFunction dMCFunction);
	
	/**
	 * Deletes command from chain of algorithm
	 *
	 * @param dMCFunction the command
	 */
	
	void delCommand(DMCFunction dMCFunction);
	
	/**
	 * Execute chain of commands.
	 */
	void execute();

}
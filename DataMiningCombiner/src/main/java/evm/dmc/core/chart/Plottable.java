package evm.dmc.core.chart;

/**
 * Interface allows implementations to be plotted on chart
 * 
 * @author id23cat
 *
 */
public interface Plottable {
	default double[] plot(int index) {
		return null;
	}

	default int getElementsCount() {
		return -1;
	}

	default String getTitle(int index) {
		return null;
	}

	default void setAttributesToPlot(int... indexes) {

	}

	default int[] getAttributesToPlot() {
		return null;
	}
}

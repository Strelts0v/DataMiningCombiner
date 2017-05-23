package evm.dmc.weka.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

import evm.dmc.core.DataFactory;
import evm.dmc.core.chart.Plottable;
import evm.dmc.core.data.Data;
import evm.dmc.core.data.HasMultiAttributes;
import evm.dmc.core.data.InMemoryData;
import evm.dmc.core.data.MultyInstace;
import evm.dmc.weka.WekaFW;
import evm.dmc.weka.exceptions.DataOperationException;
import evm.dmc.weka.exceptions.IndexOutOfRange;
import weka.core.Attribute;
import weka.core.Instances;

/**
 * Supported loading from all supported by weka files.
 * Now supports saving only * to SCV files Default separator
 * 
 * @see WEKA CSCSaver documentation
 *      http://weka.sourceforge.net/doc.dev/weka/core/converters/CSVSaver.html
 * 
 * @author id23cat
 */
@Service("Weka_Instances")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
// TODO: too complicated class !!!
public class WekaData extends InMemoryData<Instances>
		implements Cloneable, HasMultiAttributes, MultyInstace, Plottable {

	private DataFactory dataFactory;

	// used for generating cross-validation sets
	private static int FOLDS = 4;

	public WekaData(@Autowired @WekaFW DataFactory df) {
		this.dataFactory = df;
	}

	public double[] getAllValuesAsDoubleAt(int index) {
		Instances inst = super.getData();
		// // if (!inst.attribute(index).isNumeric())
		// // return null;
		// int length = inst.numInstances();
		// double[] values = new double[length];
		// for (int i = 0; i < length; i++) {
		// values[i] = inst.instance(i).value(index);
		// }
		double[] values = inst.attributeToDoubleArray(index);
		return values;

	}

	public double[] getAllValuesAsDoubleAt(String attrName) {
		return this.getAllValuesAsDoubleAt(super.getData().attribute(attrName).index());
	}

	@Override
	public Data<Instances> getAttribute(int index) {
		WekaData data;
		try {
			data = this.copyObject();
		} catch (CloneNotSupportedException e) {
			throw new DataOperationException(e);
		}

		Instances inst = data.getData();
		Attribute attrSaveFromDelete = inst.attribute(index);
		int attrsCount = inst.numAttributes();
		int curIndex = 0;
		for (int i = 0; i < attrsCount; i++) {
			if (!inst.attribute(curIndex).equals(attrSaveFromDelete))
				inst.deleteAttributeAt(curIndex);
			else
				curIndex++;
		}

		return data;
	}

	@Override
	public Data<Instances> getAttributes(int... indexes) throws IndexOutOfRange {
		WekaData data;
		try {
			data = this.copyObject();
		} catch (CloneNotSupportedException e) {
			throw new DataOperationException(e);
		}

		Instances inst = data.getData();
		Set<Attribute> attrSaveFromDelSet = new HashSet<>();

		try {
			for (int index : indexes) {
				Attribute attrSaveFromDelete = inst.attribute(index);
				attrSaveFromDelSet.add(attrSaveFromDelete);
			}
		} catch (Exception exc) {
			throw new IndexOutOfRange(exc);
		}

		int attrsCount = inst.numAttributes();
		int curIndex = 0;
		for (int i = 0; i < attrsCount; i++) {
			if (!attrSaveFromDelSet.contains(inst.attribute(curIndex)))
				inst.deleteAttributeAt(curIndex);
			else
				curIndex++;
		}

		return data;
	}

	@Override
	public boolean isMultiAttribute() {
		return true;
	}

	@Override
	public double[] plot() {
		// TODO
		return this.getAllValuesAsDoubleAt("Account length");
	}

	@Override
	public String title() {
		// TODO
		return "Account length";
	}

	@Override
	public WekaData getInstance(int index) throws IndexOutOfRange {
		WekaData newData = (WekaData) dataFactory.getData(WekaData.class);
		try {
			newData.data = new Instances(this.data, index, 1);
		} catch (IllegalArgumentException exc) {
			throw new IndexOutOfRange(exc);
		}
		return newData;
	}

	@Override
	public WekaData getSubset(int from, int to) throws IndexOutOfRange {
		WekaData newData = (WekaData) dataFactory.getData(WekaData.class);
		try {
			newData.data = new Instances(this.data, from, to - from + 1);
		} catch (IllegalArgumentException exc) {
			throw new IndexOutOfRange(exc);
		}
		return newData;
	}

	@Override
	public int getInstancesCount() {
		return data.numInstances();
	}

	@Override
	public int getAttributesCount() {
		return data.numAttributes();
	}

	@Override
	public Data<?>[] getTrainTest(int numFold) {
		if (numFold > 3)
			numFold = 3;
		if (numFold < 0)
			numFold = 0;

		// int seed = 514;
		// Random rand = new Random(seed);
		WekaData train = (WekaData) dataFactory.getData(WekaData.class);
		WekaData test = (WekaData) dataFactory.getData(WekaData.class);

		train.setData(this.data.trainCV(FOLDS, numFold));
		test.setData(this.data.testCV(FOLDS, numFold));

		WekaData[] traintest = new WekaData[] { train, test };
		return traintest;
	}

	@Override
	public WekaData clone() {
		WekaData newData = (WekaData) dataFactory.getData(WekaData.class);
		newData.data = new Instances(this.data);
		return newData;

	}

	@Override
	public String getValueAsString(int row, int column) throws IndexOutOfRange {
		if (!checkRowIndex(row) || !checkColIndex(column)) {
			throw new IndexOutOfRange(exceptionMessage("Wrong index: ", row, column));
		}
		Attribute attr = this.data.get(row).attribute(column);
		if (attr.isNominal() || attr.isString() || attr.isDate() || attr.isRelationValued())
			return this.data.get(row).stringValue(column);
		return String.valueOf(this.data.get(row).value(column));
	}

	@Override
	public double getValue(int row, int column) throws IndexOutOfRange {
		if (!checkRowIndex(row) || !checkColIndex(column)) {
			throw new IndexOutOfRange(exceptionMessage("Wrong index: ", row, column));
		}
		return this.data.get(row).value(column);
	}

	@Override
	public boolean isNominal(int column) throws IndexOutOfRange {
		if (!checkColIndex(column)) {
			throw new IndexOutOfRange(exceptionMessage("Wrong index: ", column));
		}
		return this.data.attribute(column).isNominal() || this.data.attribute(column).isString();
	}

	@Override
	public boolean isString(int column) throws IndexOutOfRange {
		if (!checkColIndex(column)) {
			throw new IndexOutOfRange(exceptionMessage("Wrong index: ", column));
		}
		return this.data.attribute(column).isString();
	}

	@Override
	public boolean isDate(int column) throws IndexOutOfRange {
		if (!checkColIndex(column)) {
			throw new IndexOutOfRange(exceptionMessage("Wrong index: ", column));
		}
		return this.data.attribute(column).isDate();
	}

	@Override
	public boolean isNumeric(int column) throws IndexOutOfRange {
		if (!checkColIndex(column)) {
			throw new IndexOutOfRange(exceptionMessage("Wrong index: ", column));
		}
		return this.data.attribute(column).isNumeric();
	}

	public WekaData copyObject() throws CloneNotSupportedException {
		WekaData data = (WekaData) dataFactory.getData(WekaData.class);
		data.setData(new Instances(this.getData()));
		return data;

	}

	private boolean checkRowIndex(int index) throws IndexOutOfRange {
		if (index < 0 || index >= getInstancesCount())
			return false;
		return true;
	}

	private boolean checkColIndex(int index) throws IndexOutOfRange {
		if (index < 0 || index >= getAttributesCount())
			return false;
		return true;
	}

	private String exceptionMessage(String msg, int... idx) {
		StringBuilder strb = new StringBuilder(msg);
		int i = 0;
		while (i < idx.length) {
			strb.append(idx[i]);
			if (i < idx.length - 1)
				strb.append(", ");
			i++;
		}

		return strb.toString();

	}

}

package equipments.Fridge.measures;

import fr.sorbonne_u.components.hem2023e3.utils.CompoundMeasure;
import fr.sorbonne_u.components.hem2023e3.utils.Measure;
import fr.sorbonne_u.components.hem2023e3.utils.MeasureI;
import fr.sorbonne_u.components.hem2023e3.utils.MeasurementUnit;
import fr.sorbonne_u.exceptions.PreconditionException;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>AirConditionningCompoundMeasure</code> implements the set of
 * measures to be sent when calling the Fridge logical sensors.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2024-10-11</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public class FridgeCompoundMeasure 
extends CompoundMeasure
implements FridgeMesureI {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	protected static final int	TARGET_TEMPERATURE_INDEX = 0;
	protected static final int	CURRENT_TEMPERATURE_INDEX = 1;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a Fridge compound measure from the two measures.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code targetTemperature.getData() == getTargetTemperature()}
	 * pre	{@code targetTemperature.getMeasurementUnit() == getTargetTemperatureMeasurementUnit()}
	 * pre	{@code currentTemperature.getData() == getCurrentTemperature()}
	 * pre	{@code currentTemperature.getMeasurementUnit() == getCurrentTemperatureMeasurementUnit()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param targetTemperature		the measure of the target temperature.
	 * @param currentTemperature	the measure of the current temperature.
	 */
	public FridgeCompoundMeasure(
			Measure<Double> targetTemperature,
			Measure<Double> currentTemperature
			)
	{
		super(new MeasureI[]{targetTemperature, currentTemperature});

		assert	targetTemperature.getData() == this.getTargetTemperature() :
			new PreconditionException(
					"targetTemperature.getData() == "
							+ "getCurrentTemperature()");
		assert	targetTemperature.getMeasurementUnit() ==
				this.getTargetTemperatureMeasurementUnit() :
					new PreconditionException(
							"targetTemperature.getMeasurementUnit() == "
									+ "getTargetTemperatureMeasurementUnit()");
		assert	currentTemperature.getData() == this.getCurrentTemperature() :
			new PreconditionException(
					"currentTemperature.getData() == "
							+ "getCurrentTemperature()");
		assert	currentTemperature.getMeasurementUnit() ==
				this.getCurrentTemperatureMeasurementUnit() :
					new PreconditionException(
							"currentTemperature.getMeasurementUnit() == "
									+ "getCurrentTemperatureMeasurementUnit()");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.hem2023e3.equipments.heater.measures.HeaterMeasureI#isTemperatureMeasures()
	 */
	@Override
	public boolean isTemperatureMeasures() {
		return true;
	}

	/***********************************************************************************/
	@SuppressWarnings("unchecked")
	public double getTargetTemperature() {
		return ((Measure<Double>)
				this.getMeasure(TARGET_TEMPERATURE_INDEX)).getData();
	}

	/***********************************************************************************/
	@SuppressWarnings("unchecked")
	public MeasurementUnit getTargetTemperatureMeasurementUnit() {
		return ((Measure<Double>)
				this.getMeasure(TARGET_TEMPERATURE_INDEX)).
				getMeasurementUnit();
	}

	/***********************************************************************************/
	@SuppressWarnings("unchecked")
	public double getCurrentTemperature() {
		return ((Measure<Double>)
				this.getMeasure(CURRENT_TEMPERATURE_INDEX)).getData();
	}

	@SuppressWarnings("unchecked")
	public MeasurementUnit	getCurrentTemperatureMeasurementUnit() {
		return ((Measure<Double>)
				this.getMeasure(CURRENT_TEMPERATURE_INDEX)).
				getMeasurementUnit();
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/


package equipments.AirConditioning.measures;

import utils.Measure;
import utils.MeasurementUnit;
import fr.sorbonne_u.exceptions.PreconditionException;
import utils.CompoundMeasure;
import utils.MeasureI;

/**
 * 
 * @author walte
 * 
 * The class <code>AirConditioningCompoundMeasure</code> implements the set of
 * measures to be sent when calling the air conditioning logical sensors.
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
 */
public class 	AirConditioningCompoundMeasure
extends 	CompoundMeasure
implements 	AirConditioningMeasureI
{
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
	 * create an air conditioning compound measure from the two measures.
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
	public			AirConditioningCompoundMeasure(
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
	 * @see equipments.AirConditioning.measures.AirConditioningMeasureI#isTemperatureMeasures()
	 */
	@Override
	public boolean		isTemperatureMeasures()
	{
		return true;
	}

	@SuppressWarnings("unchecked")
	public double		getTargetTemperature()
	{
		return ((Measure<Double>)
						this.getMeasure(TARGET_TEMPERATURE_INDEX)).getData();
	}

	@SuppressWarnings("unchecked")
	public MeasurementUnit	getTargetTemperatureMeasurementUnit()
	{
		return ((Measure<Double>)
						this.getMeasure(TARGET_TEMPERATURE_INDEX)).
														getMeasurementUnit();
	}

	@SuppressWarnings("unchecked")
	public double		getCurrentTemperature()
	{
		return ((Measure<Double>)
						this.getMeasure(CURRENT_TEMPERATURE_INDEX)).getData();
	}

	@SuppressWarnings("unchecked")
	public MeasurementUnit	getCurrentTemperatureMeasurementUnit()
	{
		return ((Measure<Double>)
						this.getMeasure(CURRENT_TEMPERATURE_INDEX)).
														getMeasurementUnit();
	}
}

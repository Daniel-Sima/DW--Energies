package equipments.AirConditioning.measures;

import utils.MeasureI;

public interface AirConditioningMeasureI
extends MeasureI 
{
	default boolean		isStateMeasure()		{ return false; }
	default boolean		isTemperatureMeasures()	{ return false; }
}

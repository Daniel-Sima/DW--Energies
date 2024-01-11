package equipments.AirConditioning.measures;

import equipments.AirConditioning.AirConditioning.AirConditioningState;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import utils.Measure;

public class 	AirConditioningStateMeasure 
extends 	Measure<AirConditioningState> 
implements 	AirConditioningMeasureI {

	private static final long serialVersionUID = 1L;

	public			AirConditioningStateMeasure(
		AcceleratedClock ac,
		AirConditioningState data
		)
	{
		super(ac, data);			
	}

	public			AirConditioningStateMeasure(AirConditioningState data)
	{
		super(data);
	}

	/**
	 * @see equipments.AirConditioning.measures.AirConditioningMeasureI#isStateMeasure()
	 */
	@Override
	public boolean	isStateMeasure()	{ return true; }

}

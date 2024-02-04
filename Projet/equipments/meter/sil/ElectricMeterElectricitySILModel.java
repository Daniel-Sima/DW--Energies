package equipments.meter.sil;

import java.util.concurrent.TimeUnit;
import equipments.meter.mil.ElectricMeterElectricityModel;
import utils.Measure;
import utils.MeasurementUnit;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;

// -----------------------------------------------------------------------------
/**
 * The class <code>ElectricMeterElectricitySILModel</code> defines the SIL
 * simulation model for the electric meter electricity consumption.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * Compared to the MIL model, this model uses the reference to the
 * {@code ElectricMeter} passed as a run time simulation parameter to set
 * the total power consumption in the corresponding variable in the
 * component. Hence, the sensor method in the component will just have
 * to return the latest value stored in the variable.
 * </p>
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
 * <p>Created on : 2023-11-18</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			ElectricMeterElectricitySILModel
extends		ElectricMeterElectricityModel
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** URI for an instance model in SIL simulations; works as long as
	 *  only one instance is created.										*/
	public static final String	SIL_URI = ElectricMeterElectricityModel.class.
													getSimpleName() + "-SIL";
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an <code>ElectricMeterElectricitySILModel</code> instance.
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition
	 * post	{@code true}	// no more postcondition
	 * </pre>
	 *
	 * @param uri				URI of the model.
	 * @param simulatedTimeUnit	time unit used for the simulation time.
	 * @param simulationEngine	simulation engine to which the model is attached.
	 * @throws Exception		<i>to do</i>.
	 */
	public				ElectricMeterElectricitySILModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see equipments.meter.mil.ElectricMeterElectricityModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime);

		// update the current consumption since the last consumption update.
		// must be done before recomputing the instantaneous intensity.
		this.updateConsumption(elapsedTime);
		// recompute the current total intensity
		// double old = this.currentIntensity.getValue();
		// double i = this.computeTotalIntensity();
		// this.currentIntensity.setNewValue(i, this.getCurrentStateTime());
		// this.ownerComponent.setCurrentPowerConsumption(
		// 					new Measure<Double>(i, MeasurementUnit.AMPERES));
		//
		// if (Math.abs(old - i) > 0.000001) {
		// 	// Tracing
		// 	StringBuffer message =
		// 				new StringBuffer("current power consumption: ");
		// 	message.append(this.currentIntensity.getValue());
		// 	message.append(" at ");
		// 	message.append(this.getCurrentStateTime());
		// 	message.append('\n');
		// 	this.logMessage(message.toString());
		// }

		// here, the difference with the MIL model; the new value is set
		// directly in the component to be retrieved by its sensor methods.
		double oldP = this.currentTotalPowerProduced.getValue();
		double p = this.computeTotalProduction();
		this.currentTotalPowerProduced.setNewValue(p, this.getCurrentStateTime());
		this.ownerComponent.setCurrentPowerProduction(
						new Measure<Double>(p, MeasurementUnit.WATTS));
		
		if (Math.abs(oldP - p) > 0.000001) {
			// Tracing
			StringBuffer message =
						new StringBuffer("current power consumption: ");
			message.append(this.currentTotalPowerProduced.getValue());
			message.append(" at ");
			message.append(this.getCurrentStateTime());
			message.append('\n');
			this.logMessage(message.toString());
		}
	}
}
// -----------------------------------------------------------------------------

package equipments.meter.mil;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import equipments.HEM.simulation.HEM_ReportI;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import equipments.meter.ElectricMeter;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.InternalVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.Pair;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.exceptions.PreconditionException;
import utils.Electricity;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>ElectricMeterElectricityModel</code> defines the simulation
 * model for the electric meter electricity consumption.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This model is an HIOA model that imports variables, hence shows how this kind
 * of models are programmed.
 * </p>
 * 
 * <ul>
 * <li>Imported events: none</li>
 * <li>Exported events: none</li>
 * <li>Imported variables:
 *   name = {@code currentAirConditioningIntensity}, type = {@code Double}</li>
 *   name = {@code currentLampIntensity}, type = {@code Double}</li>
 *   name = {@code currentCookingPlateIntensity}, type = {@code Double}</li>
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code STEP > 0.0}
 * </pre>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// TODO	// no more invariant
 * </pre>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
@ModelImportedVariable(name = "currentCookingPlateIntensity", 
						type = Double.class)
 @ModelImportedVariable(name = "currentAirConditioningIntensity", 
 						type = Double.class)
////@ModelImportedVariable(name = "currentFridgeIntensity", type = Double.class) 
//
//@ModelImportedVariable(name = "currentPowerProducedSolarPanel", type = Double.class)
//@ModelImportedVariable(name = "currentPowerProducedPetrolGenerator", type = Double.class)
//
//@ModelExportedVariable(name = "currentTotalPowerProduced", type = Double.class)
@ModelExportedVariable(name = "currentTotalPowerConsumed", 
						type = Double.class)
@ModelImportedVariable(name = "currentLampIntensity", 
						type = Double.class)
public class ElectricMeterElectricityModel 
extends AtomicHIOA {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------
	private static final long serialVersionUID = 1L;
	/** URI for a MIL model; works when only one instance is created.		*/
	public static final String	MIL_URI = ElectricMeterElectricityModel.class.
												getSimpleName() + "-MIL";
	/** URI for a MIL real time model; works when only one instance is
	*  created.															*/
	public static final String	MIL_RT_URI = ElectricMeterElectricityModel.class.
												getSimpleName() + "-MIL-RT";
	/** URI for a SIL model; works when only one instance is created.		*/
	public static final String	SIL_URI = ElectricMeterElectricityModel.class.
												getSimpleName() + "-SIL";
	/** tension of electric circuit for appliances in volts.			 	*/
	public static final double TENSION = 220.0;

	/** evaluation step for the equation (assumed in hours).				*/
	protected static final double STEP = 60.0/3600.0;	// 60 seconds
	/** evaluation step as a duration, including the time unit.				*/
	protected final Duration evaluationStep;
	
	protected ElectricMeter						ownerComponent;

	/** final report of the simulation run.									*/
	protected ElectricMeterElectricityReport finalReport;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------
	
	/** current intensity of the Cooking Plate in amperes.					*/
	@ImportedVariable(type = Double.class)
	protected Value<Double> currentCookingPlateIntensity;
	
	/** current intensity of the Lamp in amperes.							*/
	 @ImportedVariable(type = Double.class)
	 protected Value<Double> 		currentLampIntensity;

	// /** current intensity of the Air Conditioning amperes.					*/
	 @ImportedVariable(type = Double.class)
	 protected Value<Double> currentAirConditioningIntensity;
	
	/** current intensity of the Fridge amperes.							*/
//	@ImportedVariable(type = Double.class)
//	protected Value<Double> currentFridgeIntensity;
	
	/** current power produce by the Solar Panel in Wh.						*/
//	@ImportedVariable(type = Double.class)
//	protected Value<Double> currentPowerProducedSolarPanel;
	
	/** current power produce by the Petrol Generator in Wh.				*/
//	@ImportedVariable(type = Double.class)
//	protected Value<Double> currentPowerProducedPetrolGenerator;
	
	/** current total intensity of the house in amperes.					*/
	 @InternalVariable(type = Double.class)
	 protected final Value<Double> currentIntensity = 
	 										new Value<Double>(this);

	/** the current total power produced by the producers					*/
//	@ExportedVariable(type = Double.class)
//	protected final Value<Double> currentTotalPowerProduced = new Value<Double>(this);
	
	/** the current total power consumed by the consumers					*/
	 @ExportedVariable(type = Double.class)
	 protected final Value<Double> currentCumulativeConsumption = 
	 										new Value<Double>(this);
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	/**
	 * create an <code>ElectricMeterElectricityModel</code> instance.
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
	public ElectricMeterElectricityModel(
			String uri,
			TimeUnit simulatedTimeUnit,
			AtomicSimulatorI simulationEngine
			) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
		this.evaluationStep = new Duration(STEP, this.getSimulatedTimeUnit());
		this.getSimulationEngine().setLogger(new StandardLogger());
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------
	
	/**
	 * update the total electricity consumption in kwh given the current
	 * intensity has been constant for the duration {@code d}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code d != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param d	duration for which the intensity has been maintained.
	 */
	protected void updateConsumption(Duration d) 
	{
		 double c = this.currentCumulativeConsumption.getValue();
		 c += Electricity.computeConsumption(
		 		d, TENSION*this.currentIntensity.getValue()*1000.0);
		 Time t = this.currentCumulativeConsumption.getTime().add(d);
		 this.currentCumulativeConsumption.setNewValue(c, t);
	}

	/**
	 * update the total electricity production in kwh given the current
	 * production has been constant for the duration {@code d}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code d != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param d	duration for which the power level has been maintained.
	 */
	protected void updateProduction(Duration d) 
	{
//		double c = this.currentTotalPowerProduced.getValue();
//		c += Electricity.computeProduction(
//				d, TENSION*this.currentTotalPowerProduced.getValue()*1000.0);
//		Time t = this.currentTotalPowerProduced.getTime().add(d);
//		this.currentTotalPowerProduced.setNewValue(c, t);
	}
	
	/***********************************************************************************/
	/**
	 * compute the current total production.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return the current total electric production in Wh.
	 */
//	protected double computeTotalProduction() {
//		// simple sum of all incoming power produced Wh
//		double i = this.currentPowerProducedSolarPanel.getValue();
//				// this.currentPowerProducedPetrolGenerator.getValue();
//
//		// Tracing
//		if (this.currentTotalPowerProduced.isInitialised()) {
//			StringBuffer message = new StringBuffer("Current total production: ");
//			message.append(this.currentTotalPowerProduced.getValue()); // TODO AV pq = 0 tjr
//			message.append(" kWh at ");
//			message.append(this.getCurrentStateTime());
//			message.append('\n');
//			this.logMessage(message.toString());
//		}
//
//		return i; 
//	}

	/***********************************************************************************/
	/**
	 * compute the current total intensity.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return the current total intensity of electric consumption.
	 */
	 protected double computeTotalIntensity() {
	 	// simple sum of all incoming intensities
	 	double i = 	this.currentLampIntensity.getValue() 
	 				+ this.currentAirConditioningIntensity.getValue()
	 				+ this.currentCookingPlateIntensity.getValue();
				
	 	// Tracing
	 	if (this.currentIntensity.isInitialised()) {
	 		StringBuffer message = new StringBuffer("Current total consumption: ");
	 		message.append(this.currentIntensity.getValue());
	 		message.append(" at ");
	 		message.append(this.getCurrentStateTime());
	 		message.append('\n');
	 		this.logMessage(message.toString());
	 	}

	 	return i;
	 }

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------
	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#useFixpointInitialiseVariables()
	 */
	@Override
	public boolean useFixpointInitialiseVariables() {
		return true;
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#fixpointInitialiseVariables()
	 */
	@Override
	public Pair<Integer, Integer> fixpointInitialiseVariables() 
	{
		int justInitialised = 0;
		int notInitialisedYet = 0;

 		if (!this.currentIntensity.isInitialised()
 				&& this.currentCookingPlateIntensity.isInitialised() 
 				&& this.currentLampIntensity.isInitialised() 
 				&& this.currentAirConditioningIntensity.isInitialised()
 //				&& this.currentFridgeIntensity.isInitialised()
 //				&& this.currentPowerProducedSolarPanel.isInitialised()
 //				&& this.currentPowerProducedPetrolGenerator.isInitialised()
 				) {
 			double i = this.computeTotalIntensity();
 			this.currentIntensity.initialise(i);
 			this.currentCumulativeConsumption.initialise(0.0);
 			justInitialised += 1;
 		} else if (!this.currentIntensity.isInitialised()) {
 			notInitialisedYet += 1;
 		}

//		if (!this.currentTotalPowerProduced.isInitialised()
//				&& this.currentPowerProducedSolarPanel.isInitialised()) {
//			double p = this.computeTotalProduction();
//			this.currentTotalPowerProduced.initialise(p);
//			justInitialised++;
//		} else if (!this.currentTotalPowerProduced.isInitialised()) {
//			notInitialisedYet++;
//		}

		return new Pair<>(justInitialised, notInitialisedYet);
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI> output() {
		// The model does not export any event.
		return null;
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration timeAdvance() {
		// trigger a new internal transition at each evaluation step duration
		return this.evaluationStep;
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) 
	{
		super.userDefinedInternalTransition(elapsedTime);

		// update the current consumption since the last consumption update.
		// must be done before recomputing the instantaneous intensity.
		 this.updateConsumption(elapsedTime);
		// recompute the current total intensity
		 double i = this.computeTotalIntensity();
		 this.currentIntensity.setNewValue(i, this.getCurrentStateTime());
		// update 
//		double p = this.computeTotalProduction();
//		// recompute the current total production
//		this.currentTotalPowerProduced.setNewValue(p+this.currentTotalPowerProduced.getValue(), this.getCurrentStateTime());	
		
//		System.out.println("ElectricMeterElectricityModel: userDefinedInternalTransition: currentTotalPowerProduced = " + this.currentTotalPowerProduced.getValue());
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void endSimulation(Time endTime) {
//		this.updateConsumption(endTime.subtract(this.currentTotalPowerConsumed.getTime()));

		// must capture the current consumption before the finalisation
		// reinitialise the internal model variable.
		this.finalReport = new ElectricMeterElectricityReport(
				this.getURI(),
				 this.currentCumulativeConsumption.getValue()/1000.0,
				0.0
//				this.currentTotalPowerProduced.getValue()/1000.0  // TODO AV
				);

		this.logMessage("simulation ends.\n");
		super.endSimulation(endTime);
	}
	
	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation run parameters
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws MissingRunParameterException
	{
		super.setSimulationRunParameters(simParams);

		assert	simParams != null && !simParams.isEmpty() :
				new PreconditionException(
								"simParams != null && !simParams.isEmpty()");

		if (simParams.containsKey(
						AtomicSimulatorPlugin.OWNER_RUNTIME_PARAMETER_NAME)) {
			this.ownerComponent = 
				(ElectricMeter) simParams.get(
						AtomicSimulatorPlugin.OWNER_RUNTIME_PARAMETER_NAME);
			this.getSimulationEngine().setLogger(
						AtomicSimulatorPlugin.createComponentLogger(simParams));
		}
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation report
	// -------------------------------------------------------------------------
	/**
	 * The class <code>ElectricMeterElectricityReport</code> implements the
	 * simulation report for the <code>ElectricMeterElectricityModel</code>.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}	// no invariant
	 * </pre>
	 * 
	 */
	public static class ElectricMeterElectricityReport
	implements SimulationReportI, HEM_ReportI {
		private static final long serialVersionUID = 1L;
		protected String modelURI;
		protected double totalConsumption; // in kwh
		protected double totalProduction; // in kwh

		/***********************************************************************************/
		public ElectricMeterElectricityReport(
				String modelURI,
				double totalConsumption,
				double totalProduction
				) {
			super();
			this.modelURI = modelURI;
			this.totalConsumption = totalConsumption;
			this.totalProduction = totalProduction;
		}

		/***********************************************************************************/
		@Override
		public String getModelURI() {
			return this.modelURI;
		}

		/***********************************************************************************/
		@Override
		public String printout(String indent) {
			StringBuffer ret = new StringBuffer(indent);
			ret.append("---\n");
			ret.append(indent);
			ret.append('|');
			ret.append(this.modelURI);
			ret.append(" report\n");
			ret.append(indent);
			ret.append('|');
			 ret.append("total consumption in kwh = ");
			 ret.append(this.totalConsumption);
//			ret.append("\n  |total production in kwh = ");
//			ret.append(this.totalProduction);
			ret.append(".\n");
			ret.append(indent);
			ret.append("---\n");
			return ret.toString();
		}		
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport()
	{
		return this.finalReport;
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/

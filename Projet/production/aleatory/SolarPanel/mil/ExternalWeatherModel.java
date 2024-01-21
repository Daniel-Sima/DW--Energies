package production.aleatory.SolarPanel.mil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.Pair;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>ExternalWeatherModel</code> defines a simulation model
 * for the environment, namely the sunshine.
 *
 * <p><strong>Like the temperature, the sunshine (solar irradiance) has the same behavior.</strong></p>
 * 
 * <p>
 * The model makes the sunshine (solar irradiance) vary over some period representing typically
 * a day. The variation is taken as a cosine between {@code Math.PI} and
 * {@code 3*Math.PI}. The cosine (plus 1 and divided by 2 to vary between 0 and
 * 1) is taken as a coefficient applied to the maximal variation over a day and
 * then added to the minimal sunshine to get the current sunshine.
 * </p>
 * 
 * <ul>
 * <li>Imported events: none</li>
 * <li>Exported events: none</li>
 * <li>Imported variables: none</li>
 * <li>Exported variables:
 *   name = {@code externalSolarIrradiance}, type = {@code Double}</li>
 * </ul>
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code cycleTime >= 0.0 && cycleTime <= PERIOD}
 * invariant	{@code STEP > 0.0}
 * </pre>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code MAX_EXTERNAL_SOLAR_IRRADIANCE > MIN_EXTERNAL_SOLAR_IRRADIANCE}
 * invariant	{@code PERIOD > 0.0}
 * </pre>
 * 
 * <p>Created on : 2023-11-11</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
@ModelExportedVariable(name = "externalSolarIrradiance", type = Double.class)
public class ExternalWeatherModel 
extends	AtomicHIOA {

	// Declaring ANSI_RESET so that we can reset the color 
	public static final String ANSI_RESET = "\u001B[0m"; 
	// Declaring colors
	public static final String ANSI_CYAN = "\u001B[36m"; 

	// Declaring the background color 
	public static final String ANSI_RED_BACKGROUND  = "\u001B[41m"; 
	public static final String ANSI_BLACK_BACKGROUND  = "\033[40m"; 
	public static final String ANSI_GREY_BACKGROUND  = "\033[0;100m"; 
	public static final String ANSI_BLUE_BACKGROUND  = "\u001B[44m"; 

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** URI for a model; works when only one instance is created.			*/
	public static final String	URI = ExternalWeatherModel.class.getSimpleName();
	/** URI for a model; works when the MIL RT instance is created.			*/
	public static final String	MIL_RT_URI = URI + "-MIL-RT";
	/** URI for a model; works when the SIL instance is created.			*/
	public static final String	SIL_URI = URI + "-SIL";

	/** minimal external solar irradiance (W/m^2).							*/
	public static final double MIN_EXTERNAL_SOLAR_IRRADIANCE = 0;
	/** maximal external solar irradiance (W/m^2).							*/
	public static final double MAX_EXTERNAL_SOLAR_IRRADIANCE = 800.0;
	/** period of the temperature variation cycle (day); the cycle begins
	 *  at the minimal temperature and ends at the same temperature.		*/
	public static final double PERIOD = 24.0;

	/** evaluation step for the equation (assumed in hours).				*/
	protected static double	STEP = (10* 60.0)/3600.0;	// 60 seconds * 10 = 10 min
	/** evaluation step as a duration, including the time unit.				*/
	protected final Duration evaluationStep;

	protected double cycleTime;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	/** current external solar irradiance (W/m^2).							*/
	@ExportedVariable(type = Double.class)
	protected final Value<Double> externalSolarIrradiance = new Value<Double>(this);

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an external solar irradiance MIL model instance.
	 * 
	 * <p><strong>Contract</strong></p>
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
	public ExternalWeatherModel(
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
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void	initialiseState(Time initialTime) {
		super.initialiseState(initialTime);

		this.cycleTime = 0.0;
	}

	/***********************************************************************************/
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
	public Pair<Integer, Integer> fixpointInitialiseVariables() {
		if (!this.externalSolarIrradiance.isInitialised()) {
			this.externalSolarIrradiance.initialise(MIN_EXTERNAL_SOLAR_IRRADIANCE);

			this.getSimulationEngine().toggleDebugMode();
			this.logMessage("simulation begins.\n");
			StringBuffer message =
					new StringBuffer("current external soolar irradiance: ");
			message.append(this.externalSolarIrradiance.getValue());
			message.append(" at ");
			message.append(this.getCurrentStateTime());
			message.append("\n");
			this.logMessage(message.toString());

			return new Pair<>(1, 0);
		} else {
			return new Pair<>(0, 0);
		}
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#initialiseVariables()
	 */
	@Override
	public void	initialiseVariables() {
		super.initialiseVariables();
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI> output() {
		// the model does not export any event.
		return null;
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration timeAdvance() {
		// the model makes an internal transition every evaluation step
		// duration
		return this.evaluationStep;
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {
		super.userDefinedInternalTransition(elapsedTime);

		// compute the current time in the cycle
		this.cycleTime += elapsedTime.getSimulatedDuration();
		if (this.cycleTime > PERIOD) {
			this.cycleTime -= PERIOD;
		}
		// compute the new temperature
		double c = Math.cos((1.0 + this.cycleTime/(PERIOD/2.0))*Math.PI);
		double newSolarIrr =
				MIN_EXTERNAL_SOLAR_IRRADIANCE +
				(MAX_EXTERNAL_SOLAR_IRRADIANCE - MIN_EXTERNAL_SOLAR_IRRADIANCE)*
				((1.0 + c)/2.0);
		this.externalSolarIrradiance.setNewValue(newSolarIrr, this.getCurrentStateTime());

		// Tracing
		StringBuffer message =
				new StringBuffer(ANSI_GREY_BACKGROUND + "Current external solar irradiance: ");
		message.append(Math.round(this.externalSolarIrradiance.getValue().doubleValue() * 100.0) / 100.0);
		message.append(" W/m^2 at ");
		message.append(this.getCurrentStateTime());
		message.append("\n" + ANSI_RESET);
		this.logMessage(message.toString());
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void endSimulation(Time endTime) {
		this.logMessage("simulation ends.\n");
		super.endSimulation(endTime);
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation report
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getFinalReport()
	 */
	@Override
	public SimulationReportI getFinalReport() {
		return null;
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/

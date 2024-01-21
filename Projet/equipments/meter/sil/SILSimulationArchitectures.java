package equipments.meter.sil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import equipments.AirConditioning.mil.AirConditioningElectricityModel;
import equipments.AirConditioning.mil.events.Cool;
import equipments.AirConditioning.mil.events.DoNotCool;
import equipments.AirConditioning.mil.events.SetPowerAirConditioning;
import equipments.AirConditioning.mil.events.SwitchOffAirConditioning;
import equipments.AirConditioning.mil.events.SwitchOnAirConditioning;
import equipments.Lamp.mil.LampElectricityModel;
import equipments.Lamp.mil.events.DecreaseLamp;
import equipments.Lamp.mil.events.IncreaseLamp;
import equipments.Lamp.mil.events.SwitchOffLamp;
import equipments.Lamp.mil.events.SwitchOnLamp;
import equipments.meter.mil.ElectricMeterCoupledModel;
import fr.sorbonne_u.components.hem2023e3.equipments.hairdryer.mil.HairDryerElectricityModel;
import fr.sorbonne_u.components.hem2023e3.equipments.hairdryer.mil.events.SwitchOnHairDryer;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.hioa.architectures.HIOA_Composer;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTAtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTCoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;

// -----------------------------------------------------------------------------
/**
 * The class <code>SILSimulationArchitectures</code> defines the local
 * SIL simulation architectures pertaining to the electric meter components.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * Compared to the MIL architectures, the SIL just replace the
 * {@code ElectricMeterElectricityModel} by the
 * {@code ElectricMeterElectricitySILModel}.
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
 */
public abstract class	SILSimulationArchitectures
{
	/**
	 * create the local SIL real time simulation architecture for the
	 * {@code ElectricMeter} component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param accelerationFactor	acceleration factor for this run.
	 * @return						the local SIL real time simulation architecture for the {@code ElectricMeter} component.
	 * @throws Exception			<i>to do</i>.
	 */
	public static Architecture	createElectricMeterSILArchitecture(
		double accelerationFactor
		) throws Exception
	{
		// map that will contain the atomic model descriptors to construct
		// the simulation architecture
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();

		// the electric meter electricity model accumulates the electric
		// power consumption, an atomic HIOA model hence we use an
		// RTAtomicHIOA_Descriptor
		atomicModelDescriptors.put(
				ElectricMeterElectricitySILModel.SIL_URI,
				RTAtomicHIOA_Descriptor.create(
						ElectricMeterElectricitySILModel.class,
						ElectricMeterElectricitySILModel.SIL_URI,
						TimeUnit.HOURS,
						null,
						accelerationFactor));
		atomicModelDescriptors.put(
				LampElectricityModel.SIL_URI,
				RTAtomicHIOA_Descriptor.create(
						LampElectricityModel.class,
						LampElectricityModel.SIL_URI,
						TimeUnit.HOURS,
						null,
						accelerationFactor));
		atomicModelDescriptors.put(
				AirConditioningElectricityModel.SIL_URI,
				RTAtomicHIOA_Descriptor.create(
						AirConditioningElectricityModel.class,
						AirConditioningElectricityModel.SIL_URI,
						TimeUnit.HOURS,
						null,
						accelerationFactor));

		// map that will contain the coupled model descriptors to construct
		// the simulation architecture
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
															new HashMap<>();

		// the set of submodels of the coupled model, given by their URIs
		Set<String> submodels = new HashSet<String>();
		submodels.add(ElectricMeterElectricitySILModel.SIL_URI);
		submodels.add(LampElectricityModel.SIL_URI);
		submodels.add(AirConditioningElectricityModel.SIL_URI);

		Map<Class<? extends EventI>,EventSink[]> imported = new HashMap<>();
		imported.put(
				SwitchOnLamp.class,
				new EventSink[] {
					new EventSink(LampElectricityModel.SIL_URI,
								  SwitchOnLamp.class)
				});
		imported.put(
			SwitchOffLamp.class,
			new EventSink[] {
				new EventSink(LampElectricityModel.SIL_URI,
								SwitchOffLamp.class)
			});
		imported.put(
			DecreaseLamp.class,
			new EventSink[] {
				new EventSink(LampElectricityModel.SIL_URI,
								DecreaseLamp.class)
			});
		imported.put(
			IncreaseLamp.class,
			new EventSink[] {
				new EventSink(LampElectricityModel.SIL_URI,
								IncreaseLamp.class)
			});

		imported.put(
				SetPowerAirConditioning.class,
				new EventSink[] {
						new EventSink(AirConditioningElectricityModel.SIL_URI,
									  SetPowerAirConditioning.class)
				});
		imported.put(
				SwitchOnAirConditioning.class,
				new EventSink[] {
						new EventSink(AirConditioningElectricityModel.SIL_URI,
									  SwitchOnAirConditioning.class)
				});
		imported.put(
				SwitchOffAirConditioning.class,
				new EventSink[] {
						new EventSink(AirConditioningElectricityModel.SIL_URI,
									  SwitchOffAirConditioning.class)
				});
		imported.put(
				Cool.class,
				new EventSink[] {
						new EventSink(AirConditioningElectricityModel.SIL_URI,
									  Cool.class)
				});
		imported.put(
				DoNotCool.class,
				new EventSink[] {
						new EventSink(AirConditioningElectricityModel.SIL_URI,
									  DoNotCool.class)
				});

		// variable bindings between exporting and importing models
		Map<VariableSource,VariableSink[]> bindings =
								new HashMap<VariableSource,VariableSink[]>();
		bindings.put(
				new VariableSource("currentIntensity",
								   Double.class,
								   LampElectricityModel.SIL_URI),
				new VariableSink[] {
					new VariableSink("currentLampIntensity",
									 Double.class,
									 ElectricMeterElectricitySILModel.SIL_URI)
				});

		bindings.put(
				new VariableSource("currentIntensity",
								   Double.class,
								   AirConditioningElectricityModel.SIL_URI),
				new VariableSink[] {
					new VariableSink("currentAirConditioningIntensity",
									 Double.class,
									 ElectricMeterElectricitySILModel.SIL_URI)
				});

		coupledModelDescriptors.put(
				ElectricMeterCoupledModel.SIL_URI,
				new RTCoupledHIOA_Descriptor(
						ElectricMeterCoupledModel.class,
						ElectricMeterCoupledModel.SIL_URI,
						submodels,
						imported,
						null,
						null,
						null,
						null,
						null,
						bindings,
						new HIOA_Composer(),
						accelerationFactor));

		Architecture architecture =
				new RTArchitecture(
						ElectricMeterCoupledModel.SIL_URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.HOURS);

		return architecture;
	}
}
// -----------------------------------------------------------------------------

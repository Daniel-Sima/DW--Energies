package equipments.meter.mil;

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
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.CoupledHIOA_Descriptor;
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
 * The class <code>MILSimulationArchitectures</code>  defines the local MIL
 * simulation architecture pertaining to the electric meter component.
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
 * <p>Created on : 2023-11-16</p>
 * 
 * @author	<a href="mailto:walter.abeles@etu.sorbonne-universite.fr">Walter Abeles</a>
 */
public abstract class	MILSimulationArchitectures
{
	/**
	 * create the local MIL simulation architecture for the {@code ElectricMeter}
	 * component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the local MIL simulation architecture for the {@code ElectricMeter} component.
	 * @throws Exception	<i>to do</i>.
	 */
	public static Architecture	createElectricMeterMILArchitecture()
	throws Exception
	{
		// map that will contain the atomic model descriptors to construct
		// the simulation architecture
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
				new HashMap<>();

		// the electric meter electricity model accumulates the electric
		// power consumption, an atomic HIOA model hence we use an
		// RTAtomicHIOA_Descriptor
		atomicModelDescriptors.put(
				ElectricMeterElectricityModel.MIL_URI,
				AtomicHIOA_Descriptor.create(
						ElectricMeterElectricityModel.class,
						ElectricMeterElectricityModel.MIL_URI,
						TimeUnit.HOURS,
						null));
		// The electricity models of all appliances will need to be put within
		// the ElectricMeter simulator to be able to share the variables
		// containing their power consumptions.
		
		atomicModelDescriptors.put(
				LampElectricityModel.MIL_URI,
				AtomicHIOA_Descriptor.create(
						LampElectricityModel.class,
						LampElectricityModel.MIL_URI,
						TimeUnit.HOURS,
						null));
		
		atomicModelDescriptors.put(
				AirConditioningElectricityModel.MIL_URI,
				AtomicHIOA_Descriptor.create(
						AirConditioningElectricityModel.class,
						AirConditioningElectricityModel.MIL_URI,
						TimeUnit.HOURS,
						null));

		// map that will contain the coupled model descriptors to construct
		// the simulation architecture
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
				new HashMap<>();

		// the set of submodels of the coupled model, given by their URIs
		Set<String> submodels = new HashSet<String>();
		submodels.add(ElectricMeterElectricityModel.MIL_URI);
		submodels.add(LampElectricityModel.MIL_URI);
		submodels.add(AirConditioningElectricityModel.MIL_URI);

		// events imported from the HairDryer component model architecture
		Map<Class<? extends EventI>,EventSink[]> imported = new HashMap<>();
		
		imported.put(
				SwitchOnLamp.class,
				new EventSink[] {
					new EventSink(LampElectricityModel.MIL_URI,
								  SwitchOnLamp.class)
				});
		imported.put(
				SwitchOffLamp.class,
				new EventSink[] {
					new EventSink(LampElectricityModel.MIL_URI,
								  SwitchOffLamp.class)
				});
		imported.put(
				DecreaseLamp.class,
				new EventSink[] {
					new EventSink(LampElectricityModel.MIL_URI,
								  DecreaseLamp.class)
				});
		imported.put(
				IncreaseLamp.class,
				new EventSink[] {
					new EventSink(LampElectricityModel.MIL_URI,
								  IncreaseLamp.class)
				});
		
		
		imported.put(
				SetPowerAirConditioning.class,
				new EventSink[] {
						new EventSink(AirConditioningElectricityModel.MIL_URI,
									  SetPowerAirConditioning.class)
				});
		imported.put(
				SwitchOnAirConditioning.class,
				new EventSink[] {
						new EventSink(AirConditioningElectricityModel.MIL_URI,
									  SwitchOnAirConditioning.class)
				});
		imported.put(
				SwitchOffAirConditioning.class,
				new EventSink[] {
						new EventSink(AirConditioningElectricityModel.MIL_URI,
									  SwitchOffAirConditioning.class)
				});
		imported.put(
				Cool.class,
				new EventSink[] {
						new EventSink(AirConditioningElectricityModel.MIL_URI,
									  Cool.class)
				});
		imported.put(
				DoNotCool.class,
				new EventSink[] {
						new EventSink(AirConditioningElectricityModel.MIL_URI,
									  DoNotCool.class)
				});

		// // variable bindings between exporting and importing models
		Map<VariableSource,VariableSink[]> bindings =
								new HashMap<VariableSource,VariableSink[]>();
		bindings.put(
				new VariableSource("currentIntensity",
								   Double.class,
								   LampElectricityModel.MIL_URI),
				new VariableSink[] {
					new VariableSink("currentLampIntensity",
									 Double.class,
									 ElectricMeterElectricityModel.MIL_URI)
				});
		bindings.put(
				new VariableSource("currentIntensity",
								   Double.class,
								   AirConditioningElectricityModel.MIL_URI),
				new VariableSink[] {
					new VariableSink("currentAirConditioningIntensity",
									 Double.class,
									 ElectricMeterElectricityModel.MIL_URI)
				});

		coupledModelDescriptors.put(
				ElectricMeterCoupledModel.MIL_URI,
				new CoupledHIOA_Descriptor(
						ElectricMeterCoupledModel.class,
						ElectricMeterCoupledModel.MIL_URI,
						submodels,
						imported,
						null,
						null,
						null,
						null,
						null,
						bindings,
						new HIOA_Composer()));

		Architecture architecture =
				new Architecture(
						ElectricMeterCoupledModel.MIL_URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.HOURS);

		return architecture;
	}

	/**
	 * create the local MIL real time simulation architecture for the
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
	 * @return						the local MIL real time simulation architecture for the {@code ElectricMeter} component.
	 * @throws Exception		<i>to do</i>.
	 */
	public static Architecture	createElectricMeterMILRTArchitecture(
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
				ElectricMeterElectricityModel.MIL_RT_URI,
				RTAtomicHIOA_Descriptor.create(
						ElectricMeterElectricityModel.class,
						ElectricMeterElectricityModel.MIL_RT_URI,
						TimeUnit.HOURS,
						null,
						accelerationFactor));
		// The electricity models of all appliances will need to be put within
		// the ElectricMeter simulator to be able to share the variables
		// containing their power consumptions.
		atomicModelDescriptors.put(
				LampElectricityModel.MIL_RT_URI,
				RTAtomicHIOA_Descriptor.create(
						LampElectricityModel.class,
						LampElectricityModel.MIL_RT_URI,
						TimeUnit.HOURS,
						null,
						accelerationFactor));
		atomicModelDescriptors.put(
				AirConditioningElectricityModel.MIL_RT_URI,
				RTAtomicHIOA_Descriptor.create(
						AirConditioningElectricityModel.class,
						AirConditioningElectricityModel.MIL_RT_URI,
						TimeUnit.HOURS,
						null,
						accelerationFactor));

		// map that will contain the coupled model descriptors to construct
		// the simulation architecture
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
				new HashMap<>();

		// the set of submodels of the coupled model, given by their URIs
		Set<String> submodels = new HashSet<String>();
		submodels.add(ElectricMeterElectricityModel.MIL_RT_URI);
		submodels.add(LampElectricityModel.MIL_RT_URI);
		submodels.add(AirConditioningElectricityModel.MIL_RT_URI);

		Map<Class<? extends EventI>,EventSink[]> imported = new HashMap<>();

		imported.put(
				SwitchOnLamp.class,
				new EventSink[] {
						new EventSink(LampElectricityModel.MIL_RT_URI,
									  SwitchOnLamp.class)
				});
		imported.put(
				SwitchOffLamp.class,
				new EventSink[] {
						new EventSink(LampElectricityModel.MIL_RT_URI,
									  SwitchOffLamp.class)
				});
		imported.put(
				DecreaseLamp.class,
				new EventSink[] {
						new EventSink(LampElectricityModel.MIL_RT_URI,
									  DecreaseLamp.class)
				});
		imported.put(
				IncreaseLamp.class,
				new EventSink[] {
						new EventSink(LampElectricityModel.MIL_RT_URI,
									  IncreaseLamp.class)
				});
		
		imported.put(
			SetPowerAirConditioning.class,
			new EventSink[] {
					new EventSink(AirConditioningElectricityModel.MIL_RT_URI,
								SetPowerAirConditioning.class)
			});
		imported.put(
				SwitchOnAirConditioning.class,
				new EventSink[] {
						new EventSink(AirConditioningElectricityModel.MIL_RT_URI,
									  SwitchOnAirConditioning.class)
				});
		imported.put(
				SwitchOffAirConditioning.class,
				new EventSink[] {
						new EventSink(AirConditioningElectricityModel.MIL_RT_URI,
									  SwitchOffAirConditioning.class)
				});
		imported.put(
				Cool.class,
				new EventSink[] {
						new EventSink(AirConditioningElectricityModel.MIL_RT_URI,
									  Cool.class)
				});
		imported.put(
				DoNotCool.class,
				new EventSink[] {
						new EventSink(AirConditioningElectricityModel.MIL_RT_URI,
									  DoNotCool.class)
				});

		// variable bindings between exporting and importing models
		Map<VariableSource,VariableSink[]> bindings =
								new HashMap<VariableSource,VariableSink[]>();
		bindings.put(
				new VariableSource("currentIntensity",
								   Double.class,
								   LampElectricityModel.MIL_RT_URI),
				new VariableSink[] {
					new VariableSink("currentLampIntensity",
									 Double.class,
									 ElectricMeterElectricityModel.MIL_RT_URI)
				});
		bindings.put(
				new VariableSource("currentIntensity",
								   Double.class,
								   AirConditioningElectricityModel.MIL_RT_URI),
				new VariableSink[] {
					new VariableSink("currentAirConditioningIntensity",
									 Double.class,
									 ElectricMeterElectricityModel.MIL_RT_URI)
				});

		coupledModelDescriptors.put(
				ElectricMeterCoupledModel.MIL_RT_URI,
				new RTCoupledHIOA_Descriptor(
						ElectricMeterCoupledModel.class,
						ElectricMeterCoupledModel.MIL_RT_URI,
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
						ElectricMeterCoupledModel.MIL_RT_URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.HOURS);

		return architecture;
	}
}
// -----------------------------------------------------------------------------

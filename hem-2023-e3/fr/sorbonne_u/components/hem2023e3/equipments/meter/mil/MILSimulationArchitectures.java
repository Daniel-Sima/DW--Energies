package fr.sorbonne_u.components.hem2023e3.equipments.meter.mil;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a
// basic component programming model to program with components
// real time distributed applications in the Java programming language.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
//
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
//
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
//
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.hem2023e3.equipments.hairdryer.mil.HairDryerElectricityModel;
import fr.sorbonne_u.components.hem2023e3.equipments.hairdryer.mil.events.SetHighHairDryer;
import fr.sorbonne_u.components.hem2023e3.equipments.hairdryer.mil.events.SetLowHairDryer;
import fr.sorbonne_u.components.hem2023e3.equipments.hairdryer.mil.events.SwitchOffHairDryer;
import fr.sorbonne_u.components.hem2023e3.equipments.hairdryer.mil.events.SwitchOnHairDryer;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.mil.HeaterElectricityModel;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.mil.events.DoNotHeat;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.mil.events.Heat;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.mil.events.SetPowerHeater;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.mil.events.SwitchOffHeater;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.mil.events.SwitchOnHeater;
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
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
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
				HairDryerElectricityModel.MIL_URI,
				AtomicHIOA_Descriptor.create(
						HairDryerElectricityModel.class,
						HairDryerElectricityModel.MIL_URI,
						TimeUnit.HOURS,
						null));
		atomicModelDescriptors.put(
				HeaterElectricityModel.MIL_URI,
				AtomicHIOA_Descriptor.create(
						HeaterElectricityModel.class,
						HeaterElectricityModel.MIL_URI,
						TimeUnit.HOURS,
						null));

		// map that will contain the coupled model descriptors to construct
		// the simulation architecture
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
				new HashMap<>();

		// the set of submodels of the coupled model, given by their URIs
		Set<String> submodels = new HashSet<String>();
		submodels.add(ElectricMeterElectricityModel.MIL_URI);
		submodels.add(HairDryerElectricityModel.MIL_URI);
		submodels.add(HeaterElectricityModel.MIL_URI);

		// events imported from the HairDryer component model architecture
		Map<Class<? extends EventI>,EventSink[]> imported = new HashMap<>();
		imported.put(
				SwitchOnHairDryer.class,
				new EventSink[] {
					new EventSink(HairDryerElectricityModel.MIL_URI,
								  SwitchOnHairDryer.class)
				});
		imported.put(
				SwitchOffHairDryer.class,
				new EventSink[] {
					new EventSink(HairDryerElectricityModel.MIL_URI,
								  SwitchOffHairDryer.class)
				});
		imported.put(
				SetLowHairDryer.class,
				new EventSink[] {
					new EventSink(HairDryerElectricityModel.MIL_URI,
								  SetLowHairDryer.class)
				});
		imported.put(
				SetHighHairDryer.class,
				new EventSink[] {
					new EventSink(HairDryerElectricityModel.MIL_URI,
								  SetHighHairDryer.class)
				});

		imported.put(
				SetPowerHeater.class,
				new EventSink[] {
						new EventSink(HeaterElectricityModel.MIL_URI,
									  SetPowerHeater.class)
				});
		imported.put(
				SwitchOnHeater.class,
				new EventSink[] {
						new EventSink(HeaterElectricityModel.MIL_URI,
									  SwitchOnHeater.class)
				});
		imported.put(
				SwitchOffHeater.class,
				new EventSink[] {
						new EventSink(HeaterElectricityModel.MIL_URI,
									  SwitchOffHeater.class)
				});
		imported.put(
				Heat.class,
				new EventSink[] {
						new EventSink(HeaterElectricityModel.MIL_URI,
									  Heat.class)
				});
		imported.put(
				DoNotHeat.class,
				new EventSink[] {
						new EventSink(HeaterElectricityModel.MIL_URI,
									  DoNotHeat.class)
				});

		// variable bindings between exporting and importing models
		Map<VariableSource,VariableSink[]> bindings =
								new HashMap<VariableSource,VariableSink[]>();
		bindings.put(
				new VariableSource("currentIntensity",
								   Double.class,
								   HairDryerElectricityModel.MIL_URI),
				new VariableSink[] {
					new VariableSink("currentHairDryerIntensity",
									 Double.class,
									 ElectricMeterElectricityModel.MIL_URI)
				});
		bindings.put(
				new VariableSource("currentIntensity",
								   Double.class,
								   HeaterElectricityModel.MIL_URI),
				new VariableSink[] {
					new VariableSink("currentHeaterIntensity",
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
				HairDryerElectricityModel.MIL_RT_URI,
				RTAtomicHIOA_Descriptor.create(
						HairDryerElectricityModel.class,
						HairDryerElectricityModel.MIL_RT_URI,
						TimeUnit.HOURS,
						null,
						accelerationFactor));
		atomicModelDescriptors.put(
				HeaterElectricityModel.MIL_RT_URI,
				RTAtomicHIOA_Descriptor.create(
						HeaterElectricityModel.class,
						HeaterElectricityModel.MIL_RT_URI,
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
		submodels.add(HairDryerElectricityModel.MIL_RT_URI);
		submodels.add(HeaterElectricityModel.MIL_RT_URI);

		Map<Class<? extends EventI>,EventSink[]> imported = new HashMap<>();
		imported.put(
				SwitchOnHairDryer.class,
				new EventSink[] {
					new EventSink(HairDryerElectricityModel.MIL_RT_URI,
								  SwitchOnHairDryer.class)
				});
		imported.put(
				SwitchOffHairDryer.class,
				new EventSink[] {
					new EventSink(HairDryerElectricityModel.MIL_RT_URI,
								  SwitchOffHairDryer.class)
				});
		imported.put(
				SetLowHairDryer.class,
				new EventSink[] {
					new EventSink(HairDryerElectricityModel.MIL_RT_URI,
								  SetLowHairDryer.class)
				});
		imported.put(
				SetHighHairDryer.class,
				new EventSink[] {
					new EventSink(HairDryerElectricityModel.MIL_RT_URI,
								  SetHighHairDryer.class)
				});

		imported.put(
				SetPowerHeater.class,
				new EventSink[] {
						new EventSink(HeaterElectricityModel.MIL_RT_URI,
									  SetPowerHeater.class)
				});
		imported.put(
				SwitchOnHeater.class,
				new EventSink[] {
						new EventSink(HeaterElectricityModel.MIL_RT_URI,
									  SwitchOnHeater.class)
				});
		imported.put(
				SwitchOffHeater.class,
				new EventSink[] {
						new EventSink(HeaterElectricityModel.MIL_RT_URI,
									  SwitchOffHeater.class)
				});
		imported.put(
				Heat.class,
				new EventSink[] {
						new EventSink(HeaterElectricityModel.MIL_RT_URI,
									  Heat.class)
				});
		imported.put(
				DoNotHeat.class,
				new EventSink[] {
						new EventSink(HeaterElectricityModel.MIL_RT_URI,
									  DoNotHeat.class)
				});

		// variable bindings between exporting and importing models
		Map<VariableSource,VariableSink[]> bindings =
								new HashMap<VariableSource,VariableSink[]>();
		bindings.put(
				new VariableSource("currentIntensity",
								   Double.class,
								   HairDryerElectricityModel.MIL_RT_URI),
				new VariableSink[] {
					new VariableSink("currentHairDryerIntensity",
									 Double.class,
									 ElectricMeterElectricityModel.MIL_RT_URI)
				});
		bindings.put(
				new VariableSource("currentIntensity",
								   Double.class,
								   HeaterElectricityModel.MIL_RT_URI),
				new VariableSink[] {
					new VariableSink("currentHeaterIntensity",
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

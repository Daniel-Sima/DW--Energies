package production.aleatory.SolarPanel.mil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.CoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTCoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor;
import utils.ExecutionType;

public class MILSimulationArchitectures {

    public static Architecture createSolarPanelMILArchitecture() 
    throws Exception 
    {
        // map that will contain the atomic model descriptors to construct
			// the simulation architecture
        Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors = new HashMap<>();

        // the solar panel models simulating its electricity consumption
        //	and the external weather are atomic HIOA models
        // hence we use an AtomicHIOA_Descriptor(s)
        atomicModelDescriptors.put(
                SolarPanelElectricityModel.MIL_URI,
                AtomicHIOA_Descriptor.create(
                        SolarPanelElectricityModel.class,
                        SolarPanelElectricityModel.MIL_URI,
                        TimeUnit.HOURS,
                        null));
        atomicModelDescriptors.put(
                ExternalWeatherModel.MIL_URI,
                AtomicHIOA_Descriptor.create(
                        ExternalWeatherModel.class,
                        ExternalWeatherModel.MIL_URI,
                        TimeUnit.HOURS,
                        null));
        // the solar panel unit tester model only exchanges event, an
        // atomic model hence we use an AtomicModelDescriptor
        atomicModelDescriptors.put(
                SolarPanelUnitTesterModel.MIL_URI,
                AtomicModelDescriptor.create(
                        SolarPanelUnitTesterModel.class,
                        SolarPanelUnitTesterModel.MIL_URI,
                        TimeUnit.HOURS,
                        null));

        // map that will contain the coupled model descriptors to construct
        // the simulation architecture
        Map<String,CoupledModelDescriptor> coupledModelDescriptors =
                new HashMap<>();

        // the set of submodels of the coupled model, given by their MIL_URIs
        Set<String> submodels = new HashSet<String>();
        submodels.add(SolarPanelElectricityModel.MIL_URI);
        submodels.add(ExternalWeatherModel.MIL_URI);
        submodels.add(SolarPanelUnitTesterModel.MIL_URI);

        // variable bindings between exporting and importing models
        Map<VariableSource,VariableSink[]> bindings =
                new HashMap<VariableSource,VariableSink[]>();

        bindings.put(new VariableSource("externalSolarIrradiance",
                Double.class,
                ExternalWeatherModel.MIL_URI),
                new VariableSink[] {
                        new VariableSink("externalSolarIrradiance",
                                Double.class,
                                SolarPanelElectricityModel.MIL_URI)
        });

        // coupled model descriptor
        coupledModelDescriptors.put(
                SolarPanelCoupledModel.MIL_URI,
                new CoupledHIOA_Descriptor(
                        SolarPanelCoupledModel.class,
                        SolarPanelCoupledModel.MIL_URI,
                        submodels,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        bindings));

        // simulation architecture
        Architecture architecture =
                new Architecture(
                        SolarPanelCoupledModel.MIL_URI,
                        atomicModelDescriptors,
                        coupledModelDescriptors,
                        TimeUnit.HOURS);
    
        return architecture;
    }
    
    public static Architecture createSolarPanelRTArchitecture(
            ExecutionType currentExecutionType, 
            double accelerationFactor) 
    throws Exception 
    {
        String solarPanelUnitTesterModelURI = null;
        String solarPanelElectricityModelURI = null;
        String solarPanelCoupledModelURI = null;
        String externalWeatherModelURI = null;

        switch (currentExecutionType) {
            case MIL_RT_SIMULATION:
                solarPanelUnitTesterModelURI = SolarPanelUnitTesterModel.MIL_RT_URI;
                solarPanelElectricityModelURI = SolarPanelElectricityModel.MIL_RT_URI;
                solarPanelCoupledModelURI = SolarPanelCoupledModel.MIL_RT_URI;
                externalWeatherModelURI = ExternalWeatherModel.MIL_RT_URI;
                break;
            case SIL_SIMULATION:
                solarPanelUnitTesterModelURI = SolarPanelUnitTesterModel.SIL_URI;
                solarPanelElectricityModelURI = SolarPanelElectricityModel.SIL_URI;
                solarPanelCoupledModelURI = SolarPanelCoupledModel.SIL_URI;
                externalWeatherModelURI = ExternalWeatherModel.SIL_URI;
                break;
            default:
                throw new RuntimeException("incorrect execution type: " + currentExecutionType + "!");
        }

        Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
                        new HashMap<>();

        atomicModelDescriptors.put(
            externalWeatherModelURI,
            RTAtomicModelDescriptor.create(
                ExternalWeatherModel.class,
                externalWeatherModelURI,
                TimeUnit.HOURS,
                null,
                accelerationFactor));

        if(currentExecutionType == ExecutionType.MIL_RT_SIMULATION) {
            atomicModelDescriptors.put(
                SolarPanelUnitTesterModel.MIL_RT_URI,
                RTAtomicModelDescriptor.create(
                    SolarPanelUnitTesterModel.class,
                    SolarPanelUnitTesterModel.MIL_RT_URI,
                    TimeUnit.HOURS,
                    null,
                    accelerationFactor));
        }

        Map<String,CoupledModelDescriptor> coupledModelDescriptors =
                                                        new HashMap<>();

        Set<String> submodels = new HashSet<String>();
        submodels.add(externalWeatherModelURI);
        if(currentExecutionType == ExecutionType.MIL_RT_SIMULATION) {
            submodels.add(SolarPanelUnitTesterModel.MIL_RT_URI);
        }

        Map<VariableSource,VariableSink[]> bindings =
                            new HashMap<VariableSource,VariableSink[]>();

        bindings.put(
            new VariableSource(
                "externalSolarIrradiance",
                Double.class,
                externalWeatherModelURI),
            new VariableSink[] {
                new VariableSink(
                    "externalSolarIrradiance",
                    Double.class,
                    solarPanelElectricityModelURI)});
        
        coupledModelDescriptors.put(
                solarPanelCoupledModelURI,
                new RTCoupledHIOA_Descriptor(
                        SolarPanelCoupledModel.class,
                        SolarPanelCoupledModel.MIL_RT_URI,
                        submodels,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        bindings));

        Architecture architecture =
                new RTArchitecture(
                    solarPanelCoupledModelURI, 
                    atomicModelDescriptors, 
                    coupledModelDescriptors, 
                    TimeUnit.HOURS);

        return architecture;
    }
    
}

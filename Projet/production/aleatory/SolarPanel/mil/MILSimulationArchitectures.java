package production.aleatory.SolarPanel.mil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.CoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTAtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor;
import utils.ExecutionType;

public class MILSimulationArchitectures {

    public static Architecture createSolarPanelMILArchitecture() 
    throws Exception 
    {
        Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
				new HashMap<>();

        atomicModelDescriptors.put(
                ExternalWeatherModel.MIL_URI,
                AtomicHIOA_Descriptor.create(
                    ExternalWeatherModel.class,
                    ExternalWeatherModel.MIL_URI,
                    TimeUnit.HOURS,
                    null));
        
        atomicModelDescriptors.put(
                SolarPanelUnitTesterModel.MIL_URI,
                AtomicHIOA_Descriptor.create(
                    SolarPanelUnitTesterModel.class,
                    SolarPanelUnitTesterModel.MIL_URI,
                    TimeUnit.HOURS,
                    null));

        Map<String,CoupledModelDescriptor> coupledModelDescriptors =
                                                        new HashMap<>();

        Set<String> submodels = new HashSet<String>();
        submodels.add(ExternalWeatherModel.MIL_URI);
        submodels.add(SolarPanelUnitTesterModel.MIL_URI);


        Map<VariableSource,VariableSink[]> bindings =
                                        new HashMap<VariableSource,VariableSink[]>();

        bindings.put(
            new VariableSource(
                "externalSolarIrradiance",
                Double.class,
                ExternalWeatherModel.MIL_URI),
            new VariableSink[] {
                new VariableSink(
                    "externalSolarIrradiance",
                    Double.class,
                    SolarPanelElectricityModel.MIL_URI)});



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
        String externalWeatherModelURI = null;

        switch (currentExecutionType) {
            case MIL_RT_SIMULATION:
                solarPanelUnitTesterModelURI = SolarPanelUnitTesterModel.MIL_RT_URI;
                solarPanelElectricityModelURI = SolarPanelElectricityModel.MIL_RT_URI;
                externalWeatherModelURI = ExternalWeatherModel.MIL_RT_URI;
                break;
            case SIL_SIMULATION:
                solarPanelUnitTesterModelURI = SolarPanelUnitTesterModel.SIL_URI;
                solarPanelElectricityModelURI = SolarPanelElectricityModel.SIL_URI;
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
    }
    
}

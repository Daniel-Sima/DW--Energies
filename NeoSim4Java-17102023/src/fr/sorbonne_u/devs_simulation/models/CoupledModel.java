package fr.sorbonne_u.devs_simulation.models;

import java.io.Serializable;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a new
// implementation of the DEVS simulation <i>de facto</i> standard for Java.
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
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableVisibility;
import fr.sorbonne_u.devs_simulation.models.events.CallableEventAtomicSink;
import fr.sorbonne_u.devs_simulation.models.events.EventAtomicSource;
import fr.sorbonne_u.devs_simulation.models.events.EventConverterI;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.CoordinationRTEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.CoordinatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.Pair;
import fr.sorbonne_u.devs_simulation.utils.StandardCoupledModelReport;

// -----------------------------------------------------------------------------
/**
 * The class <code>CoupledModel</code> implements the most general methods and
 * instance variables for DEVS coupled models.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The class implement a polymorphic coordination algorithm able to take
 * into account the three kinds of atomic simulation models implemented in
 * the library i.e., standard DEVS atomic models, event scheduling view (ES)
 * atomic models and hybrid system models inspired from Lynch et al. hybrid
 * input/output automata (HIOA) and timed input/output automata (TIOA).
 * Hence, it is meant to be used as the base class of any user defined
 * coupled model. However, the library may include in the future other
 * implementations of coupled models including the coordination of other
 * types of simulation models of providing specialised and more efficient
 * coordination algorithms for specific subsets of types of simulation
 * models or even coordination algorithm tailored to homogeneous sets
 * of submodels of the same type.
 * </p>
 * <p>
 * As coordination is concerned, event scheduling view atomic models needs
 * nothing more than what is already required to coordinate standard DEVS
 * atomic models. Both exchanges external events exported by some models
 * and imported by others. When creating a coupled model, it is therefore
 * required to define the connections among the submodels.  it is also
 * required to define the events that are imported by the coupled model,
 * the submodels that consume them and, when necessary, how to convert a
 * coupled model imported event into the type of events imported by each
 * consuming submodel. Finally, it is also required to define the events
 * that are exported by the submodels and reexported by the coupled model.
 * Because the type of event exported by the coupled model need not be the
 * same as the one exported by the submodel, a conversion function can also
 * be defined.
 * </p>
 * <p>
 * HIOA atomic models add a very different aspect to the standard DEVS atomic
 * models as they are defined over continuous and discrete variables that may
 * be exported and imported among them. In a manner similar to the events,
 * a coupled model must define its imported variables and the submodels that
 * consume them, its exported variables and the submodels that export them
 * and the internal bindings of variables exported by a submodel and imported
 * by other submodels.
 * </p>
 * <p>
 * Besides the management of variables, HIOA models introduce a much tighter
 * coupling between models that share variables. At initialisation time, the
 * order through which variables are evaluated needs to follow their
 * export/import dependencies, exported variables being initialised before
 * their bound imported ones can be used to further initialise other internal
 * or exported variables which need them to be computed. Hence, HIOA models
 * need a finer initialisation protocol for the variables. I n fact, two
 * protocols are proposed:
 * </p>
 * <ol>
 * <li>An unordered protocol using the method {@code initialiseVariables} in
 *   which models can be initialised in any particular order because no
 *   dependency exists among their variables.</li>
 * <li>A fix point protocol using the method {@code fixpointInitialiseVariables}
 *   in which this method is called repeatedly on all models until all
 *   variables in all models have been initialised.</li>
 * </ol>
 * <p>
 * The choice between the two protocols is controlled by the method
 * {@code useFixpointInitialiseVariables} returning false by default. Each
 * atomic model can redefine this method to say that it uses the fix point
 * protocol (to be coherent, at least two models in a simulation architecture
 * must use the protocol). Coupled models detect this use by looking at all of
 * the descendant atomic models. Mixing models that use the simple protocol and
 * the fix point one is possible. In this case, the method
 * {@code initialiseVariables} will be called first on models not using the
 * fix point protocol and then the fix point protocol is applied.
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
 * <p>Created on : 2018-04-04</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class	CoupledModel
extends		Model
implements	CoupledModelI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// Composition time information

	/** Submodels of this coupled model.									*/
	protected final ModelI[]						submodels;
	/** Inverted index to get the indexes for models in the array of
	 *  submodels.															*/
	protected final Map<ModelI,Integer>				submodel2index;
	/** URIs of submodels of this coupled model.							*/
	protected final String[]						submodelURIs;
	/** Inverted index to get the indexes for URIs of models in the
	 *  array of submodels.														*/
	protected final Map<String,Integer>				submodelURI2index;
	/** Map from imported event types to direct submodels consuming them.	*/
	protected final Map<Class<? extends EventI>,EventSink[]>
													imported2sinks;
	/** Map from event types exported by this coupled model to their
	 *  source submodel.													*/
	protected final Map<Class<? extends EventI>,ReexportedEvent>
													exported2reexported;
	/** Internal connections from source submodels to sets of sinks
	 *  submodels.															*/
	protected final Map<EventSource,EventSink[]>	internalConnections;
	/** description of the variables imported by this model.				*/
	protected final StaticVariableDescriptor[]		importedVariables;
	/** description of the variables exported by this model.				*/
	protected final StaticVariableDescriptor[]		exportedVariables;
	/** map from imported variable descriptions to the submodels and
	 *  their own imported variables that will consume them.				*/
	protected final Map<StaticVariableDescriptor,
						VariableSink[]>				importedVars2sinks;
	/** map from description of variables exported by submodels that
	 *  are reexported by this coupled model.								*/
	protected final Map<VariableSource,
						StaticVariableDescriptor>	exportedVars2reexported;
	/** bindings of variables exported by submodels to imported ones
	 *  by other submodels.													*/
	protected final Map<VariableSource,
						VariableSink[]>				internalBindings;

	// Simulation time information

	/** Elapsed times of submodels since their last internal event (i.e.,
	 *  <code>currentStateTime</code>; conceptually, the time of the
	 *  last internal event plus the elapsed time corresponds to the
	 *  current global simulation time.										*/
	protected Duration[]							elapsedTimes;
	/** The submodel that will need to execute the next event.				*/
	protected ModelI								submodelOfNextEvent;
	/** Set of submodels that have external events waiting to be
	 * 	executed during the current simulation step.						*/
	protected final Set<ModelI>						activeSubmodels;
	/** Random number generator used to break the ties when several
	 *  submodels can perform an internal transition at the same time of
	 *  next internal transition simulation time. 							*/
	protected final Random							randomGenerator;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a coupled simulation model to be run by the given simulator with
	 * the given URI and with the given time unit for the simulation clock.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * TODO: complete...
	 * 
	 * <pre>
	 * pre	{@code submodels != null && submodels.length > 1}
	 * pre	{@code Stream.of(submodels).allMatch(s -> s!= null)}
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param uri				URI of the coupled model to be created.
	 * @param simulatedTimeUnit	time unit used in the simulation by the model.
	 * @param simulationEngine	simulation engine enacting the model.
	 * @param submodels			array of submodels of the new coupled model.
	 * @param imported			map from imported event types to submodels consuming them.
	 * @param reexported		map from event types exported by submodels that are reexported by this coupled model.
	 * @param connections		map connecting event sources to arrays of event sinks among submodels.
	 */
	public				CoupledModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		CoordinatorI simulationEngine,
		ModelI[] submodels,
		Map<Class<? extends EventI>, EventSink[]> imported,
		Map<Class<? extends EventI>, ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections
		)
	{
		this(uri, simulatedTimeUnit, simulationEngine,
			 submodels, imported, reexported, connections,
			 new HashMap<StaticVariableDescriptor,VariableSink[]>(),
			 new HashMap<VariableSource,StaticVariableDescriptor>(),
			 new HashMap<VariableSource,VariableSink[]>());
	}

	/**
	 * create a coupled simulation model to be run by the given simulator with
	 * the given URI and with the given time unit for the simulation clock.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * TODO: complete...
	 * 
	 * <pre>
	 * pre	{@code submodels != null && submodels.length > 1}
	 * pre	{@code Stream.of(submodels).allMatch(s -> s!= null)}
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param uri				URI of the coupled model to be created.
	 * @param simulatedTimeUnit	time unit used in the simulation by the model.
	 * @param simulationEngine	simulation engine enacting the model.
	 * @param submodels			array of submodels of the new coupled model.
	 * @param imported			map from imported event types to submodels consuming them.
	 * @param reexported		map from event types exported by submodels that are reexported by this coupled model.
	 * @param connections		map connecting event sources to arrays of event sinks among submodels.
	 * @param importedVars		variables imported by the coupled model that are consumed by submodels.
	 * @param reexportedVars	variables exported by submodels that are reexported by the coupled model.
	 * @param bindings			bindings between exported and imported variables among submodels.
	 */
	@SuppressWarnings("unchecked")
	public				CoupledModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		CoordinatorI simulationEngine,
		ModelI[] submodels,
		Map<Class<? extends EventI>, EventSink[]> imported,
		Map<Class<? extends EventI>, ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections,
		Map<StaticVariableDescriptor, VariableSink[]> importedVars,
		Map<VariableSource, StaticVariableDescriptor> reexportedVars,
		Map<VariableSource, VariableSink[]> bindings
		)
	{
		super(uri, simulatedTimeUnit, simulationEngine);

		assert	submodels != null && submodels.length > 1 :
				new AssertionError("Precondition violation: "
						+ "submodels != null && submodels.length > 1");
		assert	Stream.of(submodels).allMatch(s -> s!= null) :
				new AssertionError("Precondition violation: "
						+ "Stream.of(submodels).allMatch(s -> s!= null)");

		// Coupled model initialisation
		this.activeSubmodels = new HashSet<ModelI>();
		this.randomGenerator = new Random(System.currentTimeMillis());

		// Submodels management
		this.submodels = new ModelI[submodels.length];
		this.submodelURIs = new String[submodels.length];
		this.submodelURI2index = new HashMap<String,Integer>();
		this.submodel2index = new HashMap<ModelI,Integer>();
		HashMap<String,ModelI> uri2models = new HashMap<>();
		for (int i = 0 ; i < submodels.length ; i++) {
			this.submodels[i] = submodels[i];
			this.submodelURIs[i] = submodels[i].getURI();
			this.submodelURI2index.put(submodels[i].getURI(), i);
			this.submodel2index.put(submodels[i], i);
			uri2models.put(this.submodels[i].getURI(), this.submodels[i]);
		}
		this.elapsedTimes = new Duration[submodels.length];

		// Exported events management
		this.exported2reexported =
				new HashMap<Class<? extends EventI>,ReexportedEvent>();
		Vector<Class<? extends EventI>> tempExported =
								new Vector<Class<? extends EventI>>();
		for (Class<? extends EventI> ce : reexported.keySet()) {
			ReexportedEvent re = reexported.get(ce);
			this.exported2reexported.put(
					ce,
					new ReexportedEvent(re.exportingModelURI,
										re.sourceEventType,
										re.sinkEventType,
										re.converter));
			if (!tempExported.contains(re.sinkEventType)) {
				tempExported.add(re.sinkEventType);
			}
		}
		this.exportedEventTypes =
			(Class<? extends EventI>[]) new Class<?>[tempExported.size()];
		for (int i = 0 ; i < this.exportedEventTypes.length ; i++) {
			this.exportedEventTypes[i] = tempExported.get(i);
		}

		// Imported events management
		this.imported2sinks =
					new HashMap<Class<? extends EventI>,EventSink[]>();
		this.importedEventTypes =
			(Class<? extends EventI>[])
								new Class<?>[imported.keySet().size()];
		{
			int i = 0;
			for (Class<? extends EventI> ce : imported.keySet()) {
				this.importedEventTypes[i++] = ce;
				EventSink[] eks = imported.get(ce);
				EventSink[] tempEks = new EventSink[eks.length];
				for (int j = 0 ; j < eks.length ; j++) {
					tempEks[j] =
						new EventSink(eks[j].importingModelURI,
									  eks[j].sourceEventType,
									  eks[j].sinkEventType,
									  eks[j].converter);
				}
				this.imported2sinks.put(ce, tempEks);
			}
		}

		// Connections of imported/exported events between submodels: creation
		// if the internal representation.
		this.internalConnections = new HashMap<EventSource,EventSink[]>();
		for (EventSource sourceEP : connections.keySet()) {
			EventSink[] temp =
					new EventSink[connections.get(sourceEP).length];
			for (int i = 0 ; i < connections.get(sourceEP).length ; i++) {
				EventSink sink = connections.get(sourceEP)[i];
				temp[i] =
					new EventSink(sink.importingModelURI,
								  sink.sourceEventType,
								  sink.sinkEventType,
								  sink.converter);
			}
			this.internalConnections.put(
					new EventSource(sourceEP.exportingModelURI,
									sourceEP.sourceEventType,
									sourceEP.sourceEventType),
					temp);
			assert	this.submodelURI2index.containsKey(
											sourceEP.exportingModelURI);
		}

		// Management of variables

		this.importedVariables =
					new StaticVariableDescriptor[importedVars.size()];
		this.exportedVariables =
					new StaticVariableDescriptor[reexportedVars.size()];

		// Imported variables
		this.importedVars2sinks =
					new HashMap<StaticVariableDescriptor,VariableSink[]>();
		int index = 0;
		for (StaticVariableDescriptor vd : importedVars.keySet()) {
			assert	vd.getVisibility() == VariableVisibility.IMPORTED;
			VariableSink[] sinks = importedVars.get(vd);
			VariableSink[] newSinks = new VariableSink[sinks.length];
			for (int i = 0 ; i < sinks.length ; i++) {
				newSinks[i] = new VariableSink(
									sinks[i].importedVariableName,
									sinks[i].importedVariableType,
									sinks[i].sinkVariableName,
									sinks[i].sinkVariableType,
									sinks[i].sinkModelURI);
			}
			StaticVariableDescriptor newVd =
				new StaticVariableDescriptor(vd.getName(), vd.getType(),
														vd.getVisibility());
			this.importedVars2sinks.put(newVd, newSinks);
			this.importedVariables[index++] = newVd;
		}

		// Exported variables
		this.exportedVars2reexported =
					new HashMap<VariableSource,StaticVariableDescriptor>();
		index = 0;
		for (VariableSource source : reexportedVars.keySet()) {
			StaticVariableDescriptor sink = reexportedVars.get(source);
			StaticVariableDescriptor vd =
					new StaticVariableDescriptor(
							sink.getName(),
							sink.getType(),
							VariableVisibility.EXPORTED);
			this.exportedVariables[index++] = vd;
			assert	source.name.equals(sink.getName());
			assert	sink.getType().isAssignableFrom(source.type);
			this.exportedVars2reexported.put(
					new VariableSource(source.name,
									  source.type,
									  source.exportingModelURI),
					vd);
		}

		// Variable bindings among submodels: binding exported variables to
		// imported ones.
		this.internalBindings = new HashMap<VariableSource,VariableSink[]>();
		for (VariableSource source : bindings.keySet()) {
			VariableSink[] sinks = bindings.get(source);
			VariableSink[] newSinks = new VariableSink[sinks.length];
			for (int i = 0 ; i < sinks.length ; i++) {
				newSinks[i] =
					new VariableSink(sinks[i].importedVariableName,
									 sinks[i].importedVariableType,
									 sinks[i].sinkVariableName,
									 sinks[i].sinkVariableType,
									 sinks[i].sinkModelURI);
			}
			this.internalBindings.put(
					new VariableSource(source.name,
									   source.type,
									   source.exportingModelURI),
					newSinks);
		}
	}

	// -------------------------------------------------------------------------
	// Model manipulation methods (e.g., description, composition, ...)
	// -------------------------------------------------------------------------

	/**
	 * get the submodel at index <code>i</code>, returning it as implementing
	 * the interface <code>ModelI</code> rather than the declared
	 * <code>ModelDescriptionI</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code i >= 0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * <p>Throws an {@code AssertionError} if {@code i} is out of range.</p>
	 * 
	 * @param i	the index of the submodel.
	 * @return	the submodel at index <code>i</code>.
	 */
	protected ModelI	getSubmodel(int i)
	{
		assert	i >= 0:
				new AssertionError("Precondition violation: i >= 0");

		assert	i < this.submodels.length :
				new AssertionError("index " + i
								   + " out of range; not a submodel");

		return this.submodels[i];
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI#isSubmodel(java.lang.String)
	 */
	@Override
	public boolean		isSubmodel(String uri)
	{
		return uri != null && this.submodelURI2index.keySet().contains(uri);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI#isDescendant(java.lang.String)
	 */
	@Override
	public boolean		isDescendant(String uri)
	{
		if (uri == null || uri.isEmpty()) {
			return false;
		} else if (this.isSubmodel(uri)) {
			return true;
		} else {
			boolean found = false;
			for (int i = 0 ; !found && i < this.submodels.length ; i++) {
				if (!this.submodels[i].isAtomic()) {
					found =
						((CoupledModelI)this.getSubmodel(i)).isDescendant(uri);
				}
			}
			return found;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI#getDescendant(java.lang.String)
	 */
	@Override
	public ModelI		getDescendant(String uri)
	{
		ModelI ret = null;
		if (this.hasURI(uri)) {
			ret = this;
		} else {
			for (int i = 0 ; ret == null && i < this.submodels.length ; i++) {
				if (this.submodels[i].isAtomic()) {
					if (this.submodels[i].hasURI(uri)) {
						ret = this.submodels[i];
					}
				} else {
					ret = ((CoupledModelI)this.submodels[i]).getDescendant(uri);
				}
			}
		}
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI#getEventSinks(java.lang.Class)
	 */
	@Override
	public Set<EventSink> 	getEventSinks(Class<? extends EventI> ce)
	{
		try {
			assert	ce != null && this.isImportedEventType(ce) :
					new AssertionError("Precondition violation: "
							+ "ce != null && this.isImportedEventType(ce)");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		EventSink[] tmp = this.imported2sinks.get(ce);
		Set<EventSink> ret = new HashSet<EventSink>();
		for (int i = 0 ; i < tmp.length ; i++) {
			ret.add(tmp[i]);
		}
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI#getReexportedEvent(java.lang.Class)
	 */
	@Override
	public ReexportedEvent	getReexportedEvent(Class<? extends EventI> ce)
	{
		assert	ce != null :
				new AssertionError("Precondition violation: ce != null");

		return this.exported2reexported.get(ce);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getEventAtomicSource(java.lang.Class)
	 */
	@Override
	public EventAtomicSource		getEventAtomicSource(
		Class<? extends EventI> ce
		)
	{
		assert	ce != null && this.isExportedEventType(ce) :
				new AssertionError("Precondition violation: "
						+ "ce != null && this.isExportedEventType(ce)");

		ReexportedEvent re = this.exported2reexported.get(ce);
		assert	re.sinkEventType.isAssignableFrom(ce);
		int index = this.submodelURI2index.get(re.exportingModelURI);
		EventAtomicSource source =
			this.getSubmodel(index).getEventAtomicSource(re.sourceEventType);
		return new EventAtomicSource(
						source.exportingModelURI,
						source.sourceEventType,
						re.sinkEventType,
						EventConverterI.compose(re.converter,
											    source.converter));
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getEventAtomicSinks(java.lang.Class)
	 */
	@Override
	public Set<CallableEventAtomicSink>	getEventAtomicSinks(
		Class<? extends EventI> ce
		)
	{
		assert	ce != null && this.isImportedEventType(ce) :
				new AssertionError("Precondition violation: "
						+ "ce != null && this.isImportedEventType(ce)");

		Set<CallableEventAtomicSink> ret = new HashSet<CallableEventAtomicSink>();
		Set<EventSink> submodelSinks = this.getEventSinks(ce);
		for (EventSink es : submodelSinks) {
			assert	ce.equals(es.sourceEventType);
			assert	this.isSubmodel(es.importingModelURI);
			int submodelIndex =
				this.submodelURI2index.get(es.importingModelURI);
			Set<CallableEventAtomicSink> submodelAtomicSinks =
				this.getSubmodel(submodelIndex).
									getEventAtomicSinks(es.sinkEventType);
			for (CallableEventAtomicSink as : submodelAtomicSinks) {
				assert	es.sinkEventType.equals(as.sourceEventType);
				CallableEventAtomicSink sink =
					new CallableEventAtomicSink(
							as.importingModelURI,
							ce,
							as.sinkEventType,
							as.importingAtomicModelReference,
							EventConverterI.compose(as.converter,
												    es.converter));
				ret.add(sink);
			}
		}
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#addInfluencees(java.lang.String, java.lang.Class, java.util.Set)
	 */
	@Override
	public void			addInfluencees(
		String modelURI,
		Class<? extends EventI> ce,
		Set<CallableEventAtomicSink> influencees
		)
	{
		if (this.simulationEngine.hasDebugLevel(2)) {
			this.logMessage("CoupledModel>>addInfluencees");
		}

		assert	isDescendant(modelURI) :
				new AssertionError("Precondition violation: "
						+ "isDescendentModel(modelURI)");
		assert	ce != null && isExportedEventType(ce) :
				new AssertionError("Precondition violation: "
						+ "ce != null && isExportedEventType(ce)");
		assert	influencees != null && influencees.size() > 0 :
				new AssertionError("Precondition violation: "
						+ "influencees != null && influencees.size() > 0");

		ModelI submodel = null;
		for (int i = 0 ; submodel == null && i < this.submodels.length ; i++) {
			if (this.submodels[i].getURI().equals(modelURI) ||
				(!this.submodels[i].isAtomic() &&
					((CoupledModelI)this.submodels[i]).isDescendant(modelURI))) {
				submodel = this.submodels[i];
			}
		}
		assert	submodel != null;
		submodel.addInfluencees(modelURI, ce, influencees);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getInfluencees(java.lang.String, java.lang.Class)
	 */
	@Override
	public Set<CallableEventAtomicSink>	getInfluencees(
		String modelURI,
		Class<? extends EventI> ce
		)
	{
		assert	this.isDescendant(modelURI) :
				new AssertionError("Precondition violation: "
								   + "isDescendent(modelURI)");
		assert	ce != null :
				new AssertionError("Precondition violation: ce != null");

		return this.getDescendant(modelURI).getInfluencees(modelURI, ce);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#areInfluencedThrough(java.lang.String, java.util.Set, java.lang.Class)
	 */
	@Override
	public boolean		areInfluencedThrough(
		String modelURI,
		Set<String> destinationModelURIs,
		Class<? extends EventI> ce
		)
	{
		assert	this.isDescendant(modelURI) :
				new AssertionError("Precondition violation: "
								   + "isDescendent(modelURI)");
		assert	ce != null :
				new AssertionError("Precondition violation: ce != null");
		assert	destinationModelURIs != null && !destinationModelURIs.isEmpty() :
				new AssertionError("Precondition violation: "
								   + "destinationModelURIs != null && "
								   + "!destinationModelURIs.isEmpty()");
		assert	destinationModelURIs.stream().allMatch(
										uri -> uri != null && !uri.isEmpty()) :
				new AssertionError("Precondition violation: "
								   + "destinationModelURIs.stream().allMatch("
								   + "uri -> uri != null && !uri.isEmpty())");

		return this.getDescendant(modelURI).
						areInfluencedThrough(modelURI, destinationModelURIs, ce);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#isInfluencedThrough(java.lang.String, java.lang.String, java.lang.Class)
	 */
	@Override
	public boolean		isInfluencedThrough(
		String modelURI,
		String destinationModelURI,
		Class<? extends EventI> ce
		)
	{
		assert	this.isDescendant(modelURI) :
				new AssertionError("Precondition violation: "
								   + "isDescendentModel(modelURI)");
		assert	ce != null :
				new AssertionError("Precondition violation: ce != null");
		assert	destinationModelURI != null && !destinationModelURI.isEmpty() :
				new AssertionError("Precondition violation: "
								   + "destinationModelURI != null && "
								   + "!destinationModelURI.isEmpty()");

		return this.getDescendant(modelURI).
						isInfluencedThrough(modelURI, destinationModelURI, ce);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#isTIOA()
	 */
	@Override
	public boolean		isTIOA()
	{
		return this.importedVariables.length == 0 &&
										this.exportedVariables.length == 0;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableDefinitionsAndSharingI#isExportedVariable(java.lang.String, java.lang.Class)
	 */
	@Override
	public boolean		isExportedVariable(
		String name,
		Class<?> type
		)
	{
		assert	name != null && !name.isEmpty() :
				new AssertionError("Precondition violation: "
						+ "name != null && !name.isEmpty()");
		assert	type != null :
				new AssertionError("Precondition violation: type != null");

		boolean ret = false;
		for (StaticVariableDescriptor vd : this.exportedVariables) {
			ret = ret || (vd.getName().equals(name) &&
						type.isAssignableFrom(vd.getType()));
			if (ret) break;
		}
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableDefinitionsAndSharingI#isImportedVariable(java.lang.String, java.lang.Class)
	 */
	@Override
	public boolean		isImportedVariable(
		String name,
		Class<?> type
		)
	{
		assert	name != null && !name.isEmpty() :
				new AssertionError("Precondition violation: "
						+ "name != null && !name.isEmpty()");
		assert	type != null :
				new AssertionError("Precondition violation: type != null");

		boolean ret = false;
		for (StaticVariableDescriptor vd : this.importedVariables) {
			ret = ret || (vd.getName().equals(name) &&
							type.isAssignableFrom(vd.getType()));
			if (ret) break;
		}
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableDefinitionsAndSharingI#getImportedVariables()
	 */
	@Override
	public StaticVariableDescriptor[]	getImportedVariables()
	{
		StaticVariableDescriptor[] ret =
			new StaticVariableDescriptor[this.importedVariables.length];
		for (int i = 0 ; i < this.importedVariables.length ; i++) {
			ret[i] = new StaticVariableDescriptor(
								this.importedVariables[i].getName(),
								this.importedVariables[i].getType(),
								this.importedVariables[i].getVisibility());
		}
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableDefinitionsAndSharingI#getExportedVariables()
	 */
	@Override
	public StaticVariableDescriptor[]	getExportedVariables()
	{
		StaticVariableDescriptor[] ret =
			new StaticVariableDescriptor[this.exportedVariables.length];
		for (int i = 0 ; i < this.exportedVariables.length ; i++) {
			ret[i] = new StaticVariableDescriptor(
								this.exportedVariables[i].getName(),
								this.exportedVariables[i].getType(),
								this.exportedVariables[i].getVisibility());
		}
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableDefinitionsAndSharingI#getActualExportedVariableValueReference(java.lang.String, java.lang.String, java.lang.Class)
	 */
	@Override
	public Value<?>		getActualExportedVariableValueReference(
		String modelURI,
		String sourceVariableName,
		Class<?> sourceVariableType
		)
	{
		assert	modelURI != null && this.uri.equals(modelURI) :
				new AssertionError("Precondition violation: "
						+ "modelURI != null && getURI().equals(modelURI)");
		assert	this.isExportedVariable(
							sourceVariableName, sourceVariableType) :
				new AssertionError("Precondition violation:"
						+ "isExportedVariable("
						+ "sourceVariableName, sourceVariableType)");

		for (VariableSource source : this.exportedVars2reexported.keySet()) {
			StaticVariableDescriptor vd =
								this.exportedVars2reexported.get(source);
			if (vd.getName().equals(sourceVariableName) &&
						sourceVariableType.isAssignableFrom(vd.getType())) {
				return this.getSubmodel(this.submodelURI2index.get(
												source.exportingModelURI)).
								getActualExportedVariableValueReference(
													source.exportingModelURI,
													source.name,
													source.type);
			}
		}
		// not found.
		return null;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableDefinitionsAndSharingI#setImportedVariableValueReference(java.lang.String, java.lang.String, java.lang.Class, fr.sorbonne_u.devs_simulation.hioa.models.vars.Value)
	 */
	@Override
	public void			setImportedVariableValueReference(
		String modelURI,
		String sinkVariableName,
		Class<?> sinkVariableType,
		Value<?> value
		)
	{
		assert	modelURI != null && this.uri.equals(modelURI) :
				new AssertionError("Precondition violation: "
						+ "modelURI != null && getURI().equals(modelURI)");
		assert	this.isImportedVariable(sinkVariableName, sinkVariableType) :
				new AssertionError("Precondition violation: "
						+ "isImportedVariable(sinkVariableName, "
						+ "sinkVariableType)");

		for (StaticVariableDescriptor vd : this.importedVars2sinks.keySet()) {
			if (vd.getName().equals(sinkVariableName) &&
							vd.getType().isAssignableFrom(sinkVariableType)) {
				VariableSink[] sinks = this.importedVars2sinks.get(vd);
				for (int i = 0 ; i < sinks.length ; i++) {
					this.getSubmodel(this.submodelURI2index.get(
													sinks[i].sinkModelURI)).
							setImportedVariableValueReference(
											sinks[i].sinkModelURI,
											sinks[i].sinkVariableName,
											sinks[i].sinkVariableType, 
											value);
				}
			}
		}
	}

	// -------------------------------------------------------------------------
	// DEVS standard protocol and related methods
	//
	//    These methods are used only when the coupled model is tightly
	//    integrated with its submodels (and subsubmodels...) by using
	//    a single common simulation engine and then they are Java
	//    objects directly referenced by each others.
	//    When submodels are not co-localised with the coupled model
	//    or when they use different simulation engines, a coordination
	//    engine is used to bring together the simulation engines of the
	//    submodels and then it uses its own method to execute the simulation
	//    by calling the simulation engines of the submodels.
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#isCoordinated()
	 */
	@Override
	public boolean		isCoordinated()
	{
//		SimulatorI se = this.getSimulationEngine();
//		return !(se instanceof AtomicRTEngine ||
//										se instanceof CoordinationRTEngine);
		return !(this.getSimulationEngine() instanceof CoordinationRTEngine);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#isStateInitialised()
	 */
	@Override
	public boolean		isStateInitialised()
	{
		boolean ret = super.isStateInitialised();

		for (int i = 0 ; i < this.submodels.length ; i++) {
			ret = ((ModelI)this.getSubmodel(i)).isStateInitialised();
		}
		ret &= this.timeOfNextEvent != null;
		ret &= this.nextTimeAdvance != null;
		ret &= (this.submodelOfNextEvent != null ||
				this.timeOfNextEvent.equals(Time.INFINITY));
		ret &= this.elapsedTimes != null;
		ret &= this.activeSubmodels != null;

		return ret;
	}

//	/**
//	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#clockSynchronised()
//	 */
//	@Override
//	public boolean		clockSynchronised()
//	{
//		// TODO Auto-generated method stub
//		return false;
//	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime);

		for (int i = 0 ; i < this.submodels.length ; i++) {
			((ModelI)this.submodels[i]).initialiseState(initialTime);
		}
		for (int i = 0 ; i < this.elapsedTimes.length ; i++) {
			this.elapsedTimes[i] =
							Duration.zero(this.getSimulatedTimeUnit());
		}

		this.activeSubmodels.clear();

		// find the model with the next internal event transition.
		this.timeOfNextEvent = ((ModelI)this.getSubmodel(0)).getTimeOfNextEvent();
		this.submodelOfNextEvent = (ModelI)this.getSubmodel(0);
		for (int i = 1 ; i < this.submodels.length ; i++) {
			if (((ModelI)this.getSubmodel(i)).getTimeOfNextEvent().
										lessThan(this.timeOfNextEvent)) {
				this.timeOfNextEvent =
						((ModelI)this.getSubmodel(i)).getTimeOfNextEvent();
				this.submodelOfNextEvent = (ModelI)this.getSubmodel(i);
			}
		}
		this.nextTimeAdvance =
				this.timeOfNextEvent.subtract(this.currentStateTime);

		// Postconditions
		for (int i = 0 ; i < this.submodels.length ; i++) {
			((ModelI)this.submodels[i]).getCurrentStateTime().add(
													this.elapsedTimes[i]).
					equals(this.getCurrentStateTime());
		}
		if (!this.getTimeOfNextEvent().equals(Time.INFINITY)) {
			assert	this.getTimeOfNextEvent().subtract(
												this.getCurrentStateTime()).
									equals(this.getNextTimeAdvance());
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#allModelVariablesTimeInitialised()
	 */
	@Override
	public boolean		allModelVariablesTimeInitialised()
	{
		boolean ret = true;
		for (int i = 0 ; ret && i < this.submodels.length ; i++) {
			ret = ret && ((VariableInitialisationI)this.submodels[i]).
											allModelVariablesTimeInitialised();
		}
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#allModelVariablesInitialised()
	 */
	@Override
	public boolean		allModelVariablesInitialised()
	{
		boolean ret = true;
		for (int i = 0 ; ret && i < this.submodels.length ; i++) {
			ret = ret && ((VariableInitialisationI)this.submodels[i]).
												allModelVariablesInitialised();
		}
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#useFixpointInitialiseVariables()
	 */
	@Override
	public boolean		useFixpointInitialiseVariables()
	{
		boolean ret = false;
		for (int i = 0 ; !ret && i < this.submodels.length ; i++) {
			ret = ret || ((VariableInitialisationI)this.submodels[i]).
											useFixpointInitialiseVariables();
		}
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#initialiseVariables()
	 */
	@Override
	public void			initialiseVariables()
	{
		boolean isRoot = true;
		try {
			isRoot = this.isRoot();
		} catch (Exception e1) {
			throw new RuntimeException(e1) ;
		}

		if (isRoot) {
			// these conditions are computed over the whole tree, so it suffices
			// that the root coupled model verifies them
			assert	!useFixpointInitialiseVariables() :
					new AssertionError("Precondition violation: "
									   + "!useFixpointInitialiseVariables()");
			assert	allModelVariablesTimeInitialised() :
					new AssertionError("Precondition violation: "
									   + "allModelVariablesTimeInitialised()");
		}

		for (int i = 0 ; i < this.submodels.length ; i++) {
			((ModelI)this.submodels[i]).initialiseVariables();
		}

		if (isRoot) {
			// this condition is computed over the whole tree, so it suffices
			// that the root coupled model verifies them
			assert	allModelVariablesInitialised() :
					new AssertionError("Postcondition violation: "
									   + "allModelVariablesInitialised()");
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#fixpointInitialiseVariables()
	 */
	@Override
	public Pair<Integer, Integer>	fixpointInitialiseVariables()
	{
		boolean isRoot = true;
		try {
			isRoot = this.isRoot();
		} catch (Exception e1) {
			throw new RuntimeException(e1) ;
		}

		if (isRoot) {
			// these conditions are computed over the whole tree, so it suffices
			// that the root coupled model verifies them
			assert	useFixpointInitialiseVariables() :
					new AssertionError("Precondition violation: "
									   + "useFixpointInitialiseVariables()");
			assert	allModelVariablesTimeInitialised() :
					new AssertionError("Precondition violation: "
									   + "allModelVariablesTimeInitialised()");
		}

		Pair<Integer,Integer> ret = null;
		if (isRoot) {
			// only the root needs to perform the fixpoint computation
			ret = new Pair<>(Integer.MAX_VALUE, Integer.MAX_VALUE);
			while (!(ret.getFirst() == 0 && ret.getSecond() == 0)) {
				ret = this.onceFixpointInitialiseVariables();
				if (ret.getFirst() == 0 && ret.getSecond() > 0) {
					// if 0 variables have been initialised but one or more
					// variables were not during the last round, then it
					// means that there are circular dependencies or errors
					// in the implementation of the initialisation protocol
					// in the user's models
					throw new RuntimeException(
								"fixpoint initialisation protocol error " +
								"(circular dependencies or errors in the " +
								"implementation).");
				}
			}
		} else {
			ret = this.onceFixpointInitialiseVariables();
		}

		assert	ret.getFirst() >= 0 && ret.getSecond() >= 0 :
				new AssertionError("Postcondition violation: "
							   + "ret.getFirst() >= 0 && ret.getSecond() >= 0");
		if (isRoot) {
			// this condition can only be made true by the root coupled model
			assert	allModelVariablesInitialised() :
					new AssertionError("Postcondition violation: "
						+ "!(ret.getFirst() >= 0 || ret.getSecond() == 0) || "
						+ "allModelVariablesInitialised()");
		}

		return ret;
	}

	/**
	 * compute one run of the fixpoint algorithm to initialise the model
	 * variables.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code allModelVariablesTimeInitialised()}
	 * pre	{@code useFixpointInitialiseVariables()}
	 * post	{@code ret.getFirst() >= 0 && ret.getSecond() >= 0}
	 * post	{@code !(ret.getFirst() >= 0 || ret.getSecond() == 0) || allModelVariablesInitialised()}
	 * </pre>
	 *
	 * @return	a pair which first element is the number of newly initialised variables (<i>i.e.</i>) and the second the number of non initialised yet variables.
	 */
	protected Pair<Integer, Integer>	onceFixpointInitialiseVariables()
	{
		Pair<Integer,Integer> ret = new Pair<>(0, 0);
		for (int i = 0 ; i < this.submodels.length ; i++) {
			Pair<Integer,Integer> p =
				((VariableInitialisationI)this.submodels[i]).
											fixpointInitialiseVariables();
			ret = new Pair<>(ret.getFirst() + p.getFirst(),
							 ret.getSecond() + p.getSecond());
		}
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		return this.nextTimeAdvance;
	}

	/**
	 * If the submodel of next event is among the HIOA, every HIOA submodels
	 * will be executed at the next internal step so all of them must produce
	 * their outputs, otherwise only the submodel of next event will produce
	 * its output.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code current != null}
	 * post	{@code true}	// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#produceOutput(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			produceOutput(Time current)
	{
		assert	current != null :
				new AssertionError("Precondition violation: current != null");

		if (this.simulationEngine.hasDebugLevel(2)) {
			this.logMessage("CoupledModel>>produceOutput " + this.uri);
		}

		this.submodelOfNextEvent.produceOutput(current);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#internalTransition()
	 */
	@Override
	public void			internalTransition()
	{
		this.simulationEngine.internalEventStep();
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		// By default, do nothing.
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI#select(java.lang.String[])
	 */
	@Override
	public String		select(String[] candidates)
	{
		assert	candidates != null && candidates.length > 1;

		// ******************************************************************
		// TODO:
		//    - the selection should be done knowing the types of the events
		//      that are due to execute;
		//    - a simpler mean to provide a selection function than
		//      creating a subclass should be provided.
		// ******************************************************************

		int index = 0;
		if (candidates.length > 1) {
			// For the time being, random selection.
			index = this.randomGenerator.nextInt(candidates.length - 1);
		}
		return candidates[index];
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.CoupledModel#externalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			externalTransition(Duration elapsedTime)
	{
		this.simulationEngine.externalEventStep(elapsedTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void 		userDefinedExternalTransition(Duration elapsedTime)
	{
		// do nothing, if not redefined by concrete user subclasses.
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.CoupledModel#confluentTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			confluentTransition(Duration elapsedTime)
	{
		this.simulationEngine.confluentEventStep(elapsedTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#userDefinedConfluentTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedConfluentTransition(Duration elapsedTime)
	{
		throw new RuntimeException(
					"No confluent transition on coupled models.");
	}

	// -------------------------------------------------------------------------
	// Simulation run management
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Serializable> simParams
		) throws MissingRunParameterException
	{
		for (int i = 0 ; i < this.submodels.length ; i++) {
			((ModelI)this.submodels[i]).setSimulationRunParameters(simParams);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport()
	{
		// Default implementation; redefine if a specific report is needed.
		StandardCoupledModelReport report =
							new StandardCoupledModelReport(this.getURI());
		for (int i = 0 ; i < this.submodels.length ; i++) {
			report.addReport(this.submodels[i].getFinalReport());
		}
		return report;
	}

	// -------------------------------------------------------------------------
	// Debugging behaviour
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#showCurrentState(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			showCurrentState(String indent, Duration elapsedTime)
	{
		System.out.println(indent + "---------------------------------------------");
		String name = this.getClass().getCanonicalName();
		if (name.contains(".")) {
			int index = name.lastIndexOf('.');
			name = name.substring(index + 1);
		}
		System.out.println(
					indent + name + " " + this.uri
					+ " " + this.currentStateTime.getSimulatedTime()
					+ " " + elapsedTime.getSimulatedDuration());
		System.out.println(indent + "---------------------------------------------");
		this.showCurrentStateContent(indent, elapsedTime);
		System.out.println(indent+ "---------------------------------------------");		
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#showCurrentStateContent(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			showCurrentStateContent(
		String indent,
		Duration elapsedTime
		)
	{
		String et = "elapsed times = {";
		for (int i = 0 ; i < this.elapsedTimes.length ; i++) {
			try {
				et += "(" + this.submodels[i].getURI() + " => " + this.elapsedTimes[i].getSimulatedDuration() + ")";
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			if (i < this.elapsedTimes.length - 1) {
				et += ", ";
			}
		}
		System.out.println(indent + et + "}");
		try {
			System.out.println(indent + "next event submodel = " + this.submodelOfNextEvent.getURI());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		System.out.println(indent + "time of last event = " + this.currentStateTime.getSimulatedTime());
		System.out.println(indent + "next time advance = " + this.nextTimeAdvance.getSimulatedDuration());
		System.out.println(indent + "time of next event = " + this.timeOfNextEvent.getSimulatedTime());
		for (int i = 0 ; i < this.submodels.length ; i++) {
			((Model)this.submodels[i]).showCurrentState(
					indent + "    ", this.elapsedTimes[i]);
		}

	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#modelContentAsString(java.lang.String, java.lang.StringBuffer)
	 */
	@Override
	protected void		modelContentAsString(String indent, StringBuffer sb)
	{
		super.modelContentAsString(indent, sb);
		sb.append(indent);
		sb.append("submodels = [");
		for (int i = 0 ; i < this.submodels.length ; i++) {
			sb.append(this.submodels[i].getURI());
			if (i < this.submodels.length - 1) {
				sb.append(", ");
			}
		}
		sb.append("]\n");
		sb.append(indent);
		if (this.internalConnections.size() == 0) {
			sb.append("connections = none\n");
		} else {
			sb.append("connections =\n");
			for (EventSource eep : this.internalConnections.keySet()) {
				sb.append(indent);
				sb.append("  (");
				sb.append(eep.exportingModelURI);
				sb.append(", ");
				sb.append(eep.sourceEventType.getName());
				sb.append(") ==> [");
				for (int j = 0 ; j < this.internalConnections.get(eep).length ; j++) {
					sb.append("(");
					sb.append(this.internalConnections.get(eep)[j].
													importingModelURI);
					sb.append(", ");
					sb.append(this.internalConnections.get(eep)[j].
													sourceEventType.getName());
					sb.append(", ");
					sb.append(this.internalConnections.get(eep)[j].
													sinkEventType.getName());
					sb.append(")");
					if (j < this.internalConnections.get(eep).length - 1) {
						sb.append(", ");
					}
				}
				sb.append("]\n");
			}
		}
		sb.append(indent);
		sb.append("Imported variables:\n");
		for (StaticVariableDescriptor vd : this.importedVariables) {
			sb.append(indent);
			sb.append("  VariableDescriptor(");
			sb.append(vd.getName());
			sb.append(", ");
			sb.append(vd.getType());
			sb.append(", ");
			sb.append(vd.getVisibility());
			sb.append(")\n");
		}
		sb.append(indent);
		sb.append("Exported variables:\n");
		for (StaticVariableDescriptor vd : this.exportedVariables) {
			sb.append(indent);
			sb.append("  VariableDescriptor(");
			sb.append(vd.getName());
			sb.append(", ");
			sb.append(vd.getType());
			sb.append(", ");
			sb.append(vd.getVisibility());
			sb.append(")\n");
		}
		sb.append(indent);
		sb.append("Imported variables sinks:\n");
		for (StaticVariableDescriptor vd : this.importedVars2sinks.keySet()) {
			sb.append(indent);
			sb.append("  VariableDescriptor(");
			sb.append(vd.getName());
			sb.append(", ");
			sb.append(vd.getType());
			sb.append(", ");
			sb.append(vd.getVisibility());
			sb.append(") ==> [");
			VariableSink[] sinks = this.importedVars2sinks.get(vd);
			for (int i = 0 ; i < sinks.length ; i++) {
				sb.append("VariableSink(");
				sb.append(sinks[i].sinkModelURI);
				sb.append(", ");
				sb.append(sinks[i].importedVariableName);
				sb.append(", ");
				sb.append(sinks[i].importedVariableType);
				sb.append(", ");
				sb.append(sinks[i].sinkVariableName);
				sb.append(", ");
				sb.append(sinks[i].sinkVariableType);
				sb.append(")");
				if (i < sinks.length - 1) {
					sb.append(", ");
				}
			}
			sb.append("]\n");
		}
		sb.append(indent);
		sb.append("Exported variables sources:\n");
		for (VariableSource vs : this.exportedVars2reexported.keySet()) {
			StaticVariableDescriptor vd = this.exportedVars2reexported.get(vs);
			sb.append(indent);
			sb.append("  VariableSource(");
			sb.append(vs.exportingModelURI);
			sb.append(", ");
			sb.append(vs.name);
			sb.append(", ");
			sb.append(vs.type);
			sb.append(") == > VariableDescriptor(");
			sb.append(vd.getName());
			sb.append(", ");
			sb.append(vd.getType());
			sb.append(", ");
			sb.append(vd.getVisibility());
			sb.append(")\n");
		}
		sb.append(indent);
		sb.append("InternalConnections:\n");
		for (VariableSource vs : this.internalBindings.keySet()) {
			sb.append(indent);
			sb.append("  VariableSource(");
			sb.append(vs.exportingModelURI);
			sb.append(", ");
			sb.append(vs.name);
			sb.append(", ");
			sb.append(vs.type);
			sb.append(") == > [");
			VariableSink[] sinks = this.internalBindings.get(vs);
			for (int i = 0 ; i < sinks.length ; i++) {
				sb.append("VariableSink(");
				sb.append(sinks[i].sinkModelURI);
				sb.append(", ");
				sb.append(sinks[i].importedVariableName);
				sb.append(", ");
				sb.append(sinks[i].importedVariableType);
				sb.append(", ");
				sb.append(sinks[i].sinkVariableName);
				sb.append(", ");
				sb.append(sinks[i].sinkVariableType);
				sb.append(")");
				if (i < sinks.length - 1) {
					sb.append(", ");
				}
			}
			sb.append("]\n");
		}
		sb.append(indent);
		sb.append("---------------------------------------------------\n");
		for (int i = 0 ; i < this.submodels.length ; i++) {
			sb.append(this.submodels[i].modelAsString(indent + "    "));
			if (this.submodels[i] instanceof SimulatorI) {
				sb.append("\n");
			}
			sb.append(indent);
			sb.append("---------------------------------------------------\n");
		}
	}
}
// -----------------------------------------------------------------------------

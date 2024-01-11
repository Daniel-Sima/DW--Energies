package fr.sorbonne_u.devs_simulation.models.architectures;

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

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import fr.sorbonne_u.devs_simulation.models.StandardCoupledModelFactory;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelFactoryI;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;

// -----------------------------------------------------------------------------
/**
 * The class <code>CoupledModelDescriptor</code> defines coupled models in
 * simulation architectures, associating their URI, their static information,
 * as well as an optional factory that can be used to instantiate a coupled
 * model object to run simulations.
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
 * invariant	{@code CoupledModelDescriptor.checkInternalConsistency(this)}
 * </pre>
 * 
 * <p>Created on : 2018-06-22</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			CoupledModelDescriptor
implements	Serializable,
			ModelDescriptorI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long							serialVersionUID = 1L;
	/** class defining the coupled model.									*/
	public final Class<? extends CoupledModelI>			modelClass;
	/** URI of the model to be created.										*/
	public final String									modelURI;
	/** Set of URIs of the submodels of this coupled model.					*/
	public final Set<String>							submodelURIs;
	/** Map from imported event types to the internal sinks importing
	 *  them.																*/
	public final Map<Class<? extends EventI>,EventSink[]>		imported;
	/** Map from event types exported by submodels to event types exported
	 *  by this coupled model.												*/
	public final Map<Class<? extends EventI>,ReexportedEvent>	reexported;
	/** Map connecting submodel exported event types to submodels imported
	 *  ones which will consume them.										*/
	public final Map<EventSource,EventSink[]>			connections;
	/** Coupled model factory allowing to create the model.					*/
	public final CoupledModelFactoryI					cmFactory;
	public final ModelComposer							mc;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a new coupled model creation descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelClass != null || cmFactory != null}
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code submodelURIs != null && submodelURIs.size() > 1}
	 * pre	{@code submodelURIs.stream().allMatch(t -> t != null && !t.isEmpty())}
	 * pre	{@code engineCreationMode != null}
	 * post	{@code CoupledModelDescriptor.checkInternalConsistency(this)}
	 * </pre>
	 *
	 * @param modelClass			class defining the coupled model.
	 * @param modelURI				URI of the coupled model to be created.
	 * @param submodelURIs			URIs of the submodels of the coupled model.
	 * @param imported				map from imported event types to the submodels importing them.
	 * @param reexported			map from event types exported by submodels to exported event types exported by the coupled model.
	 * @param connections			connections between event types exported by submodels to event types imported by other submodels.
	 * @param cmFactory				coupled model factory allowing to create the coupled model or null if none.
	 */
	public				CoupledModelDescriptor(
		Class<? extends CoupledModelI> modelClass,
		String modelURI,
		Set<String> submodelURIs,
		Map<Class<? extends EventI>, EventSink[]> imported,
		Map<Class<? extends EventI>, ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections,
		CoupledModelFactoryI cmFactory
		)
	{
		this(modelClass, modelURI, submodelURIs, imported, reexported,
			 connections, cmFactory, new ModelComposer());
	}

	/**
	 * create a new coupled model creation descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelClass != null || cmFactory != null}
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code submodelURIs != null && submodelURIs.size() > 1}
	 * pre	{@code submodelURIs.stream().allMatch(t -> t != null && !t.isEmpty())}
	 * pre	{@code engineCreationMode != null}
	 * pre	{@code mc != null}
	 * post	{@code CoupledModelDescriptor.checkInternalConsistency(this)}
	 * </pre>
	 *
	 * @param modelClass			class defining the coupled model.
	 * @param modelURI				URI of the coupled model to be created.
	 * @param submodelURIs			URIs of the submodels of the coupled model.
	 * @param imported				map from imported event types to the submodels importing them.
	 * @param reexported			map from event types exported by submodels to exported event types exported by the coupled model.
	 * @param connections			connections between event types exported by submodels to event types imported by other submodels.
	 * @param cmFactory				coupled model factory allowing to create the coupled model or null if none.
	 * @param mc					model composer to be used.
	 */
	public				CoupledModelDescriptor(
		Class<? extends CoupledModelI> modelClass,
		String modelURI,
		Set<String> submodelURIs,
		Map<Class<? extends EventI>, EventSink[]> imported,
		Map<Class<? extends EventI>, ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections,
		CoupledModelFactoryI cmFactory,
		ModelComposer mc
		)
	{
		super();

		assert	modelClass != null || cmFactory != null :
				new AssertionError("Precondition violation: modelClass != null"
											+ " || cmFactory != null");
		assert	modelURI != null && !modelURI.isEmpty() :
				new AssertionError("Precondition violation: modelURI != null");
		assert	submodelURIs != null && submodelURIs.size() > 1 :
				new AssertionError("Precondition violation: "
											+ "submodelURIs != null && "
											+ "submodelURIs.size() > 1");
		assert	submodelURIs.stream().allMatch(t -> t != null && !t.isEmpty()) :
				new AssertionError("Precondition violation: "
								+ "submodelURIs.stream()."
								+ "allMatch(t -> t != null && !t.isEmpty())");
		assert	mc != null :
				new AssertionError("Precondition violation: mc != null");

		this.modelClass = modelClass;
		this.modelURI = modelURI;
		this.submodelURIs = submodelURIs;
		if (imported != null) {
			this.imported = imported;
		} else {
			this.imported = new HashMap<Class<? extends EventI>,EventSink[]>();
		}
		if (reexported != null) {
			this.reexported = reexported;	
		} else {
			this.reexported =
				new HashMap<Class<? extends EventI>,ReexportedEvent>();
		}
		if (connections != null) {
			this.connections = connections;	
		} else {
			this.connections = new HashMap<EventSource,EventSink[]>();
		}
		if (cmFactory == null) {
			this.cmFactory = new StandardCoupledModelFactory(this.modelClass);
		} else {
			this.cmFactory = cmFactory;
		}
		this.mc = mc;

		assert	CoupledModelDescriptor.checkInvariant(this);
	}

	// -------------------------------------------------------------------------
	// Static methods
	// -------------------------------------------------------------------------

	/**
	 * check the invariant for the given coupled model descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code d != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param d	coupled model descriptor to be checked.
	 * @return	true if the descriptor satisfies its invariant.
	 */
	public static boolean	checkInvariant(CoupledModelDescriptor d)
	{
		assert	d != null :
					new AssertionError("Precondition violation: d != null");

		boolean invariant = true;

		invariant &= d.modelClass != null || d.cmFactory != null;
		assert	invariant :
				new AssertionError("Invariant violation: "
								+ "modelClass != null || cmFactory != null");

		invariant &= d.modelURI != null && !d.modelURI.isEmpty();
		assert	invariant :
				new AssertionError("Invariant violation: modelURI != null &&"
								+ "!modelURI.isEmpty(");

		invariant &= d.submodelURIs != null && d.submodelURIs.size() > 1;
		assert	invariant :
				new AssertionError("Invariant violation: "
								+ "submodelURIs != null && "
								+ "submodelURIs.size() > 1");
		invariant &= d.submodelURIs.stream().
								allMatch(t -> t != null && !t.isEmpty());
		assert	invariant :
				new AssertionError("Precondition violation: "
								+ "submodelURIs.stream()."
								+ "allMatch(t -> t != null && !t.isEmpty())");

		invariant &= d.mc != null;
		assert	invariant :
				new AssertionError("Invariant violation: mc != null");

		invariant &= d.imported != null;
		assert	invariant :
				new AssertionError("Invariant violation: imported != null");

		invariant &= d.reexported != null;
		assert	invariant :
				new AssertionError("Invariant violation: "
											+ "reexported != null");

		invariant &= d.connections != null;
		assert	invariant :
				new AssertionError("Invariant violation: "
											+ "connections != null");

		invariant &= CoupledModelDescriptor.checkInternalConsistency(d);
		assert	invariant :
				new AssertionError("Invariant violation: "
											+ "CoupledModelDescriptor."
											+ "checkInternalConsistency(d)");

		return invariant;
	}

	/**
	 * check the internal consistency of the coupled model i.e., when
	 * only the URIs of the submodels are known but not their own
	 * information (imported and exported event types, ...).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code desc != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param desc	coupled model descriptor to be checked.
	 * @return		true if the descriptor satisfies the consistency constraints.
	 */
	public static boolean	checkInternalConsistency(
		CoupledModelDescriptor desc
		)
	{
		assert	desc != null;

		boolean consistent = true;

		// Assert that all imported event sinks correspond to an existing
		// submodel consuming them.
		if (consistent) {
			for (Entry<Class<? extends EventI>,EventSink[]> e :
													desc.imported.entrySet()) {
				EventSink[] es = e.getValue();
				for (int i = 0 ; i < es.length ; i++) {
					consistent &= desc.submodelURIs.contains(
													es[i].importingModelURI);
					assert	consistent :
							new AssertionError(
										"Invariant violation (inconsistency): "
										+ es[i].importingModelURI
										+ "is not a submodel");
				}
			}
		}
		

		// Assert that all reexported event correspond to one existing
		// submodel producing them.
		if (consistent) {
			for (Entry<Class<? extends EventI>,ReexportedEvent> e :
												desc.reexported.entrySet()) {
				ReexportedEvent re = e.getValue();
				consistent &= desc.submodelURIs.contains(re.exportingModelURI);
				assert	consistent :
						new AssertionError(
									"Invariant violation (inconsistency): "
											+ re.exportingModelURI
											+ "is not a submodel");
			}
		}

		// Assert that all connections are among existing submodels.
		if (consistent) {
			for (Entry<EventSource,EventSink[]> e :
												desc.connections.entrySet()) {
				EventSource es = e.getKey();
				consistent &= desc.submodelURIs.contains(es.exportingModelURI);
				assert	consistent :
						new AssertionError(
									"Invariant violation (inconsistency): "
											+ es.exportingModelURI
											+ "is not a submodel");
				EventSink[] sinks = e.getValue();
				for (int i = 0 ; i < sinks.length ; i++) {
					consistent &= desc.submodelURIs.
										contains(sinks[i].importingModelURI);
					assert	consistent :
							new AssertionError(
										"Invariant violation (inconsistency): "
										+ sinks[i].importingModelURI
										+ "is not a submodel");
					consistent &= !sinks[i].importingModelURI.
												equals(es.exportingModelURI);
					assert	consistent :
							new AssertionError(
										"Invariant violation (inconsistency): "
										+ " exporting model "
										+ es.exportingModelURI
										+ " is the same as the importing model "
										+ sinks[i].importingModelURI);
				}
			}
		}

		return consistent;
	}

	/**
	 * check both the internal and the external consistency of the
	 * coupled model i.e., when both the URIs of the submodels are
	 * known and their own information (imported and exported event
	 * types, ...).
	 * 
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code desc != null}
	 * pre	{@code atomicModelDescriptors != null}
	 * pre	{@code coupledModelDescriptors != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param desc						coupled model descriptor to be checked.
	 * @param atomicModelDescriptors	descriptors of the atomic models including the ones that are submodels.
	 * @param coupledModelDescriptors	descriptors of the coupled models including the ones that are submodels.
	 * @return							true if the descriptor satisfies the consistency constraints.
	 */
	public static boolean	checkFullConsistency(
		CoupledModelDescriptor desc,
		Map<String,AtomicModelDescriptor> atomicModelDescriptors,
		Map<String,CoupledModelDescriptor> coupledModelDescriptors
		)
	{
		assert	desc != null;
		assert	atomicModelDescriptors != null;
		assert	coupledModelDescriptors != null;

		boolean ret = CoupledModelDescriptor.checkInternalConsistency(desc);

		for (String uri : desc.submodelURIs) {
			ret &= atomicModelDescriptors.containsKey(uri) ||
									coupledModelDescriptors.containsKey(uri);
		}

		// Assert that all imported event sinks correspond to an
		// existing submodel consuming them.
		for (Entry<Class<? extends EventI>,EventSink[]> e :
													desc.imported.entrySet()) {
			EventSink[] es = e.getValue();
			for (int i = 0 ; i < es.length ; i++) {
				CoupledModelDescriptor.isSubmodelImportingEventType(
											es[i].importingModelURI,
											es[i].sinkEventType,
											atomicModelDescriptors,
											coupledModelDescriptors);
			}
		}
		// Assert that all reexported event correspond to one and only one
		// existing submodel producing them.
		for (Entry<Class<? extends EventI>,ReexportedEvent> e :
												desc.reexported.entrySet()) {
			int found = 0;
			ReexportedEvent re = e.getValue();
			for (String submodelURI : atomicModelDescriptors.keySet()) {
				if (CoupledModelDescriptor.isSubmodelExportingEventType(
						submodelURI,
						re.sourceEventType,
						atomicModelDescriptors,
						coupledModelDescriptors)) {
					found++;
				}
			}
			ret &= (found == 1);
		}
		// Assert that all connections are among existing submodels that
		// export and imports the corresponding events.
		for (Entry<EventSource,EventSink[]> e : desc.connections.entrySet()) {
			EventSource es = e.getKey();
			ret &= CoupledModelDescriptor.isSubmodelExportingEventType(
												es.exportingModelURI,
												es.sourceEventType,
												atomicModelDescriptors,
												coupledModelDescriptors);
			EventSink[] sinks = e.getValue();
			for (int i = 0 ; i < sinks.length ; i++) {
				ret &= !sinks[i].importingModelURI.
											equals(es.exportingModelURI);
				ret &= CoupledModelDescriptor.isSubmodelImportingEventType(
												sinks[i].importingModelURI,
												sinks[i].sinkEventType,
												atomicModelDescriptors,
												coupledModelDescriptors);
			}
		}
		return ret;
	}

	/**
	 * return true if the given submodel imports the given event type.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code submodelURI != null && !submodelURI.isEmpty()}
	 * pre	{@code et != null}
	 * pre	{@code atomicModelDescriptors != null}
	 * pre	{@code coupledModelDescriptors != null}
	 * pre	{@code atomicModelDescriptors.containsKey(submodelURI) || coupledModelDescriptors.containsKey(submodelURI)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param submodelURI				URI of the submodel to be checked.
	 * @param et						event type to be checked.
	 * @param atomicModelDescriptors	atomic model descriptors.
	 * @param coupledModelDescriptors	coupled model descriptors.
	 * @return							true if the given submodel imports the given event type.
	 */
	protected static boolean	isSubmodelImportingEventType(
		String submodelURI,
		Class<? extends EventI> et,
		Map<String,AtomicModelDescriptor> atomicModelDescriptors,
		Map<String,CoupledModelDescriptor> coupledModelDescriptors
		)
	{
		assert	submodelURI != null && !submodelURI.isEmpty():
			new AssertionError("Precondition violation: "
							+ "submodelURI != null && !submodelURI.isEmpty()");
		assert	et != null:
				new AssertionError("Precondition violation: et != null");
		assert	atomicModelDescriptors != null:
				new AssertionError("Precondition violation: "
										+ "atomicModelDescriptors != null");
		assert	coupledModelDescriptors != null:
				new AssertionError("Precondition violation: "
										+ "coupledModelDescriptors != null");
		assert	atomicModelDescriptors.containsKey(submodelURI) ||
					coupledModelDescriptors.containsKey(submodelURI):
				new AssertionError("Precondition violation: "
						+ "atomicModelDescriptors.containsKey(submodelURI) || "
						+ "coupledModelDescriptors.containsKey(submodelURI)");

		boolean ret = false;
		if (atomicModelDescriptors.containsKey(submodelURI)) {
			Class<? extends EventI>[] importedEvents =
					atomicModelDescriptors.get(submodelURI).importedEvents;
			for (int i = 0 ; !ret && i < importedEvents.length ; i++) {
				ret = et.equals(importedEvents[i]);
			}
		} else {
			assert	coupledModelDescriptors.containsKey(submodelURI);
			ret = coupledModelDescriptors.get(submodelURI).
											  imported.keySet().contains(et);
		}
		return ret;
	}

	/**
	 * return true if the given submodel exports the given event type.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code submodelURI != null && !submodelURI.isEmpty()}
	 * pre	{@code et != null}
	 * pre	{@code atomicModelDescriptors != null}
	 * pre	{@code coupledModelDescriptors != null}
	 * pre	{@code atomicModelDescriptors.containsKey(submodelURI) || coupledModelDescriptors.containsKey(submodelURI)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param submodelURI				URI of the submodel to be checked.
	 * @param et						event type to be checked.
	 * @param atomicModelDescriptors	atomic model descriptors.
	 * @param coupledModelDescriptors	coupled model descriptors.
	 * @return							true if the given submodel exports the given event type.
	 */
	protected static boolean	isSubmodelExportingEventType(
		String submodelURI,
		Class<? extends EventI> et,
		Map<String,AtomicModelDescriptor> atomicModelDescriptors,
		Map<String,CoupledModelDescriptor> coupledModelDescriptors
		)
	{
		assert	submodelURI != null && !submodelURI.isEmpty():
				new AssertionError("Precondition violation: "
							+ "submodelURI != null && !submodelURI.isEmpty()");
		assert	et != null:
				new AssertionError("Precondition violation: et != null");
		assert	atomicModelDescriptors != null:
				new AssertionError("Precondition violation: "
							+ "atomicModelDescriptors != null");
		assert	coupledModelDescriptors != null:
				new AssertionError("Precondition violation: "
							+ "coupledModelDescriptors != null");
		assert	atomicModelDescriptors.containsKey(submodelURI) ||
						coupledModelDescriptors.containsKey(submodelURI):
				new AssertionError("Precondition violation: "
						+ "atomicModelDescriptors.containsKey(submodelURI) || "
						+ "coupledModelDescriptors.containsKey(submodelURI)");

		boolean ret = false;
		if (atomicModelDescriptors.containsKey(submodelURI)) {
			Class<? extends EventI>[] exportedEvents =
					atomicModelDescriptors.get(submodelURI).exportedEvents;
			for (int i = 0 ; !ret && i < exportedEvents.length ; i++) {
				ret = et.equals(exportedEvents[i]);
			}
		} else {
			assert	coupledModelDescriptors.containsKey(submodelURI);
			for (ReexportedEvent re : coupledModelDescriptors.get(submodelURI).
													reexported.values()) {
				ret = et.equals(re.sinkEventType);
				if (ret){
					break;
				}
			}
		}
		return ret;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.architectures.ModelDescriptorI#getModelURI()
	 */
	@Override
	public String		getModelURI()
	{
		return this.modelURI;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.architectures.ModelDescriptorI#isCoupledModelDescriptor()
	 */
	@Override
	public boolean		isCoupledModelDescriptor()
	{
		return true;
	}

	/**
	 * return true if all predefined submodels are defined in {@code models}
	 * and no more than them.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code models != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param models	array of submodels to be tested.
	 * @return			true if all predefined submodels are defined in {@code models} and no more than them.
	 */
	public boolean		definesExpectedSubmodels(ModelI[] models)
	{
		assert	models != null :
				new AssertionError("Precondition violation: models != null");
		boolean ret = true;
		HashSet<String> hs = new HashSet<String>();
		for (int i = 0 ; ret && i < models.length ; i++) {
			ret = this.submodelURIs.contains(models[i].getURI());
			hs.add(models[i].getURI());
		}
		if (ret) {
			for (String uri : this.submodelURIs) {
				ret = hs.contains(uri);
				if (!ret) {
					break;
				}
			}
		}
		return ret;
	}

	/**
	 * create a coupled model from this descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code models != null && models.length > 1}
	 * pre	{@code }
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param models	model descriptions for the submodels of this coupled model.
	 * @return			the new coupled model as a model description.
	 */
	public CoupledModelI	createCoupledModel(ModelI[] models)
	{
		assert	models != null && models.length > 1 :
				new AssertionError("Precondition violation: "
									+ "models != null && models.length > 1");
		assert	this.definesExpectedSubmodels(models) :
				new AssertionError(
					"Precondition violation: definesExpectedSubmodels(models)");

		return this.mc.compose(models,
							   this.modelURI,
							   cmFactory.createCoordinationEngine(),
							   cmFactory,
							   this.imported,
							   this.reexported,
							   this.connections);
	}
}
// -----------------------------------------------------------------------------

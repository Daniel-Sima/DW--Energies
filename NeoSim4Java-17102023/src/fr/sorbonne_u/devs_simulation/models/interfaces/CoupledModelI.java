package fr.sorbonne_u.devs_simulation.models.interfaces;

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

import java.util.Set;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;

// -----------------------------------------------------------------------------
/**
 * The interface <code>CoupledModelI</code> defines the most generic methods
 * to be implemented by DEVS atomic simulation models.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2016-03-25</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		CoupledModelI
extends		ModelI
{
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#isAtomic()
	 */
	@Override
	default boolean		isAtomic() { return false; }

	/**
	 * return true if <code>uri</code> is the URI of a submodel of this
	 * coupled model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri	URI of the model to be tested.
	 * @return		true if <code>uri</code> is a submodel of this coupled model.
	 */
	public boolean		isSubmodel(String uri);

	/**
	 * return true if <code>uri</code> is the URI of a descendant model of
	 * this (coupled) model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri			a model URI.
	 * @return				true if <code>uri</code> is the URI of a descendant model of this coupled model.
	 */
	public boolean		isDescendant(String uri);

	/**
	 * get a Java reference on the object representing a model that is a
	 * descendant or the model executing the method.
	 * 
	 * <p>Description</p>
	 * 
	 * As this method returns a reference on a Java object, it must be used
	 * with much care. Models are not callable through RMI, hence it should
	 * not be called through RMI (i.e., among components). Also, direct access
	 * to the object representing a model should be read-only, as changing the
	 * state of the model can interfere severely with the simulation protocol.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri	an URI for which this model or a submodel is sought.
	 * @return		the model with the corresponding URI or null if none exists.
	 */
	public ModelI		getDescendant(String uri);

//	/**
//	 * return a description of the atomic model source of the type of events,
//	 * perhaps after conversion to the type <code>ce</code>.
//	 * 
//	 * <p><strong>Contract</strong></p>
//	 * 
//	 * <pre>
//	 * pre	{@code ce != null && this.isExportedEventType(ce)}
//	 * post	{@code true}	// no postcondition.
//	 * </pre>
//	 *
//	 * @param ce	a type of events exported by this coupled model.
//	 * @return		the atomic model source of the event, perhaps after conversion to <code>ce</code>.
//	 */
//	public EventSource	getEventSource(Class<? extends EventI> ce);

	/**
	 * return the descriptions of the submodels of this coupled model
	 * that must receive the events of type <code>ce</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code ce != null && this.isImportedEventType(ce)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param ce	an imported type of event.
	 * @return		a set of atomic model sinks that must receive the events of type <code>ce</code>.
	 */
	public Set<EventSink>	getEventSinks(Class<? extends EventI> ce);

	/**
	 * return the description of the type of events exported by a submodel of$
	 * this coupled model which events are reexported by the coupled model as
	 * events of type <code>ce</code>, perhaps after a conversion.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code ce != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param ce	event type exported by one of the submodels of this coupled model.
	 * @return		the description of the reexported event.
	 */
	public ReexportedEvent	getReexportedEvent(Class<? extends EventI> ce);

	/**
	 * when a coupled model is performing an internal step and more than
	 * one submodel tie for the next internal step, return the URI of the
	 * model that should do so, breaking the tie.
	 * 
	 * <p>Description</p>
	 * 
	 * <p>
	 * Users must implement this method when their coupled models can exhibit
	 * such tied internal transitions, otherwise an implementation performing
	 * either a random selection for non HIOA models or selecting a HIOA
	 * model having no dependencies is provided by the abstract class.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code candidates != null && candidates.length > 1}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param candidates	URIs of the candidates submodels for the next internal transition.
	 * @return				the URI of model that will perform its internal transition, breaking the tie.
	 */
	public String		select(String[] candidates);
}
// -----------------------------------------------------------------------------

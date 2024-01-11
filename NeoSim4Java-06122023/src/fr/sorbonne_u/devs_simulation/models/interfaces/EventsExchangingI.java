package fr.sorbonne_u.devs_simulation.models.interfaces;

import java.util.ArrayList;

import fr.sorbonne_u.devs_simulation.models.events.EventI;

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

// -----------------------------------------------------------------------------
/**
 * The interface <code>EventsExchangingI</code> declares the method used to
 * exchange events among atomic models.
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
 * <p>Created on : 2023-11-02</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		EventsExchangingI
{
	/**
	 * store external events imported from another simulation model.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * The DEVS protocol proposes two ways to propagate events among models.
	 * The first way is to return the produced events to the parent coordination
	 * engine which passes them to a sibling or return them to its own parent
	 * coordination engine.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code destinationURI != null && !destinationURI.isEmpty()}
	 * pre	{@code getURI().equals(destinationURI)}
	 * pre	{@code es != null && !es.isEmpty()}
	 * pre	{@code es.stream().allMatch(e -> isImportedEventType(e.getClass())}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param destinationURI	URI of the destination model.
	 * @param es				imported external events to be stored.
	 */
	public void			storeInput(String destinationURI, ArrayList<EventI> es);
}
// -----------------------------------------------------------------------------

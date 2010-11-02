package edu.uci.lighthouse.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.widgets.Display;

import edu.uci.lighthouse.model.io.IPersistable;


/**
 * Represents a Lighthouse Model, in which it has:
 * 
 * - List of Entities (from LighthouseAbstractModel)
 * - List of Relationships (from LighthouseAbstractModel)
 * - List of Events
 * 
 * */
public class LighthouseModel extends LighthouseAbstractModel implements IPersistable {

	// Do not change this.
	private static final long serialVersionUID = -6504942647992239589L;

	private static Logger logger = Logger.getLogger(LighthouseModel.class);

	private static LighthouseModel instance;

	/** Associate an Artifact(Entity or Relationship) with a list of events. */
	private HashMap<Object, LinkedHashSet<LighthouseEvent>> mapArtifactEvents = new HashMap<Object, LinkedHashSet<LighthouseEvent>>();

	/** List of all events*/
	private LinkedHashSet<LighthouseEvent> listEvents = new LinkedHashSet<LighthouseEvent>();

	private HashMap<LighthouseClass, LinkedHashSet<LighthouseClass>> classRelationships = new HashMap<LighthouseClass, LinkedHashSet<LighthouseClass>>();
	
	protected LighthouseModel() {
	}

	public static synchronized LighthouseModel getInstance() {
		if (instance == null) {
			instance = new LighthouseModel();
		}
		return instance;
	}
	
	public synchronized void clear() {
		super.clear();
		mapArtifactEvents.clear();
		listEvents.clear();
		classRelationships.clear();
	}
	
	/**
	 * Used to create a shallow copy
	 * @param model
	 */
	protected synchronized void assignTo(LighthouseModel model){
		super.assignTo(model);
		mapArtifactEvents = model.mapArtifactEvents;
		listEvents = model.listEvents;
		classRelationships = model.classRelationships;
	}

	@Override
	protected synchronized void addRelationship(LighthouseRelationship rel) {
		super.addRelationship(rel);
		LighthouseModelManager manager = new LighthouseModelManager(this);
		LighthouseClass fromClass = manager.getMyClass(rel.getFromEntity());
		LighthouseClass toClass = manager.getMyClass(rel.getToEntity());
		if (fromClass != null && toClass != null && !fromClass.equals(toClass)) {
			LinkedHashSet<LighthouseClass> listRelationships = classRelationships.get(fromClass);
			if (listRelationships == null){
				listRelationships = new LinkedHashSet<LighthouseClass>();
				classRelationships.put(fromClass, listRelationships);
			}
			listRelationships.add(toClass);
		}
	}
	
	//FIXME: Find a better method name
	public Collection<LighthouseClass> getConnectTo(LighthouseClass aClass){
		LinkedHashSet<LighthouseClass> list = classRelationships.get(aClass);
		return list != null ? list : new LinkedList<LighthouseClass>();
	}

	@Override
	protected synchronized void removeRelationship(LighthouseRelationship rel) {
		super.removeRelationship(rel);
		LighthouseModelManager manager = new LighthouseModelManager(this);
		LighthouseClass fromClass = manager.getMyClass(rel.getFromEntity());
		LighthouseClass toClass = manager.getMyClass(rel.getToEntity());
		if (fromClass != null && toClass != null && !fromClass.equals(toClass) ) {
			LinkedHashSet<LighthouseClass> listRelationships = classRelationships.get(fromClass);
			if (listRelationships != null){
				listRelationships.remove(toClass);
			}
		}
	}

	final synchronized void addEvent(LighthouseEvent event) {
		Object artifact = event.getArtifact();
		if (artifact!=null) {
			LinkedHashSet<LighthouseEvent> listArtifactEvents = mapArtifactEvents.get(artifact);
			if (listArtifactEvents==null) {
				listArtifactEvents = new LinkedHashSet<LighthouseEvent>();
				mapArtifactEvents.put(artifact,listArtifactEvents);
			}
			if (listArtifactEvents.contains(event)){
				for(LighthouseEvent evt: listArtifactEvents){
					if (evt.equals(event)){
						evt.setCommitted(event.isCommitted());
						evt.setCommittedTime(event.getCommittedTime());
					}
				}
			} else {
				listArtifactEvents.add(event);
				listEvents.add(event);
			}
		} else {
			logger.warn("Artifact is null: " + event.toString());
		}
	}

	final synchronized void removeEvent(LighthouseEvent event) {
		listEvents.remove(event);
		Object artifact = event.getArtifact();
		if (artifact!=null) {
			mapArtifactEvents.remove(artifact);
		} else {
			logger.warn("Artifact is null: " + event.toString());
		}
	}

	final synchronized void removeEventAndArtifact(LighthouseEvent event) {
		removeEvent(event);
		Object artifact = event.getArtifact();
		if (artifact instanceof LighthouseEntity) {
			removeEntity((LighthouseEntity) artifact);
		} else if (artifact instanceof LighthouseRelationship) {
			removeRelationship((LighthouseRelationship) artifact);
		}
	}

	/**
	 * Get events related with a given Artifact
	 * 
	 * @param artifact
	 * 		{@link LighthouseEntity}
	 * 		OR
	 * 		{@link LighthouseRelationship} 	
	 * */
	public Collection<LighthouseEvent> getEvents(Object artifact){
		LinkedHashSet<LighthouseEvent> result = mapArtifactEvents.get(artifact);
		return result != null ? result : new LinkedHashSet<LighthouseEvent>();
	}

	public LinkedHashSet<LighthouseEvent> getListEvents() {
		return listEvents;
	}

	// Handle Listeners...

	private transient List<ILighthouseModelListener> listeners = new ArrayList<ILighthouseModelListener>();

	public void addModelListener(ILighthouseModelListener listener) {
		listeners.add(listener);
	}

	public void removeModelListener(ILighthouseModelListener listener) {
		listeners.remove(listener);
	}

	public void fireModelChanged() {
		for (final ILighthouseModelListener l : listeners) {
			if (l instanceof ILighthouseUIModelListener) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						l.modelChanged();
					}
				});
			} else {
				l.modelChanged();
			}
		}
	}

	public void fireClassChanged(final LighthouseClass c,
			final LighthouseEvent.TYPE type) {
		for (final ILighthouseModelListener l : listeners) {
			if (l instanceof ILighthouseUIModelListener) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						l.classChanged(c, type);
					}
				});
			} else {
				l.classChanged(c, type);
			}
		}
	}

	public void fireRelationshipChanged(final LighthouseRelationship r,
			final LighthouseEvent.TYPE type) {
		for (final ILighthouseModelListener l : listeners) {
			if (l instanceof ILighthouseUIModelListener) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						l.relationshipChanged(r, type);
					}
				});
			} else {
				l.relationshipChanged(r, type);
			}
		}
	}

	public boolean isEmpty(){
		return (getListEvents().size()== 0
				&& getEntities().size() == 0
				&& getRelationships().size() == 0);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((classRelationships == null) ? 0 : classRelationships
						.hashCode());
		result = prime * result
				+ ((listEvents == null) ? 0 : listEvents.hashCode());
		result = prime
				* result
				+ ((mapArtifactEvents == null) ? 0 : mapArtifactEvents
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		LighthouseModel other = (LighthouseModel) obj;
		if (classRelationships == null) {
			if (other.classRelationships != null)
				return false;
		} else if (!classRelationships.equals(other.classRelationships))
			return false;
		if (listEvents == null) {
			if (other.listEvents != null)
				return false;
		} else if (!listEvents.equals(other.listEvents))
			return false;
		if (mapArtifactEvents == null) {
			if (other.mapArtifactEvents != null)
				return false;
		} else if (!mapArtifactEvents.equals(other.mapArtifactEvents))
			return false;
		return super.equals(obj);
	}

	@Override
	public String getFileName() {
		String metadataDir = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString()  + "/.metadata/";
		return metadataDir + "lighthouse-model.bin";
	}

}

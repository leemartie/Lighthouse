package edu.uci.lighthouse.model.jpa;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import edu.uci.lighthouse.model.LighthouseAuthor;
import edu.uci.lighthouse.model.LighthouseEntity;
import edu.uci.lighthouse.model.LighthouseEvent;
import edu.uci.lighthouse.model.LighthouseRelationship;
import edu.uci.lighthouse.model.LighthouseEvent.TYPE;

public class LHEventDAO extends AbstractDAO<LighthouseEvent, Integer> {
	
	public List<LighthouseEvent> executeQueryCheckOut(
			LinkedHashSet<LighthouseEntity> listEntitiesInside,
			Date revisionTime) {
		List<LighthouseEvent> result = new LinkedList<LighthouseEvent>();
		if (listEntitiesInside.size()>0) {			
			String strQuery = "SELECT e " + "FROM LighthouseEvent e "
			+ "WHERE ";
			strQuery += " ( ";
			// Get all entities' events
			for (LighthouseEntity entity : listEntitiesInside) {
				String fqn = entity.getFullyQualifiedName();
				fqn = fqn.replaceAll("\\,", "\\\\,");
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String strTimestamp = formatter.format(revisionTime);
				strQuery += " ( ( ";
				strQuery += "e.entity" + " = " + "'" + fqn + "'";
				strQuery += " AND ";
				strQuery += "e.timestamp" + " >= " + "'" + strTimestamp + "'";
				strQuery += " ) OR ( ( ";
				strQuery += "e.entity" + " = " + "'" + fqn + "'";
				strQuery += " AND ";
				strQuery += "e.isCommitted" + " = " + true;
				strQuery += " AND ";
				strQuery += "e.committedTime" + " <= " + "'" + strTimestamp + "'";
				strQuery += " ) AND ( ";
				strQuery += "e.type" + " = " + TYPE.ADD.ordinal();
				strQuery += " OR ";
				strQuery += "e.type" + " = " + TYPE.REMOVE.ordinal();
				strQuery += " ) ) ) ";
				strQuery += "OR ";
			}
			// Get all relationships' events
			for (LighthouseEntity entity : listEntitiesInside) {
				String fqn = entity.getFullyQualifiedName();
				fqn = fqn.replaceAll("\\,", "\\\\,");
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String strTimestamp = formatter.format(revisionTime);
				strQuery += " ( ( ";
				strQuery += " ( ";
				strQuery += "e.relationship.primaryKey.from" + " = " + "'" + fqn + "'";
				strQuery += " OR ";
				strQuery += "e.relationship.primaryKey.to" + " = " + "'" + fqn + "'";
				strQuery += " ) ";
				strQuery += " AND ";
				strQuery += "e.timestamp" + " >= " + "'" + strTimestamp + "'";
				strQuery += " ) OR ( ( ";
				strQuery += " ( ";
				strQuery += "e.relationship.primaryKey.from" + " = " + "'" + fqn + "'";
				strQuery += " OR ";
				strQuery += "e.relationship.primaryKey.to" + " = " + "'" + fqn + "'";
				strQuery += " ) ";
				strQuery += " AND ";
				strQuery += "e.isCommitted" + " = " + true;
				strQuery += " AND ";
				strQuery += "e.committedTime" + " <= " + "'" + strTimestamp + "'";
				strQuery += " ) AND ( ";
				strQuery += "e.type" + " = " + TYPE.ADD.ordinal();
				strQuery += " OR ";
				strQuery += "e.type" + " = " + TYPE.REMOVE.ordinal();
				strQuery += " ) ) ) ";
				strQuery += "OR ";
			}
			strQuery = strQuery.substring(0, strQuery.lastIndexOf("OR"));
			strQuery += " )";
			List<LighthouseEvent> queryResult = executeDynamicQuery(strQuery);
			result = (queryResult!=null) ? queryResult : result;
		}
		return result;
	}
	
	public List<LighthouseEvent> executeQueryLhBaseFile(
			LinkedHashSet<LighthouseEntity> listEntitiesInside,
			Date revisionTime, LighthouseAuthor author) {
		List<LighthouseEvent> result = new LinkedList<LighthouseEvent>();
		if (listEntitiesInside.size()>0) {			
			String strQuery = "SELECT e " + "FROM LighthouseEvent e "
			+ "WHERE ";
			strQuery += " ( ";
			// Get all entities' events
			for (LighthouseEntity entity : listEntitiesInside) {
				String fqn = entity.getFullyQualifiedName();
				fqn = fqn.replaceAll("\\,", "\\\\,");
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String strTimestamp = formatter.format(revisionTime);
				strQuery += " ( ( ";
				strQuery += "e.entity" + " = " + "'" + fqn + "'";
				strQuery += " AND ";
				strQuery += "e.timestamp" + " >= " + "'" + strTimestamp + "'";
				strQuery += " AND ";
				strQuery += "e.author" + " = " + "'" + author + "'";
				strQuery += " AND ";
				strQuery += "e.type" + " = " + "'" + TYPE.ADD.ordinal() + "'";
				strQuery += " ) OR ( ( ";
				strQuery += "e.entity" + " = " + "'" + fqn + "'";
				strQuery += " AND ";
				strQuery += "e.isCommitted" + " = " + true;
				strQuery += " AND ";
				strQuery += "e.committedTime" + " <= " + "'" + strTimestamp + "'";
				strQuery += " ) AND ( ";
				strQuery += "e.type" + " = " + TYPE.ADD.ordinal();
				strQuery += " OR ";
				strQuery += "e.type" + " = " + TYPE.REMOVE.ordinal();
				strQuery += " ) ) ) ";
				strQuery += "OR ";
			}
			// Get all relationships' events
			for (LighthouseEntity entity : listEntitiesInside) {
				String fqn = entity.getFullyQualifiedName();
				fqn = fqn.replaceAll("\\,", "\\\\,");
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String strTimestamp = formatter.format(revisionTime);
				strQuery += " ( ( ";
				strQuery += " ( ";
				strQuery += "e.relationship.primaryKey.from" + " = " + "'" + fqn + "'";
				strQuery += " OR ";
				strQuery += "e.relationship.primaryKey.to" + " = " + "'" + fqn + "'";
				strQuery += " ) ";
				strQuery += " AND ";
				strQuery += "e.timestamp" + " >= " + "'" + strTimestamp + "'";
				strQuery += " AND ";
				strQuery += "e.author" + " = " + "'" + author + "'";
				strQuery += " AND ";
				strQuery += "e.type" + " = " + "'" + TYPE.ADD.ordinal() + "'";
				strQuery += " ) OR ( ( ";
				strQuery += " ( ";
				strQuery += "e.relationship.primaryKey.from" + " = " + "'" + fqn + "'";
				strQuery += " OR ";
				strQuery += "e.relationship.primaryKey.to" + " = " + "'" + fqn + "'";
				strQuery += " ) ";
				strQuery += " AND ";
				strQuery += "e.isCommitted" + " = " + true;
				strQuery += " AND ";
				strQuery += "e.committedTime" + " <= " + "'" + strTimestamp + "'";
				strQuery += " ) AND ( ";
				strQuery += "e.type" + " = " + TYPE.ADD.ordinal();
				strQuery += " OR ";
				strQuery += "e.type" + " = " + TYPE.REMOVE.ordinal();
				strQuery += " ) ) ) ";
				strQuery += "OR ";
			}
			strQuery = strQuery.substring(0, strQuery.lastIndexOf("OR"));
			strQuery += " )";
			List<LighthouseEvent> queryResult = executeDynamicQuery(strQuery);
			result = (queryResult!=null) ? queryResult : result;
		}
		return result;
	}

	public void updateCommittedEvents(LinkedHashSet<LighthouseEvent> listEventsToCommitt, Date svnCommittedTime) throws JPAUtilityException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strCommittedTime = formatter.format(svnCommittedTime);
		String command = 	"UPDATE LighthouseEvent e " +
							"SET e.isCommitted = 1 , " +
							"e.committedTime = '" + strCommittedTime + "' " +
							"WHERE ";
		command += " ( ";
		for (LighthouseEvent event : listEventsToCommitt) {
			Object artifact = event.getArtifact();
			if (artifact instanceof LighthouseEntity) {
				LighthouseEntity entity = (LighthouseEntity) artifact;
				command += "e.entity.fullyQualifiedName = '" + entity.getFullyQualifiedName() + "' ";
			} else if (artifact instanceof LighthouseRelationship) {
				command += " ( ";
				LighthouseRelationship rel = (LighthouseRelationship) artifact;
				command+= "e.relationship.primaryKey.from = " + "'" + rel.getFromEntity().getFullyQualifiedName() + "' AND ";
				command+= "e.relationship.primaryKey.to = " + "'" + rel.getToEntity().getFullyQualifiedName() + "' AND ";
				command+= "e.relationship.primaryKey.type = " + "'" + rel.getType().ordinal() + "' ";
				command += " )";
			}
			command+= " OR ";
		}
		command = command.substring(0, command.lastIndexOf("OR"));
		command += " )";
		executeUpdateQuery(command);
	}
	
}

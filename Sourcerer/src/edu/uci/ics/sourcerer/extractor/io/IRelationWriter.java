/*
* Sourcerer: an infrastructure for large-scale source code analysis.
* Copyright (C) by contributors. See CONTRIBUTORS.txt for full list.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package edu.uci.ics.sourcerer.extractor.io;

public interface IRelationWriter extends IExtractorWriter {

  public void writeInside(String innerFqn, String outerFqn);

  public void writeExtends(String subTypeFqn, String superTypeFqn);

  public void writeImplements(String subTypeFqn, String superTypeFqn);

  public void writeReceives(String fqn, String paramType, String paramName, int position);

  public void writeThrows(String fqn, String exceptionType);

  public void writeReturns(String fqn, String returnType);

  public void writeHolds(String fqn, String type);

  public void writeUses(String fqn, String type);

  public void writeCalls(String caller, String callee);

  public void writeAccesses(String accessor, String field);

  public void writeAnnotates(String entity, String annotation);

  public void writeParametrizedBy(String fqn, String typeVariable, int pos);

}
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
package edu.uci.ics.sourcerer.repo;

import java.io.File;
import java.util.Collection;

import edu.uci.ics.sourcerer.util.Helper;

public class RepoProject {
  private Collection<File> paths;
  private String batch;
  private String checkout;
  
  public RepoProject(String batch, String checkout) {
    paths = Helper.newLinkedList();
    this.batch = batch;
    this.checkout = checkout;
  }
  
//  public RepoProject(File path, String batch, String checkout) {
//    this(batch, checkout);
//    paths.add(path);
//  }
  
  public void addPath(File path) {
    paths.add(path);
  }
  
  public Collection<File> getPaths() {
    return paths;
  }
  
  public String getInputPath() {
    StringBuffer inputPath = new StringBuffer();
    for (File path : paths) {
      if (inputPath.length() > 0) {
        inputPath.append(';');
      }
      inputPath.append(path.getPath());
    }
    return inputPath.toString();
  }
  
  public String getOutputPath(String baseOutput) {
    return baseOutput + File.separatorChar + getProject();
  }
  
  public String getProject() {
    return batch + File.separatorChar + checkout;
  }
}

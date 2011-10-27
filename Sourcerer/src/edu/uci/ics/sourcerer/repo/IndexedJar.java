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
import java.util.Map;

import edu.uci.ics.sourcerer.util.Counter;
import edu.uci.ics.sourcerer.util.Helper;

public class IndexedJar {
  private File path;
  private Map<String, Counter<?>> names;
  private int totalCount;
  
  public IndexedJar(String path, String totalCount) {
    this.path = new File(path);
    this.totalCount = Integer.parseInt(totalCount);
  }
  
  public IndexedJar(File path) {
    this.path = path;
    names = Helper.newHashMap();
    totalCount = 0;
  }
  
  public void addName(String name) {
    totalCount++;
    Counter<?> counter = names.get(name);
    if (counter == null) {
      counter = new Counter<String>();
      names.put(name, counter);
    }
    counter.increment();
  }
  
  public void rename() {
    String jarName = null;
    int best = 0;
    if (names.size() == 1) {
      jarName = names.keySet().iterator().next();
    } else {
      // use the most popular
      for (Map.Entry<String, Counter<?>> name : names.entrySet()) {
        if (name.getValue().getCount() > best) {
          best = name.getValue().getCount();
          jarName = name.getKey();
        }
      }
    }
    
    jarName = jarName.replace(' ', '_');
    
    // Rename the jar
    File newPath = new File(path.getParent(), jarName);
    if (newPath.exists()) {
      String name = jarName.substring(0, jarName.lastIndexOf('.'));
      int count = 0;
      while (newPath.exists()) {
        newPath = new File(path.getParent(), name + "-" + ++count + ".jar");
      }
    }
    path.renameTo(newPath);
    path = newPath;
  }
  
  public String getNamePopularityInfo() {
    if (names.size() == 1) {
      return names.keySet().iterator().next() + " 1 1";
    } else {
      String jarName = null;
      int best = 0;
      // use the most popular
      for (Map.Entry<String, Counter<?>> name : names.entrySet()) {
        if (name.getValue().getCount() > best) {
          best = name.getValue().getCount();
          jarName = name.getKey();
        }
      }
      
      // Check how many match
      int match = 0;
      String matchName = RepoTools.getUnionName(jarName);
      for (String name : names.keySet()) {
        if (matchName.equals(RepoTools.getUnionName(name))) {
          match++;
        }
      }
      
      return jarName + " " + match + " " + names.size();
    }
  }
  
  public String getPath() {
    return path.getPath();
  }
  
  public String getName() {
    return path.getName();
  }
  
  public int getTotalCount() {
    return totalCount;
  }
}
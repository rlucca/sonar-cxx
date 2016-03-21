/*
 * Sonar C++ Plugin (Community)
 * Copyright (C) 2010 Neticoa SAS France
 * sonarqube@googlegroups.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.cxx.compiler;

import java.io.File;
import java.util.List;
import java.util.Stack;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.MatchResult;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;

import org.sonar.plugins.cxx.utils.CxxUtils;

/**
 * {@inheritDoc}
 */
public class CxxCompilerGccMakeParser implements CompilerParser {

  public static final String KEY = "GCC-MAKE";
  // search for single line with compiler warning message - order for groups: 1 = file, 2 = line, 3 = message, 4=id
  public static final String DEFAULT_REGEX_DEF = "^(.*):(?:(?:([0-9]+):[0-9]+: warning: (.*)\\[(.*)\\])|(?:\\s*(Entering|Leaving)\\s+\\w*\\s+[\"`'](.*?)[\"`']))$";
  // ToDo: as long as java 7 API is not used the support of named groups for regular expression is not possible
  // sample regex: "^.*[\\\\,/](?<filename>.*)\\((?<line>[0-9]+)\\)\\x20:\\x20warning\\x20(?<id>C\\d\\d\\d\\d):(?<message>.*)$";
  // get value with e.g. scanner.match().group("filename");
  public static final String DEFAULT_CHARSET_DEF = "UTF-8";

  /**
   * {@inheritDoc}
   */
  @Override
  public String key() {
    return KEY;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String rulesRepositoryKey() {
    return CxxCompilerGccRuleRepository.KEY;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String defaultRegexp() {
    return DEFAULT_REGEX_DEF;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String defaultCharset() {
    return DEFAULT_CHARSET_DEF;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void processReport(final Project project, final SensorContext context, File report, String charset, String reportRegEx, List<Warning> warnings) throws java.io.FileNotFoundException {
    CxxUtils.LOG.info("Parsing 'GCC-MAKE' format");

    Scanner scanner = new Scanner(report, charset);
    Pattern p = Pattern.compile(reportRegEx, Pattern.MULTILINE);
    CxxUtils.LOG.debug("Using pattern : '{}'", p);
    Stack<String> dirpath = new Stack<String>();
    MatchResult matchres = null;
    while (scanner.findWithinHorizon(p, 0) != null) {
      matchres = scanner.match();
      String type = matchres.group(5);

      if (type != null) {
        if (type.equals("Entering")) {
          String path = matchres.group(6);
          dirpath.push(path);
          CxxUtils.LOG.debug("Push path '{}'", path);
        } else if (type.equals("Leaving")) {
          String path = matchres.group(6);
          dirpath.pop();
          // Maybe throw exception if it is not equal?
          CxxUtils.LOG.debug("Pop path '{}'", path);
        }
      } else {
        String filename = CxxUtils.normalizePathFull(matchres.group(1), dirpath.peek());
        String line = matchres.group(2);
        String msg = matchres.group(3);
        String id = matchres.group(4).replaceAll("=$", "");
        CxxUtils.LOG.debug("Scanner-matches file='{}' line='{}' id='{}' msg={}",
          new Object[]{filename, line, id, msg});
        warnings.add(new Warning(filename, line, id, msg));
      }
    }

    scanner.close();
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
/*
 * Sonar C++ Plugin (Community)
 * Copyright (C) 2011 Waleri Enns and CONTACT Software GmbH
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
package org.sonar.cxx.checks;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.cxx.api.CxxMetric;
import org.sonar.squidbridge.checks.ChecksHelper;
import org.sonar.squidbridge.checks.SquidCheck;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;
import org.sonar.cxx.tag.Tag;

@Rule(
  key = "TooManyLinesOfCodeInFile",
  name = "Avoid too many code lines in source file",
  tags = {Tag.BRAIN_OVERLOAD},
  priority = Priority.MAJOR)
@ActivatedByDefault
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.READABILITY)
@SqaleConstantRemediation("1h")
//similar Vera++ rule L006 "Source file is too long"
public class TooManyLinesOfCodeInFileCheck extends SquidCheck<Grammar> {

  private static final int DEFAULT_MAXIMUM = 2000;

  @RuleProperty(
    key = "max",
    description = "Maximum code lines allowed",
    defaultValue = "" + DEFAULT_MAXIMUM)
  private int max = DEFAULT_MAXIMUM;

  public void setMax(int max) {
    this.max = max;
  }

  @Override
  public void leaveFile(AstNode astNode) {
    int linesOfCode = ChecksHelper.getRecursiveMeasureInt(getContext().peekSourceCode(), CxxMetric.LINES_OF_CODE);
    if (linesOfCode > max) {
      getContext().createFileViolation(this, "This file has {0} lines of code, which is greater than {1} authorized. Split it into smaller files.", linesOfCode, max);
    }
  }
}

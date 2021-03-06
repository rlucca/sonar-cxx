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

import java.io.File;

//import org.sonar.squid.api.CheckMessage;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.cxx.CxxAstScanner;
//import org.sonar.java.model.VisitorsBridge;
import org.sonar.squidbridge.api.SourceFile;
import org.sonar.squidbridge.checks.CheckMessagesVerifier;
import org.sonar.squidbridge.checks.CheckMessagesVerifierRule;

public class HardcodedIpCheckTest {

  @Rule
  public final CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  private final HardcodedIpCheck check = new HardcodedIpCheck();

  @Test
  public void detected() {
    SourceFile file = CxxAstScanner.scanSingleFile(new File("src/test/resources/checks/HardcodedIpCheck.cc"), check);
    CheckMessagesVerifier.verify(file.getCheckMessages())
      .next().atLine(5).withMessage("Make this IP \"0.0.0.0\" address configurable.")
      .next().atLine(6).withMessage("Make this IP \"http://192.168.0.1/admin.html\" address configurable.");
  }

}

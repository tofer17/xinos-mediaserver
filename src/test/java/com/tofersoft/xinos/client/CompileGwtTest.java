package com.tofersoft.xinos.client;

import com.google.gwt.junit.client.GWTTestCase;

public class CompileGwtTest extends GWTTestCase {
  
  @Override
  public String getModuleName() {
    return "com.tofersoft.xinos.Xinos";
  }

  public void testSandbox() {
    assertTrue(true);
  }
  
}

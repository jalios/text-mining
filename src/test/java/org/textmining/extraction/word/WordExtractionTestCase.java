/*
 * Textmining.org text extractors
 * 
 * Copyright (C) 2008 Benryan Software Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package org.textmining.extraction.word;

import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;

import junit.framework.TestCase;

import org.textmining.extraction.TextExtractor;
import org.textmining.extraction.word.PasswordProtectedException;

public abstract class WordExtractionTestCase extends TestCase {

  protected void textExtractionTest(String fileName, String text)
      throws IOException, PasswordProtectedException {

    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource(fileName).getFile());

    try (FileInputStream in = new FileInputStream(file)) {
      TextExtractor extractor = getTextExtractor(in);
      String testText = extractor.getText().trim();
      assertEquals(testText, text.trim());
    }

  }

  protected abstract TextExtractor getTextExtractor(FileInputStream in)
      throws IOException, PasswordProtectedException;

}

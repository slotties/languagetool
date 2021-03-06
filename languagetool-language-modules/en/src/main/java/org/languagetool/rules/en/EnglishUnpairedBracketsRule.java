/* LanguageTool, a natural language style checker 
 * Copyright (C) 2010 Daniel Naber (http://www.languagetool.org)
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */

package org.languagetool.rules.en;

import java.util.ResourceBundle;
import java.util.regex.Pattern;

import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.Language;
import org.languagetool.rules.GenericUnpairedBracketsRule;

public class EnglishUnpairedBracketsRule extends GenericUnpairedBracketsRule {
  
  private static final String[] EN_START_SYMBOLS = { "[", "(", "{", "“", "\"", "'" };
  private static final String[] EN_END_SYMBOLS   = { "]", ")", "}", "”", "\"", "'" };
  
  private static final Pattern NUMBER = Pattern.compile("\\d+");

  public EnglishUnpairedBracketsRule(final ResourceBundle messages,
      final Language language) {
    super(messages, language);
    startSymbols = EN_START_SYMBOLS;
    endSymbols = EN_END_SYMBOLS;
    uniqueMapInit();
  }

  @Override
  public String getId() {
    return "EN_UNPAIRED_BRACKETS";
  }
  
  @Override
  protected boolean isNoException(final String tokenStr,
      final AnalyzedTokenReadings[] tokens, final int i, final int j, final boolean precSpace,
      final boolean follSpace) {
       
    //TODO: add an', o', 'till, 'tain't, 'cept, 'fore in the disambiguator
    //and mark up as contractions somehow
    // add exception for dates like '52
   
    if (i <= 1) {
      return true;
    }
    
    final boolean superException = !super.isNoException(tokenStr, tokens, i, j, precSpace, follSpace);
    if (superException) {
      return false;
    }
    
    if (!precSpace && follSpace) {
      // exception for English inches, e.g., 20"
      final AnalyzedTokenReadings prevToken = tokens[i - 1];
      if ("\"".equals(tokenStr)
          && NUMBER.matcher(prevToken.getToken()).matches()) {
        return false;
      }
      // Exception for English plural Saxon genitive
      // current disambiguation scheme is a bit too greedy
      // for adjectives
      if ("'".equals(tokenStr) && tokens[i].hasPosTag("POS")) {
        return false;
      }
      // puttin' on the Ritz
      if ("'".equals(tokenStr) && prevToken.hasPosTag("VBG")
          && prevToken.getToken().endsWith("in")) {
        return false;
      }
    }
    if (precSpace && !follSpace) {
      // hold 'em!
      if ("'".equals(tokenStr) && i + 1 < tokens.length
          && "em".equals(tokens[i + 1].getToken())) {
        return false;
      }
    }
    return true;
  }

  
}

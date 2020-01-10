/*
 *  Copyright (c) 2020 gematik GmbH
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package de.gematik.ti.test.utils.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.gematik.ti.cardreader.provider.api.command.CommandApdu;
import de.gematik.ti.utils.codec.Hex;

/**
 * Check and convert apdu in bytes-array in a CommandApdu
 *
 * Command APDU encoding options:
 * case 1: |CLA|INS|P1 |P2 | len = 4 
 * case 2s: |CLA|INS|P1 |P2 |LE | len = 5 
 * case 3s: |CLA|INS|P1 |P2 |LC |...BODY...| len = 6..260 
 * case 4s: |CLA|INS|P1 |P2 |LC |...BODY...|LE | len = 7..261 
 * case 2e: |CLA|INS|P1 |P2 |00 |LE1|LE2| len = 7 
 * case 3e: |CLA|INS|P1 |P2 |00 |LC1|LC2|...BODY...| len = 8..65542 
 * case 4e: |CLA|INS|P1 |P2 |00 |LC1|LC2|...BODY...|LE1|LE2| len =10..65544 
 * 
 * LE, LE1, LE2 may be 0x00. 
 * LC must not be 0x00 and LC1|LC2 must not be 0x00|0x00 
 *
 * @author zhenwu.duan
 */
public final class ApduParser {
    private static final Logger LOG = LoggerFactory.getLogger(ApduParser.class);
    private static final int MIN_APDU_LENGTH = 4;
    private static final int BYTE_MASK = 0xFF;
    private static final int EXPECTED_LENGTH_WILDCARD_SHORT = 256;
    private static final int EXPECTED_LENGTH_WILDCARD_EXTENDED = 65536;

    private ApduParser() {
    }

    /**
     * Create CommandApdu from byte String
     *
     * @param apduString bytes for APDU creation
     * @return instance of CommandApdu
     */
    public static CommandApdu toCommandApdu(final String apduString) {
        byte[] apduRaw = Hex.decode(apduString);
        byte[] apdu = apduRaw.clone();
        return parse(apdu);
    }

    /**
     * Create CommandApdu from byte Array
     *
     * @param apduRaw byte array representing an apdu
     * @return instance of CommandApdu
     */
    public static CommandApdu toCommandApdu(final byte[] apduRaw) {
        byte[] apdu = apduRaw.clone();
        return parse(apdu);
    }

    private static CommandApdu parse(final byte[] apdu) {
        if (apdu.length < MIN_APDU_LENGTH) {
            throw new IllegalArgumentException("apdu must be at least 4 bytes long");
        }
        if (apdu.length == MIN_APDU_LENGTH) {
            LOG.debug("It's a Command of CASE-1");
            return new CommandApdu(apdu[0] & BYTE_MASK, apdu[1] & BYTE_MASK, apdu[2] & BYTE_MASK, apdu[3] & BYTE_MASK);
        }
        // ---------------------------------------------------------------------------
        int l1 = apdu[4] & BYTE_MASK;
        if (apdu.length == 5) {
            return doCase2e(apdu, l1, "It's a Command of CASE-2s", EXPECTED_LENGTH_WILDCARD_SHORT);
        }
        // ---------------------------------------------------------------------------
        if (l1 != 0) {
            if (apdu.length == MIN_APDU_LENGTH + 1 + l1) {
                return doCase3(apdu, l1, "It's a Command of CASE-3s", 5);
            } else if (apdu.length == MIN_APDU_LENGTH + 2 + l1) {
                return doCase4s(apdu, l1);
            } else {
                throw new IllegalArgumentException("Invalid APDU: length=" + apdu.length + ", b1=" + l1);
            }
        }
        if (apdu.length < 7) {
            throw new IllegalArgumentException("Invalid APDU: length=" + apdu.length + ", b1=" + l1);
        }
        int l2 = ((apdu[5] & BYTE_MASK) << 8) | (apdu[6] & BYTE_MASK);
        if (apdu.length == 7) {
            return doCase2e(apdu, l2, "It's a Command of CASE-2e", EXPECTED_LENGTH_WILDCARD_EXTENDED);
        }
        if (l2 == 0) {
            throw new IllegalArgumentException("Invalid APDU: length=" + apdu.length + ", b1=" + l1 + ", b2||b3=" + l2);
        }
        if (apdu.length == MIN_APDU_LENGTH + 3 + l2) {
            return doCase3(apdu, l2, "It's a Command of CASE-3e", 7);
        } else if (apdu.length == MIN_APDU_LENGTH + 5 + l2) {
            return doCase4e(apdu, l2);
        } else {
            throw new IllegalArgumentException("Invalid APDU: length=" + apdu.length + ", b1=" + l1 + ", b2||b3=" + l2);
        }
    }

    private static CommandApdu doCase4s(final byte[] apdu, final int l1) {
        final int dataOffset;
        final int ne;
        final int dataLength;
        LOG.debug("It's a Command of CASE-4s");
        dataOffset = 5;
        int l2 = apdu[apdu.length - 1] & BYTE_MASK;
        ne = (l2 == 0) ? EXPECTED_LENGTH_WILDCARD_SHORT : l2;
        dataLength = l1;
        byte[] data = new byte[dataLength];
        System.arraycopy(apdu, dataOffset, data, 0, dataLength);
        return new CommandApdu(apdu[0] & BYTE_MASK, apdu[1] & BYTE_MASK, apdu[2] & BYTE_MASK, apdu[3] & BYTE_MASK, data, ne);
    }

    private static CommandApdu doCase2e(final byte[] apdu, final int l2, final String s, final int i) {
        final int ne;
        LOG.debug(s);
        ne = (l2 == 0) ? i : l2;
        return new CommandApdu(apdu[0] & BYTE_MASK, apdu[1] & BYTE_MASK, apdu[2] & BYTE_MASK, apdu[3] & BYTE_MASK, ne);
    }

    private static CommandApdu doCase4e(final byte[] apdu, final int l2) {
        final int dataOffset;
        final int ne;
        final int dataLength;
        LOG.debug("It's a Command of CASE-4e");
        dataOffset = 7;
        int leOfs = apdu.length - 2;
        int l3 = ((apdu[leOfs] & BYTE_MASK) << 8) | (apdu[leOfs + 1] & BYTE_MASK);
        ne = (l3 == 0) ? EXPECTED_LENGTH_WILDCARD_EXTENDED : l3;

        dataLength = l2;
        byte[] data = new byte[dataLength];
        System.arraycopy(apdu, dataOffset, data, 0, dataLength);
        return new CommandApdu(apdu[0] & BYTE_MASK, apdu[1] & BYTE_MASK, apdu[2] & BYTE_MASK, apdu[3] & BYTE_MASK, data, ne);
    }

    private static CommandApdu doCase3(final byte[] apdu, final int l1, final String s, final int i) {
        final int dataOffset;
        final int dataLength;
        LOG.debug(s);
        dataOffset = i;
        dataLength = l1;
        byte[] data = new byte[dataLength];
        System.arraycopy(apdu, dataOffset, data, 0, dataLength);
        return new CommandApdu(apdu[0] & BYTE_MASK, apdu[1] & BYTE_MASK, apdu[2] & BYTE_MASK, apdu[3] & BYTE_MASK, data);
    }

}

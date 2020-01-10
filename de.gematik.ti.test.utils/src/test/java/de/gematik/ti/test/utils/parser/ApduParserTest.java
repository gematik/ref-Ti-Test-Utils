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

import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.gematik.ti.cardreader.provider.api.command.CommandApdu;
import de.gematik.ti.utils.codec.Hex;

/**
 * @author zhenwu.duan
 */
public class ApduParserTest {

    private static final Logger LOG = LoggerFactory.getLogger(ApduParserTest.class);

    @Rule
    public TestName testName = new TestName();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void shouldEqualsCaseOneCommandApdu() {
        LOG.info("--------- Start: " + testName.getMethodName());
        String input = "00442089";
        CommandApdu commandApdu = ApduParser.toCommandApdu(Hex.decode(input));
        Assert.assertThat(commandApdu, IsNull.notNullValue());
        Assert.assertThat(commandApdu.getCla(), Is.is(0x00));
        Assert.assertThat(commandApdu.getIns(), Is.is(0x44));
        Assert.assertThat(commandApdu.getP1(), Is.is(0x20));
        Assert.assertThat(commandApdu.getP2(), Is.is(0x89));
        Assert.assertThat(commandApdu.getData(), IsEqual.equalTo(new byte[0]));
        Assert.assertThat(commandApdu.getNe(), IsNull.nullValue());
        String cmdApduEncoded = Hex.encodeHexString(commandApdu.getBytes());
        Assert.assertThat(cmdApduEncoded, Is.is(input));
    }

    @Test
    public void shouldEqualsCaseTwoExtendedCommandApdu() {
        String input = "80CA0100000000";
        CommandApdu commandApdu = ApduParser.toCommandApdu(Hex.decode(input));
        Assert.assertThat(commandApdu, IsNull.notNullValue());
        Assert.assertThat(commandApdu.getNe(), Is.is(65536));
        String cmdApduEncoded = Hex.encodeHexString(commandApdu.getBytes());
        Assert.assertThat(cmdApduEncoded, Is.is(input));
    }

    @Test
    public void shouldEqualsCaseThreeExtendedCommandApdu() {
        String input = "00D6000000023E0008015B015C023D1F8B0800000000000000BD525B6F823014FE2B84773920A2B2941AA7CB629CBACCCC2D7B211D1C81086569AB5BFCF"
                + "52B862C608CDBD35EDA9CCB7769CF21A3AF22370E286456F2C0742CDB349047659CF1243067EB556738F4FC8EE399234A9E27E138CF132C30E3B8A930518A62CF1"
                + "3193385FC75F16068362E033355EAE306E0535ABA9BA96C67C5085B06071917D50107CFEA9AC664BA0837774FEBD96A19983AA3D529F92156281A512523A374AF8"
                + "E94DC6292714EBBB633B05DDB275027C8BC94DA88120C930ADC0A77C8B926A1BEEF7BFDC1D0B7095CACB75139431EA3D03F82747A8668D6C8921548EBD71A8BBD6"
                + "E133B26253AF7F30D8153958CDF0546293FA1FE66D571DD9ED7EFFDBF55B8E215CEE38B437ADB4BA68E19DF96B2156891D69039138ABA0D923AD5C484B51BCD1F9"
                + "EF02FAB476A3B04AA9BC0B54E389786963168AF1BFCBEE3F41B5A123AD5340300001F8B0800000000000000854FDB6AC24010FD95B0EF666221A03259292A12F00"
                + "20D15F1459664CC06938D642769C9D777430B2A2DF4E5CCCC99CB9983F3CFAAF43A6A6C519B488CFD407864D23A2B4C1E8938D98F2693703A1A87622EF17D715E9"
                + "34D754BDC331D86A55453C36432E5E0B8DD78EE9AB191D0CCB719C087F573AA1417573F23B828E86C560D005DE8BF086FB1DC9E0FABB724DEEF22E118A72EF1D4F"
                + "64A97ADC9AD65C5AD95987CC700E12743F863884ABA72D1392FDC28CA49E22B353D97EE473945B8177852DAA8C7DE33E1647EDF82FFCDCB2FA5BC4FE04D010000";
        CommandApdu commandApdu = ApduParser.toCommandApdu(Hex.decode(input));
        Assert.assertThat(commandApdu, IsNull.notNullValue());
        Assert.assertThat(commandApdu.getNc(), Is.is(574));
        String cmdApduEncoded = Hex.encodeHexString(commandApdu.getBytes());
        Assert.assertThat(cmdApduEncoded, Is.is(input));
    }

    @Test
    public void shouldEqualsCaseTwoShortCommandApdu() {
        LOG.info("--------- Start: " + testName.getMethodName());
        String input = "0026008108";
        CommandApdu commandApdu = ApduParser.toCommandApdu(Hex.decode(input));
        Assert.assertThat(commandApdu, IsNull.notNullValue());
        Assert.assertThat(commandApdu.getCla(), Is.is(0x00));
        Assert.assertThat(commandApdu.getIns(), Is.is(0x26));
        Assert.assertThat(commandApdu.getP1(), Is.is(0x00));
        Assert.assertThat(commandApdu.getP2(), Is.is(0x81));
        Assert.assertThat(commandApdu.getData(), IsEqual.equalTo(new byte[] {}));
        Assert.assertThat(commandApdu.getNe(), Is.is(8));
        String cmdApduEncoded = Hex.encodeHexString(commandApdu.getBytes());
        Assert.assertThat(cmdApduEncoded, Is.is(input));
    }

    @Test
    public void shouldEqualsCaseThreeShortCommandApdu() { // NOCS(lac): only test
        LOG.info("--------- Start: " + testName.getMethodName());
        String input = "002400811026123456FFFFFFFF26123456FFFFFFFF";
        CommandApdu commandApdu = ApduParser.toCommandApdu(Hex.decode(input));
        Assert.assertThat(commandApdu, IsNull.notNullValue());
        Assert.assertThat(commandApdu.getCla(), Is.is(0x00));
        Assert.assertThat(commandApdu.getIns(), Is.is(0x24));
        Assert.assertThat(commandApdu.getP1(), Is.is(0x00));
        Assert.assertThat(commandApdu.getP2(), Is.is(0x81));
        Assert.assertThat(commandApdu.getData(), IsEqual.equalTo(new byte[] { (byte) 0x26, (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, (byte) 0x26, (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF }));
        Assert.assertThat(commandApdu.getNe(), IsNull.nullValue());
        String cmdApduEncoded = Hex.encodeHexString(commandApdu.getBytes());
        Assert.assertThat(cmdApduEncoded, Is.is(input));
        //
        String input1 = "00E200E80100";
        commandApdu = ApduParser.toCommandApdu(Hex.decode(input1));
        Assert.assertThat(commandApdu, IsNull.notNullValue());
        Assert.assertThat(commandApdu.getCla(), Is.is(0x00));
        Assert.assertThat(commandApdu.getIns(), Is.is(0xE2));
        Assert.assertThat(commandApdu.getP1(), Is.is(0x00));
        Assert.assertThat(commandApdu.getP2(), Is.is(0xE8));
        Assert.assertThat(commandApdu.getData(), IsEqual.equalTo(new byte[] { 0x00 }));
        Assert.assertThat(commandApdu.getNe(), IsNull.nullValue());
        cmdApduEncoded = Hex.encodeHexString(commandApdu.getBytes());
        Assert.assertThat(cmdApduEncoded, Is.is(input1));
        //
        String input2 = "002281B60A83084445475858830214";
        commandApdu = ApduParser.toCommandApdu(Hex.decode(input2));
        Assert.assertThat(commandApdu, IsNull.notNullValue());
        Assert.assertThat(commandApdu.getCla(), Is.is(0x00));
        Assert.assertThat(commandApdu.getIns(), Is.is(0x22));
        Assert.assertThat(commandApdu.getP1(), Is.is(0x81));
        Assert.assertThat(commandApdu.getP2(), Is.is(0xB6));
        Assert.assertThat(commandApdu.getData(), IsEqual.equalTo(new byte[] { (byte) 0x83, (byte) 0x08, (byte) 0x44, (byte) 0x45, (byte) 0x47, (byte) 0x58,
                (byte) 0x58, (byte) 0x83, (byte) 0x02, (byte) 0x14 }));
        Assert.assertThat(commandApdu.getNe(), IsNull.nullValue());
        cmdApduEncoded = Hex.encodeHexString(commandApdu.getBytes());
        Assert.assertThat(cmdApduEncoded, Is.is(input2));
    }

    @Test
    public void shouldEqualsCaseFourShortCommandApdu() {
        LOG.info("--------- Start: " + testName.getMethodName());
        String input = "80FA000080000102030405060708090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F202122232425262728292A2B2C2D2E2F303132333435363738393A3B3C3"
                + "D3E3F404142434445464748494A4B4C4D4E4F505152535455565758595A5B5C5D5E5F606162636465666768696A6B6C6D6E6F707172737475767778797A7B7C7D7E7F00";
        CommandApdu commandApdu = ApduParser.toCommandApdu(Hex.decode(input));
        Assert.assertThat(commandApdu, IsNull.notNullValue());
        Assert.assertThat(commandApdu.getCla(), Is.is(0x80));
        Assert.assertThat(commandApdu.getIns(), Is.is(0xFA));
        Assert.assertThat(commandApdu.getP1(), Is.is(0x00));
        Assert.assertThat(commandApdu.getP2(), Is.is(0x00));
        Assert.assertThat(commandApdu.getData().length, IsEqual.equalTo(128));
        Assert.assertThat(commandApdu.getNe(), Is.is(256));
        String cmdApduEncoded = Hex.encodeHexString(commandApdu.getBytes());
        Assert.assertThat(cmdApduEncoded, Is.is(input));
    }

    @Test
    public void shouldEqualsCaseFourExtendedCommandApdu() {
        LOG.info("--------- Start: " + testName.getMethodName());
        String input = "002A8E8000000501090203040000";
        CommandApdu commandApdu = ApduParser.toCommandApdu(Hex.decode(input));
        Assert.assertThat(commandApdu.getData(), IsEqual.equalTo(new byte[] { 0x01, 0x09, 0x02, 0x03, 0x04 }));
        Assert.assertThat(commandApdu.getNe(), Is.is(65536));
        String cmdApduEncoded = Hex.encodeHexString(commandApdu.getBytes());
        Assert.assertThat(cmdApduEncoded, Is.is(input));

        String input1 = "002A8E80000004010203040000";
        commandApdu = ApduParser.toCommandApdu(Hex.decode(input1));
        Assert.assertThat(commandApdu.getData(), IsEqual.equalTo(new byte[] { 0x01, 0x02, 0x03, 0x04 }));
        Assert.assertThat(commandApdu.getNe(), Is.is(65536));
        cmdApduEncoded = Hex.encodeHexString(commandApdu.getBytes());
        Assert.assertThat(cmdApduEncoded, Is.is(input1));

        String input2 = "002A8680000119A08201158001017F4982010A818201010094BEEBAA816749F9F304AC9EE7C70A13DCF6D2C9E511CEA7C07822CBAA21B713BB90DB"
                + "DA98D9E12AA48076DC40B632EC9DFC9A8C5A7A51B92601D7A17D1FF1C8E164747CB1A95ABF14D8EA515B920167DFE2623580F43740A8109966A7CBFB663189"
                + "F67EF3FDAA83075A67875C4D2715F38E0DE1270B05BD1F7BC192E9ABEF8938053481D9CA2612051822AB0AD1E61FF8E5423F4A8221BF0765C6658EC00B6A7F"
                + "4E5A1FBC4F997E6FF35FD85BC2FBB5C797B63AF48194D31E4959F7651E8F5E1F1AF8819948DE2EA1123C5D9AD892B89E78BE63D582290BB548C4918E7B69F0"
                + "784EAA4AE8706FFF643696E9F1FF9C01951E3BF318991611C77928066174E38182030100018001000100";

        commandApdu = ApduParser.toCommandApdu(Hex.decode(input2));
        Assert.assertThat(commandApdu.getNe(), Is.is(256));
        cmdApduEncoded = Hex.encodeHexString(commandApdu.getBytes());
        Assert.assertThat(cmdApduEncoded, Is.is(input2));
    }

}

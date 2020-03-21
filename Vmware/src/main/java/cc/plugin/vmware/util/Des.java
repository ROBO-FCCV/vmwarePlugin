
/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能描述
 *
 * @since 2019 -10-09
 */
public class Des {
    private static final Logger logger = LoggerFactory.getLogger(Des.class);

    /**
     * Str enc string.
     *
     * @param data the data
     * @param firstKey the first key
     * @param secondKey the second key
     * @param thirdKey the third key
     * @return the string
     */
    public String strEnc(String data, String firstKey, String secondKey, String thirdKey) {
        if (data == null) {
            logger.info("data is null");
            return null;
        }
        int leng = data.length();
        StringBuffer encData = new StringBuffer();
        int firstLength = 0;
        int secondLength;
        int thirdLength;
        List firstKeyBt = getFirstKeyBt(firstKey, null);
        if (firstKeyBtNotEmpty(firstKeyBt)) {
            logger.error("--------- firstKeyBt is " + firstKeyBt);
            return null;
        }
        firstLength = getFirstLength(firstKeyBt, firstLength);
        List secondKeyBt = getFirstKeyBt(secondKey, null);
        if (secondKeyBt == null) {
            logger.error("secondKeyBt is null");
            return null;
        }
        secondLength = secondKeyBt.size();
        List thirdKeyBt = getFirstKeyBt(thirdKey, null);
        if (thirdKeyBt == null) {
            logger.error("thirdKeyBt is null");
            return null;
        }
        thirdLength = thirdKeyBt.size();

        if (leng <= 0) {
            return encData.toString();
        }
        if (firstKeyBt == null) {
            firstKeyBt = new ArrayList();
        }
        encData = getEncData(data, firstKey, secondKey, thirdKey, leng, encData, firstKeyBt, secondKeyBt, thirdKeyBt,
            firstLength, secondLength, thirdLength);
        return encData.toString();
    }

    private StringBuffer getEncData(String data, String firstKey, String secondKey, String thirdKey, int leng,
        StringBuffer encData, List firstKeyBt, List secondKeyBt, List thirdKeyBt, int firstLength, int secondLength,
        int thirdLength) {
        if (leng < 4) {
            encData = getEncData(data, firstKey, secondKey, thirdKey, encData, firstKeyBt, secondKeyBt, thirdKeyBt,
                firstLength, secondLength, thirdLength);
        } else {
            getEncData3(data, firstKey, secondKey, thirdKey, leng, encData, firstKeyBt, secondKeyBt, thirdKeyBt,
                firstLength, secondLength, thirdLength);
        }
        return encData;
    }

    private int getFirstLength(List firstKeyBt, int firstLength) {
        if (firstKeyBt != null) {
            firstLength = firstKeyBt.size();
        }
        return firstLength;
    }

    private List getFirstKeyBt(String firstKey, List firstKeyBt) {
        if (StringUtils.isNotBlank(firstKey)) {
            firstKeyBt = getKeyBytes(firstKey);
        }
        return firstKeyBt;
    }

    private void getEncData3(String data, String firstKey, String secondKey, String thirdKey, int leng,
        StringBuffer encData, List firstKeyBt, List secondKeyBt, List thirdKeyBt, int firstLength, int secondLength,
        int thirdLength) {
        int iterator = leng / 4;
        int remainder = leng % 4;
        int it;
        for (it = 0; it < iterator; ++it) {
            getEncData2(data, firstKey, secondKey, thirdKey, encData, firstKeyBt, secondKeyBt, thirdKeyBt, firstLength,
                secondLength, thirdLength, it);
        }

        if (remainder > 0) {
            String remainderData = data.substring(iterator * 4 + 0, leng);
            int[] tempByte = strToBt(remainderData);
            int[] encByte = buildEncByte(firstKey, secondKey, thirdKey, tempByte, firstLength, secondLength,
                thirdLength, firstKeyBt, secondKeyBt, thirdKeyBt);
            if (encByte != null) {
                encData.append(bt64ToHex(encByte));
            }
        }
    }

    private void getEncData2(String data, String firstKey, String secondKey, String thirdKey, StringBuffer encData,
        List firstKeyBt, List secondKeyBt, List thirdKeyBt, int firstLength, int secondLength, int thirdLength, int i) {
        String tempData = data.substring(i * 4 + 0, i * 4 + 4);
        int[] tempByte = strToBt(tempData);
        int[] encByte = null;
        if (anyKeyNotBlank(firstKey, secondKey, thirdKey)) {
            int[] tempBt =
                getEncByte4(firstKeyBt, secondKeyBt, thirdKeyBt, firstLength, secondLength, thirdLength, tempByte);
            encByte = tempBt;
        } else if (anyKeyLengthValid(firstKey, secondKey)) {
            int[] tempBt = getEncByte5(firstKeyBt, secondKeyBt, firstLength, secondLength, tempByte);
            encByte = tempBt;
        } else if (firstKeyNotEmpty(firstKey)) {
            int[] tempBt = getEncByte6(firstKeyBt, firstLength, tempByte);
            encByte = tempBt;
        }
        if (encByte != null) {
            encData.append(bt64ToHex(encByte));
        }
    }

    private StringBuffer getEncData(String data, String firstKey, String secondKey, String thirdKey,
        StringBuffer encData, List firstKeyBt, List secondKeyBt, List thirdKeyBt, int firstLength, int secondLength,
        int thirdLength) {
        int[] bt = strToBt(data);
        int[] encByte = null;
        if (anyKeyNotEmpty(firstKey, secondKey, thirdKey)) {
            int[] tempBt = getEncByte1(firstKeyBt, secondKeyBt, thirdKeyBt, firstLength, secondLength, thirdLength, bt);
            encByte = tempBt;
        } else if (anyKeyNotEmpty(firstKey, secondKey)) {
            int[] tempBt = getEncByte2(firstKeyBt, secondKeyBt, firstLength, secondLength, bt);
            encByte = tempBt;
        } else if (getFirstKeyNotNull3(firstKey)) {
            int[] tempBt = encByte3(firstKeyBt, firstLength, bt);
            encByte = tempBt;
        }

        if (encByte != null) {
            encData = new StringBuffer(bt64ToHex(encByte));
        }
        return encData;
    }

    private int[] getEncByte6(List firstKeyBt, int firstLength, int[] tempByte) {
        int[] tempBt = tempByte;
        for (int x = 0; x < firstLength; ++x) {
            tempBt = enc(tempBt, (int[]) (int[]) firstKeyBt.get(x));
        }
        return tempBt;
    }

    private int[] getEncByte5(List firstKeyBt, List secondKeyBt, int firstLength, int secondLength, int[] tempByte) {
        int[] tempBt = tempByte;
        for (int x = 0; x < firstLength; ++x) {
            tempBt = enc(tempBt, (int[]) (int[]) firstKeyBt.get(x));
        }
        for (int y = 0; y < secondLength; ++y) {
            tempBt = enc(tempBt, (int[]) (int[]) secondKeyBt.get(y));
        }
        return tempBt;
    }

    private int[] getEncByte4(List firstKeyBt, List secondKeyBt, List thirdKeyBt, int firstLength, int secondLength,
        int thirdLength, int[] tempByte) {
        int[] tempBt = tempByte;
        for (int x = 0; x < firstLength; ++x) {
            tempBt = enc(tempBt, (int[]) (int[]) firstKeyBt.get(x));
        }
        for (int y = 0; y < secondLength; ++y) {
            tempBt = enc(tempBt, (int[]) (int[]) secondKeyBt.get(y));
        }
        for (int z = 0; z < thirdLength; ++z) {
            tempBt = enc(tempBt, (int[]) (int[]) thirdKeyBt.get(z));
        }
        return tempBt;
    }

    private int[] encByte3(List firstKeyBt, int firstLength, int[] bt) {
        int xt;
        int[] tempBt = bt;
        for (xt = 0; xt < firstLength; ++xt) {
            tempBt = enc(tempBt, (int[]) (int[]) firstKeyBt.get(xt));
        }
        return tempBt;
    }

    private int[] getEncByte2(List firstKeyBt, List secondKeyBt, int firstLength, int secondLength, int[] bt) {
        int[] tempBt = bt;
        for (int x = 0; x < firstLength; ++x) {
            tempBt = enc(tempBt, (int[]) (int[]) firstKeyBt.get(x));
        }
        for (int y = 0; y < secondLength; ++y) {
            tempBt = enc(tempBt, (int[]) (int[]) secondKeyBt.get(y));
        }
        return tempBt;
    }

    private int[] getEncByte1(List firstKeyBt, List secondKeyBt, List thirdKeyBt, int firstLength, int secondLength,
        int thirdLength, int[] bt) {
        int[] tempBt = bt;
        for (int x = 0; x < firstLength; ++x) {
            tempBt = enc(tempBt, (int[]) (int[]) firstKeyBt.get(x));
        }
        for (int y = 0; y < secondLength; ++y) {
            tempBt = enc(tempBt, (int[]) (int[]) secondKeyBt.get(y));
        }
        for (int z = 0; z < thirdLength; ++z) {
            tempBt = enc(tempBt, (int[]) (int[]) thirdKeyBt.get(z));
        }
        return tempBt;
    }

    private boolean firstKeyNotEmpty(String firstKey) {
        return (firstKey != null) && firstKey.length() > 0;
    }

    private boolean anyKeyLengthValid(String firstKey, String secondKey) {
        return (firstKey != null) && (firstKey.length() > 0) && (secondKey != null) && secondKey.length() > 0;
    }

    private boolean anyKeyNotBlank(String firstKey, String secondKey, String thirdKey) {
        return (StringUtils.isNotBlank(firstKey)) && (StringUtils.isNotBlank(secondKey))
            && (StringUtils.isNotBlank(thirdKey));
    }

    private boolean anyKeyNotEmpty(String firstKey, String secondKey) {
        return (StringUtils.isNotEmpty(firstKey)) && (!"".equalsIgnoreCase(firstKey))
            && (StringUtils.isNotEmpty(secondKey)) && !"".equalsIgnoreCase(secondKey);
    }

    private boolean anyKeyNotEmpty(String firstKey, String secondKey, String thirdKey) {
        return (firstKey != null) && (!"".equalsIgnoreCase(firstKey)) && (secondKey != null)
            && (!"".equalsIgnoreCase(secondKey)) && (thirdKey != null) && (!"".equalsIgnoreCase(secondKey));
    }

    private boolean firstKeyBtNotEmpty(List firstKeyBt) {
        return (firstKeyBt == null) || (firstKeyBt.size() == 0);
    }

    private int[] buildEncByte(String firstKey, String secondKey, String thirdKey, int[] tempByte, int firstLength,
        int secondLength, int thirdLength, List firstKeyBt, List secondKeyBt, List thirdKeyBt) {
        if (firstKeyNotEmpty(firstKey, secondKey, thirdKey)) {
            int[] tempBt =
                getTempBt8(tempByte, firstLength, secondLength, thirdLength, firstKeyBt, secondKeyBt, thirdKeyBt);
            return tempBt;
        } else if (getFirstKeyNotNull2(firstKey, secondKey)) {
            int[] tempBt = getTempBt9(tempByte, firstLength, secondLength, firstKeyBt, secondKeyBt);
            return tempBt;
        } else if (getFirstKeyNotNull3(firstKey)) {
            int[] tempBt = getTempBt10(tempByte, firstLength, firstKeyBt);
            return tempBt;
        }
        return tempByte;
    }

    private boolean getFirstKeyNotNull3(String firstKey) {
        return (firstKey != null) && !"".equalsIgnoreCase(firstKey);
    }

    private boolean getFirstKeyNotNull2(String firstKey, String secondKey) {
        return (firstKey != null) && !"".equalsIgnoreCase(firstKey) && (secondKey != null)
            && !"".equalsIgnoreCase(secondKey);
    }

    private boolean firstKeyNotEmpty(String firstKey, String secondKey, String thirdKey) {
        return (firstKey != null) && !"".equalsIgnoreCase(firstKey) && (secondKey != null)
            && !"".equalsIgnoreCase(secondKey) && (thirdKey != null) && !"".equalsIgnoreCase(thirdKey);
    }

    private int[] getTempBt10(int[] tempByte, int firstLength, List firstKeyBt) {
        int[] tempBt = tempByte;
        for (int x = 0; x < firstLength; ++x) {
            tempBt = enc(tempBt, (int[]) (int[]) firstKeyBt.get(x));
        }
        return tempBt;
    }

    private int[] getTempBt9(int[] tempByte, int firstLength, int secondLength, List firstKeyBt, List secondKeyBt) {
        int[] tempBt = tempByte;
        for (int x = 0; x < firstLength; ++x) {
            tempBt = enc(tempBt, (int[]) (int[]) firstKeyBt.get(x));
        }
        for (int y = 0; y < secondLength; ++y) {
            tempBt = enc(tempBt, (int[]) (int[]) secondKeyBt.get(y));
        }
        return tempBt;
    }

    private int[] getTempBt8(int[] tempByte, int firstLength, int secondLength, int thirdLength, List firstKeyBt,
        List secondKeyBt, List thirdKeyBt) {
        int[] tempBt = tempByte;
        for (int x = 0; x < firstLength; ++x) {
            tempBt = enc(tempBt, (int[]) (int[]) firstKeyBt.get(x));
        }
        for (int y = 0; y < secondLength; ++y) {
            tempBt = enc(tempBt, (int[]) (int[]) secondKeyBt.get(y));
        }
        for (int z = 0; z < thirdLength; ++z) {
            tempBt = enc(tempBt, (int[]) (int[]) thirdKeyBt.get(z));
        }
        return tempBt;
    }

    private int[] strToBt(String str) {
        int leng = str.length();
        int[] bt = new int[64];
        if (leng < 4) {
            int jt;
            int pt;
            int qt;
            for (int i = 0; i < leng; ++i) {
                jt = getBtArray(str, bt, i);
                logger.info("strToBt get j is: {}", jt);
            }
            for (pt = leng; pt < 4; ++pt) {
                qt = getBtArray2(bt, pt);
                logger.info("strToBt get q is: {}", qt);
            }
        } else {
            getBtArray3(str, bt);
        }
        return bt;
    }

    private void getBtArray3(String str, int[] bt) {
        for (int i = 0; i < 4; ++i) {
            int kt = str.charAt(i);
            for (int j = 0; j < 16; ++j) {
                int pow = 1;
                for (int m = 15; m > j; --m) {
                    pow *= 2;
                }

                bt[16 * i + j] = kt / pow % 2;
            }
        }
    }

    private int getBtArray2(int[] bt, int p) {
        int qt;
        int kt = 0;
        for (qt = 0; qt < 16; ++qt) {
            int pow = 1;
            int mt;
            for (mt = 15; mt > qt; --mt) {
                pow *= 2;
            }

            bt[16 * p + qt] = kt / pow % 2;
        }
        return qt;
    }

    private int getBtArray(String str, int[] bt, int i) {
        int jt;
        int kt = str.charAt(i);
        for (jt = 0; jt < 16; ++jt) {
            int pow = 1;
            int mt;
            for (mt = 15; mt > jt; --mt) {
                pow *= 2;
            }

            bt[16 * i + jt] = kt / pow % 2;
        }
        return jt;
    }

    private List getKeyBytes(String key) {
        List keyBytes = new ArrayList();
        if (!StringUtils.isEmpty(key)) {
            int leng = key.length();
            int iterator = leng / 4;
            int remainder = leng % 4;
            int it;
            for (it = 0; it < iterator; ++it) {
                keyBytes.add(it, strToBt(key.substring(it * 4 + 0, it * 4 + 4)));
            }
            if (remainder > 0) {
                keyBytes.add(it, strToBt(key.substring(it * 4 + 0, leng)));
            }
        }

        return keyBytes;
    }

    private int[] enc(int[] dataByte, int[] keyByte) {
        int[][] keys = generateKeys(keyByte);
        int[] ipByte = initPermute(dataByte);
        int[] ipLeft = new int[32];
        int[] ipRight = new int[32];
        int[] tempLeft = new int[32];
        int jt;
        int kt;
        int mt;
        int nt;
        for (kt = 0; kt < 32; ++kt) {
            ipLeft[kt] = ipByte[kt];
            ipRight[kt] = ipByte[32 + kt];
        }
        for (int i = 0; i < 16; ++i) {
            for (jt = 0; jt < 32; ++jt) {
                tempLeft[jt] = ipLeft[jt];
                ipLeft[jt] = ipRight[jt];
            }
            int[] key = new int[48];
            for (mt = 0; mt < 48; ++mt) {
                key[mt] = keys[i][mt];
            }
            int[] tempRight = xor(pPermute(sBoxPermute(xor(expandPermute(ipRight), key))), tempLeft);
            for (nt = 0; nt < 32; ++nt) {
                ipRight[nt] = tempRight[nt];
            }

        }

        int[] finalData = new int[64];
        for (int i = 0; i < 32; ++i) {
            finalData[i] = ipRight[i];
            finalData[32 + i] = ipLeft[i];
        }
        return finallyPermute(finalData);
    }

    private int[] pPermute(int[] sByte) {
        int[] pPermute = new int[32];
        pPermute[0] = sByte[15];
        pPermute[1] = sByte[6];
        pPermute[2] = sByte[19];
        pPermute[3] = sByte[20];
        pPermute[4] = sByte[28];
        pPermute[5] = sByte[11];
        pPermute[6] = sByte[27];
        pPermute[7] = sByte[16];
        pPermute[8] = sByte[0];
        pPermute[9] = sByte[14];
        pPermute[10] = sByte[22];
        pPermute[11] = sByte[25];
        pPermute[12] = sByte[4];
        pPermute[13] = sByte[17];
        pPermute[14] = sByte[30];
        pPermute[15] = sByte[9];
        pPermute[16] = sByte[1];
        pPermute[17] = sByte[7];
        pPermute[18] = sByte[23];
        pPermute[19] = sByte[13];
        pPermute[20] = sByte[31];
        pPermute[21] = sByte[26];
        pPermute[22] = sByte[2];
        pPermute[23] = sByte[8];
        pPermute[24] = sByte[18];
        pPermute[25] = sByte[12];
        pPermute[26] = sByte[29];
        pPermute[27] = sByte[5];
        pPermute[28] = sByte[21];
        pPermute[29] = sByte[10];
        pPermute[30] = sByte[3];
        pPermute[31] = sByte[24];
        return pPermute;
    }

    private int[] sBoxPermute(int[] expandByte) {
        int[] boxByte = new int[32];
        String bina = "";
        int[][] array1 = {{14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7},
            {0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8},
            {4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0},
            {15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13}};

        int[][] array2 = {{15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10},
            {3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5},
            {0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15},
            {13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9}};

        int[][] array3 = {{10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8},
            {13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1},
            {13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7},
            {1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12}};

        int[][] array4 = {{7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15},
            {13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9},
            {10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4},
            {3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14}};

        int[][] array5 = {{2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9},
            {14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6},
            {4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14},
            {11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3}};

        int[][] array6 = {{12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11},
            {10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8},
            {9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6},
            {4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13}};

        int[][] array7 = {{4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1},
            {13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6},
            {1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2},
            {6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12}};

        int[][] array8 = {{13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7},
            {1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2},
            {7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8},
            {2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11}};

        for (int mt = 0; mt < 8; ++mt) {
            int it;
            int jt;
            it = expandByte[mt * 6 + 0] * 2 + expandByte[mt * 6 + 5];
            jt = expandByte[mt * 6 + 1] * 2 * 2 * 2 + expandByte[mt * 6 + 2] * 2 * 2 + expandByte[mt * 6 + 3] * 2
                + expandByte[mt * 6 + 4];
            bina = getBinary(bina, array1, array2, array3, array4, array5, array6, array7, array8, mt, it, jt);
            if (bina == null) {
                continue;
            }
            boxByte[mt * 4 + 0] = Integer.parseInt(bina.substring(0, 1));
            boxByte[mt * 4 + 1] = Integer.parseInt(bina.substring(1, 2));
            boxByte[mt * 4 + 2] = Integer.parseInt(bina.substring(2, 3));
            boxByte[mt * 4 + 3] = Integer.parseInt(bina.substring(3, 4));
        }
        return boxByte;
    }

    private String getBinary(String binary, int[][] s1, int[][] s2, int[][] s3, int[][] s4, int[][] s5, int[][] s6,
        int[][] s7, int[][] s8, int m, int i, int j) {
        if (m == 0) {
            binary = getBoxBinary(s1[i][j]);
        } else if (m == 1) {
            binary = getBoxBinary(s2[i][j]);
        } else if (m == 2) {
            binary = getBoxBinary(s3[i][j]);
        } else if (m == 3) {
            binary = getBoxBinary(s4[i][j]);
        } else if (m == 4) {
            binary = getBoxBinary(s5[i][j]);
        } else if (m == 5) {
            binary = getBoxBinary(s6[i][j]);
        } else if (m == 6) {
            binary = getBoxBinary(s7[i][j]);
        } else if (m == 7) {
            binary = getBoxBinary(s8[i][j]);
        }
        return binary;
    }

    private String getBoxBinary(int i) {
        Map<Integer, String> map = new HashMap<Integer, String>();
        map.put(0, "0000");
        map.put(1, "0001");
        map.put(2, "0010");
        map.put(3, "0011");
        map.put(4, "0100");
        map.put(5, "0101");
        map.put(6, "0110");
        map.put(7, "0111");
        map.put(8, "1000");
        map.put(9, "1001");
        map.put(10, "1010");
        map.put(11, "1011");
        map.put(12, "1100");
        map.put(13, "1101");
        map.put(14, "1110");
        map.put(15, "1111");

        return null == map.get(i) ? "0000" : map.get(i);
    }

    private int[] xor(int[] byteOne, int[] byteTwo) {
        int[] xorByte = new int[byteOne.length];
        for (int i = 0; i < byteOne.length; ++i) {
            xorByte[i] = byteOne[i] ^ byteTwo[i];
        }
        return xorByte;
    }

    private int[] finallyPermute(int[] inputByte) {
        int[] finallyPermuteByte = new int[64];
        finallyPermuteByte[0] = inputByte[39];
        finallyPermuteByte[1] = inputByte[7];
        finallyPermuteByte[2] = inputByte[47];
        finallyPermuteByte[3] = inputByte[15];
        finallyPermuteByte[4] = inputByte[55];
        finallyPermuteByte[5] = inputByte[23];
        finallyPermuteByte[6] = inputByte[63];
        finallyPermuteByte[7] = inputByte[31];
        finallyPermuteByte[8] = inputByte[38];
        finallyPermuteByte[9] = inputByte[6];
        finallyPermuteByte[10] = inputByte[46];
        finallyPermuteByte[11] = inputByte[14];
        finallyPermuteByte[12] = inputByte[54];
        finallyPermuteByte[13] = inputByte[22];
        finallyPermuteByte[14] = inputByte[62];
        finallyPermuteByte[15] = inputByte[30];
        finallyPermuteByte[16] = inputByte[37];
        finallyPermuteByte[17] = inputByte[5];
        finallyPermuteByte[18] = inputByte[45];
        finallyPermuteByte[19] = inputByte[13];
        finallyPermuteByte[20] = inputByte[53];
        finallyPermuteByte[21] = inputByte[21];
        finallyPermuteByte[22] = inputByte[61];
        finallyPermuteByte[23] = inputByte[29];
        finallyPermuteByte[24] = inputByte[36];
        finallyPermuteByte[25] = inputByte[4];
        finallyPermuteByte[26] = inputByte[44];
        finallyPermuteByte[27] = inputByte[12];
        finallyPermuteByte[28] = inputByte[52];
        finallyPermuteByte[29] = inputByte[20];
        finallyPermuteByte[30] = inputByte[60];
        finallyPermuteByte[31] = inputByte[28];
        finallyPermuteByte[32] = inputByte[35];
        finallyPermuteByte[33] = inputByte[3];
        finallyPermuteByte[34] = inputByte[43];
        finallyPermuteByte[35] = inputByte[11];
        finallyPermuteByte[36] = inputByte[51];
        finallyPermuteByte[37] = inputByte[19];
        finallyPermuteByte[38] = inputByte[59];
        finallyPermuteByte[39] = inputByte[27];
        finallyPermuteByte[40] = inputByte[34];
        finallyPermuteByte[41] = inputByte[2];
        finallyPermuteByte[42] = inputByte[42];
        finallyPermuteByte[43] = inputByte[10];
        finallyPermuteByte[44] = inputByte[50];
        finallyPermuteByte[45] = inputByte[18];
        finallyPermuteByte[46] = inputByte[58];
        finallyPermuteByte[47] = inputByte[26];
        finallyPermuteByte[48] = inputByte[33];
        finallyPermuteByte[49] = inputByte[1];
        finallyPermuteByte[50] = inputByte[41];
        finallyPermuteByte[51] = inputByte[9];
        finallyPermuteByte[52] = inputByte[49];
        finallyPermuteByte[53] = inputByte[17];
        finallyPermuteByte[54] = inputByte[57];
        finallyPermuteByte[55] = inputByte[25];
        finallyPermuteByte[56] = inputByte[32];
        finallyPermuteByte[57] = inputByte[0];
        finallyPermuteByte[58] = inputByte[40];
        finallyPermuteByte[59] = inputByte[8];
        finallyPermuteByte[60] = inputByte[48];
        finallyPermuteByte[61] = inputByte[16];
        finallyPermuteByte[62] = inputByte[56];
        finallyPermuteByte[63] = inputByte[24];
        return finallyPermuteByte;
    }

    /**
     * Expand permute int [ ].
     *
     * @param rightData the right data
     * @return the int [ ]
     */
    public int[] expandPermute(int[] rightData) {
        int[] expandPermuteByte = new int[48];

        for (int i = 0; i < 8; ++i) {
            if (i == 0) {
                expandPermuteByte[i * 6] = rightData[31];
            } else {
                expandPermuteByte[i * 6] = rightData[i * 4 - 1];
            }
            expandPermuteByte[i * 6 + 1] = rightData[i * 4];
            expandPermuteByte[i * 6 + 2] = rightData[i * 4 + 1];
            expandPermuteByte[i * 6 + 3] = rightData[i * 4 + 2];
            expandPermuteByte[i * 6 + 4] = rightData[i * 4 + 3];
            if (i == 7) {
                expandPermuteByte[i * 6 + 5] = rightData[0];
            } else {
                expandPermuteByte[i * 6 + 5] = rightData[i * 4 + 4];
            }
        }
        return expandPermuteByte;
    }

    /**
     * Generate keys int [ ] [ ].
     *
     * @param keyByte the key byte
     * @return the int [ ] [ ]
     */
    public int[][] generateKeys(int[] keyByte) {
        int[] key = new int[56];
        int[][] keys = new int[16][48];

        int[] loop = {1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1};

        // 消除圈复杂度
        buildKey(key, keyByte);

        for (int i = 0; i < 16; ++i) {
            buildKey(key, loop, i);

            int[] tKeys = new int[48];
            tKeys[0] = key[13];
            tKeys[1] = key[16];
            tKeys[2] = key[10];
            tKeys[3] = key[23];
            tKeys[4] = key[0];
            tKeys[5] = key[4];
            tKeys[6] = key[2];
            tKeys[7] = key[27];
            tKeys[8] = key[14];
            tKeys[9] = key[5];
            tKeys[10] = key[20];
            tKeys[11] = key[9];
            tKeys[12] = key[22];
            tKeys[13] = key[18];
            tKeys[14] = key[11];
            tKeys[15] = key[3];
            tKeys[16] = key[25];
            tKeys[17] = key[7];
            tKeys[18] = key[15];
            tKeys[19] = key[6];
            tKeys[20] = key[26];
            tKeys[21] = key[19];
            tKeys[22] = key[12];
            tKeys[23] = key[1];
            tKeys[24] = key[40];
            tKeys[25] = key[51];
            tKeys[26] = key[30];
            tKeys[27] = key[36];
            tKeys[28] = key[46];
            tKeys[29] = key[54];
            tKeys[30] = key[29];
            tKeys[31] = key[39];
            tKeys[32] = key[50];
            tKeys[33] = key[44];
            tKeys[34] = key[32];
            tKeys[35] = key[47];
            tKeys[36] = key[43];
            tKeys[37] = key[48];
            tKeys[38] = key[38];
            tKeys[39] = key[55];
            tKeys[40] = key[33];
            tKeys[41] = key[52];
            tKeys[42] = key[45];
            tKeys[43] = key[41];
            tKeys[44] = key[49];
            tKeys[45] = key[35];
            tKeys[46] = key[28];
            tKeys[47] = key[31];

            dealLabel1202(keys, i, tKeys);
        }

        return keys;
    }

    private void dealLabel1202(int[][] keys, int i, int[] tempKey) {
        for (int j = 0; j < 48; j++) {
            keys[i][j] = tempKey[j];
        }

    }

    private void buildKey(int[] key, int[] loop, int i) {
        for (int j = 0; j < loop[i]; ++j) {
            int tempLeft = key[0];
            int tempRight = key[28];
            for (int k = 0; k < 27; ++k) {
                key[k] = key[k + 1];
                key[28 + k] = key[29 + k];
            }
            key[27] = tempLeft;
            key[55] = tempRight;
        }
    }

    private void buildKey(int[] key, int[] keyByte) {
        for (int it = 0; it < 7; ++it) {
            int jt = 0;
            for (int kt = 7; jt < 8; --kt) {
                key[it * 8 + jt] = keyByte[8 * kt + it];
                ++jt;
            }
        }
    }

    private int[] initPermute(int[] originalData) {
        int[] ipByte = new int[64];
        int mt = 1;
        int nt;
        int it = 0;
        for (nt = 0; it < 4; nt += 2) {
            int jt = 7;
            for (int kt = 0; jt >= 0; ++kt) {
                ipByte[it * 8 + kt] = originalData[jt * 8 + mt];
                ipByte[it * 8 + kt + 32] = originalData[jt * 8 + nt];

                --jt;
            }
            ++it;
            mt += 2;
        }

        return ipByte;
    }

    private static String bt4ToHex(String binary) {

        Map<String, String> hex = new HashMap<String, String>();
        hex.put("0000", "0");
        hex.put("0001", "1");
        hex.put("0010", "2");
        hex.put("0011", "3");
        hex.put("0100", "4");
        hex.put("0101", "5");
        hex.put("0110", "6");
        hex.put("0111", "7");
        hex.put("1000", "8");
        hex.put("1001", "9");
        hex.put("1010", "A");
        hex.put("1011", "B");
        hex.put("1100", "C");
        hex.put("1101", "D");
        hex.put("1110", "E");
        hex.put("1111", "F");

        return hex.get(binary) == null ? "" : hex.get(binary);

    }

    private String bt64ToHex(int[] byteData) {
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < 16; ++i) {
            StringBuffer bt = new StringBuffer();
            for (int j = 0; j < 4; ++j) {
                bt = bt.append(byteData[i * 4 + j]);
            }
            hex.append(bt4ToHex(bt.toString()));
        }
        return hex.toString();
    }
}

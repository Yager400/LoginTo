/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.common.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;

public class OTPCodeUtils {
    public static GoogleAuthenticatorKey getRandomKey() {
        GoogleAuthenticator auth = new GoogleAuthenticator();

        return auth.createCredentials();
    }

    public static String getOtpUrl(String playerName, String serverName, GoogleAuthenticatorKey key) {
        return GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(
                playerName,
                serverName,
                key
        );
    }

    public static BitMatrix getBitMatrix(String otpData, int size) {
        try {
            return new MultiFormatWriter().encode(otpData, BarcodeFormat.QR_CODE, size, size);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getQRCodeServerUrl(String otpData) {
        return String.format("https://api.qrserver.com/v1/create-qr-code/?data=%s&size=512x512&ecc=M&margin=30", otpData);
    }

}

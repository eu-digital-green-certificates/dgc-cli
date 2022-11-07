/*-
 * ---license-start
 * EU Digital Green Certificate Gateway Service / dgc-cli
 * ---
 * Copyright (C) 2021 - 2022 T-Systems International GmbH and all other contributors
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ---license-end
 */

package eu.europa.ec.dgc.cli.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import org.apache.commons.io.FileUtils;
import org.bouncycastle.jcajce.provider.asymmetric.x509.CertificateFactory;

public class CliUtils {

    /**
     * Reads a {@link PrivateKey} from a given {@link File}.
     *
     * @param inputFile the file to read from.
     * @return Private Key
     * @throws IOException if reading failed.
     */
    public static PrivateKey readKeyFromFile(File inputFile) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String pem = FileUtils.readFileToString(inputFile, StandardCharsets.UTF_8);
        int beginPrivateKey = pem.indexOf("-----BEGIN PRIVATE KEY-----");
        int endPrivateKey = pem.indexOf("-----END PRIVATE KEY-----");

        if (beginPrivateKey == -1 || endPrivateKey == -1) {
            System.err.println("Could not find PKCS#8 Private Key");
            return null;
        }


        String privateKeyBase64 = pem
                .substring(beginPrivateKey + 27, endPrivateKey - 1)
                .replaceAll("[\n\r ]", "");
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyBase64);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory ecKeyFactory = KeyFactory.getInstance("ECDSA");

        try {
            return ecKeyFactory.generatePrivate(spec);
        } catch (InvalidKeySpecException e) {
            KeyFactory rsaKeyFactory = KeyFactory.getInstance("RSA");
            return rsaKeyFactory.generatePrivate(spec);
        }
    }

    /**
     * Reads a {@link X509Certificate} from a given {@link File}.
     *
     * @param inputFile the file to read from.
     * @return Read Certificate
     * @throws IOException          if reading failed.
     * @throws CertificateException if certificate parsing failed.
     */
    public static X509Certificate readCertFromFile(File inputFile) throws IOException, CertificateException {
        CertificateFactory certificateFactory = new CertificateFactory();

        try (FileInputStream fileInputStream = new FileInputStream(inputFile)) {
            Certificate inputCert = certificateFactory.engineGenerateCertificate(fileInputStream);
            fileInputStream.close();

            if (inputCert instanceof X509Certificate) {
                return (X509Certificate) inputCert;
            } else {
                throw new CertificateException("Given Certificate does not contain a X.509 Certificate");
            }
        }
    }
}

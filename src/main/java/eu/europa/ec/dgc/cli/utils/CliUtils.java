/*-
 * ---license-start
 * EU Digital Green Certificate Gateway Service / dgc-cli
 * ---
 * Copyright (C) 2021 T-Systems International GmbH and all other contributors
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
import java.io.FileReader;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

public class CliUtils {

    /**
     * Reads a {@link PrivateKey} from a given {@link File}.
     *
     * @param inputFile the file to read from.
     * @return Private Key
     * @throws IOException if reading failed.
     */
    public static PrivateKey readKeyFromFile(File inputFile) throws IOException {
        FileReader fileReader = new FileReader(inputFile);
        PEMParser pemParser = new PEMParser(fileReader);

        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(pemParser.readObject());

        return converter.getPrivateKey(privateKeyInfo);
    }

    /**
     * Reads a {@link X509Certificate} from a given {@link File}.
     *
     * @param inputFile the file to read from.
     * @return Read Certificate
     * @throws IOException if reading failed.
     * @throws CertificateException if certificate parsing failed.
     */
    public static X509Certificate readCertFromFile(File inputFile) throws IOException, CertificateException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

        FileInputStream fileInputStream = new FileInputStream(inputFile);
        Certificate inputCert = certificateFactory.generateCertificate(fileInputStream);
        fileInputStream.close();

        if (inputCert instanceof X509Certificate) {
            return (X509Certificate) inputCert;
        } else {
            throw new CertificateException("Given Certificate does not contain a X.509 Certificate");
        }
    }
}

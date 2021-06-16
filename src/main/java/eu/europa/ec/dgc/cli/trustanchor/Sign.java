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

package eu.europa.ec.dgc.cli.trustanchor;

import static eu.europa.ec.dgc.cli.utils.CliUtils.readCertFromFile;
import static eu.europa.ec.dgc.cli.utils.CliUtils.readKeyFromFile;

import eu.europa.ec.dgc.signing.SignedCertificateMessageBuilder;
import eu.europa.ec.dgc.utils.CertificateUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.concurrent.Callable;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import picocli.CommandLine;

@CommandLine.Command(
    name = "sign",
    mixinStandardHelpOptions = true,
    description = "Signs a X.509 certificate as TrustAnchor for usage in DGC Gateway."
)
public class Sign implements Callable<Integer> {

    @CommandLine.Option(
        names = {"--inputCert", "-i"},
        description = "Input certificate you want to sign.",
        required = true
    )
    private File inputCertFile;

    @CommandLine.Option(
        names = {"--trustAnchorCert", "-c"},
        description = "TrustAnchor which should be used to sign.",
        required = true
    )
    private File signingCertFile;

    @CommandLine.Option(
        names = {"--trustAnchorkey", "-k"},
        description = "TrustAnchor's private key to use to sign.",
        required = true
    )
    private File signingKeyFile;

    @Override
    public Integer call() throws Exception {

        CertificateUtils certificateUtils = new CertificateUtils();

        X509Certificate inputCert = readCertFromFile(inputCertFile);

        X509Certificate signingCert = readCertFromFile(signingCertFile);
        PrivateKey signingCertPrivateKey = readKeyFromFile(signingKeyFile);
        X509CertificateHolder inputCertHolder = new X509CertificateHolder(inputCert.getEncoded());

        String trustAnchorSignature = new SignedCertificateMessageBuilder()
            .withSigningCertificate(new X509CertificateHolder(signingCert.getEncoded()), signingCertPrivateKey)
            .withPayload(inputCertHolder)
            .buildAsString(true);

        System.out.println();

        System.out.println("TrustAnchor Signature: " + trustAnchorSignature);
        System.out.println();
        System.out.println();

        System.out.println("Certificate Raw Data: " + Base64.getEncoder().encodeToString(inputCert.getEncoded()));
        System.out.println();
        System.out.println();

        System.out.println("Certificate Thumbprint: " + certificateUtils.getCertThumbprint(inputCert));
        System.out.println();
        System.out.println();

        String country = inputCertHolder.getSubject().getRDNs(X509ObjectIdentifiers.countryName)[0]
            .getFirst().getValue().toString();
        System.out.println("Certificate Country: " + country);

        return 0;
    }
}

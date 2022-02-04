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

package eu.europa.ec.dgc.cli.signing;

import static eu.europa.ec.dgc.cli.utils.CliUtils.readCertFromFile;
import static eu.europa.ec.dgc.cli.utils.CliUtils.readKeyFromFile;

import eu.europa.ec.dgc.signing.SignedCertificateMessageBuilder;
import java.io.File;
import java.io.FileWriter;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.concurrent.Callable;
import org.bouncycastle.cert.X509CertificateHolder;
import picocli.CommandLine;

@CommandLine.Command(
    name = "sign-cert",
    mixinStandardHelpOptions = true,
    description = "Calculates a signature for a X.509 certificate."
)
public class SignCert implements Callable<Integer> {

    @CommandLine.Option(
        names = {"--inputCert", "-i"},
        description = "Input certificate you want to sign.",
        required = true
    )
    private File inputCertFile;

    @CommandLine.Option(
        names = {"--cert", "-c"},
        description = "Certificate which should be used to sign.",
        required = true
    )
    private File signingCertFile;

    @CommandLine.Option(
        names = {"--key", "-k"},
        description = "Certificate's private key to use to sign.",
        required = true
    )
    private File signingKeyFile;

    @CommandLine.Option(
        names = {"--outputFile", "-o"},
        description = "The file where to write the signed certificate to.",
        required = true
    )
    private File outputFile;

    @CommandLine.Option(
        names = {"--detached", "-d"},
        description = "Output only the signature without encapsulated payload.",
        defaultValue = "false",
        showDefaultValue = CommandLine.Help.Visibility.ALWAYS
    )
    private Boolean detached;

    @Override
    public Integer call() throws Exception {

        X509Certificate inputCert = readCertFromFile(inputCertFile);

        X509Certificate signingCert = readCertFromFile(signingCertFile);
        PrivateKey signingCertPrivateKey = readKeyFromFile(signingKeyFile);

        String signedMessaged = new SignedCertificateMessageBuilder()
            .withSigningCertificate(new X509CertificateHolder(signingCert.getEncoded()), signingCertPrivateKey)
            .withPayload(new X509CertificateHolder(inputCert.getEncoded()))
            .buildAsString(detached);

        FileWriter fileWriter = new FileWriter(outputFile, false);
        fileWriter.write(signedMessaged);
        fileWriter.close();

        System.out.println("Success");

        return 0;
    }
}

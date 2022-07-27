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

import eu.europa.ec.dgc.signing.SignedByteArrayMessageParser;
import eu.europa.ec.dgc.utils.CertificateUtils;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.concurrent.Callable;
import org.bouncycastle.cert.X509CertificateHolder;
import picocli.CommandLine;

@CommandLine.Command(
    name = "validate-file",
    mixinStandardHelpOptions = true,
    description = "Validates a signature of a CMS signed message. This just validates the"
        + " integrity of the message itself. The certificate will not be checked. Checking of signing certificate"
        + " can be enabled with -c option."
)
public class ValidateFile implements Callable<Integer> {

    @CommandLine.ArgGroup(multiplicity = "1")
    Input input;

    static class Input {
        @CommandLine.Option(
            names = {"--inputString", "-s"},
            description = "Input String you want to validate.",
            required = true
        )
        String string;

        @CommandLine.Option(
            names = {"--inputFile", "-i"},
            description = "Input File with the String you want to verify.",
            required = true
        )
        File file;
    }

    @CommandLine.ArgGroup
    InputPayload inputPayload;

    static class InputPayload {
        @CommandLine.Option(
            names = {"--inputPayloadString", "-ps"},
            description = "Base64 encoded input String with payload to validate detached signature."
        )
        String string;

        @CommandLine.Option(
            names = {"--inputPayloadFile", "-p"},
            description = "Input File to validate detached signature."
        )
        File file;
    }

    @CommandLine.Option(
            names = {"--cert", "-c"},
            description = "Certificate which should be checked against the signature.",
            required = false
    )
    private File signingCertFile;

    @Override
    public Integer call() throws Exception {

        String cms;
        if (input.file != null) {
            cms = Files.readString(input.file.toPath());
        } else {
            cms = input.string;
        }

        byte[] payload = null;
        if (inputPayload.file != null) {
            payload = Base64.getEncoder().encode(Files.readAllBytes(inputPayload.file.toPath()));
        } else if (inputPayload.string != null) {
            payload = inputPayload.string.getBytes(StandardCharsets.UTF_8);
        }

        SignedByteArrayMessageParser parser;
        if (payload == null) {
            parser = new SignedByteArrayMessageParser(cms);
        } else {
            parser = new SignedByteArrayMessageParser(cms, payload);
        }

        if (parser.getParserState() != SignedByteArrayMessageParser.ParserState.SUCCESS) {
            System.out.println("Failed to validate message: " + parser.getParserState().toString());
        } else {
            if (parser.isSignatureVerified()) {
                System.out.println("Result: Valid");
            } else {
                System.out.println("Result: Invalid CMS");
            }

            CertificateUtils certificateUtils = new CertificateUtils();

            System.out.println("Signer Cert:");
            System.out.println("  Subject: " + parser.getSigningCertificate().getSubject().toString());
            System.out.println("  Issuer: " + parser.getSigningCertificate().getIssuer().toString());
            System.out.println("  Thumbprint: " + certificateUtils.getCertThumbprint(parser.getSigningCertificate()));

            if (signingCertFile != null) {
                X509CertificateHolder signingCert =
                        certificateUtils.convertCertificate(readCertFromFile(signingCertFile));
                System.out.println("  Matches Given Certificate: "
                        + (signingCert.equals(parser.getSigningCertificate()) ? "yes" : "no"));
            }

            System.out.println("Payload: " + parser.getPayload().length + " Bytes");
        }

        return 0;
    }
}

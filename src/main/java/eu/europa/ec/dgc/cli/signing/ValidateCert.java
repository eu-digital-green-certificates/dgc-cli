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

import eu.europa.ec.dgc.signing.SignedCertificateMessageParser;
import java.io.File;
import java.nio.file.Files;
import java.util.Base64;
import java.util.concurrent.Callable;
import org.bouncycastle.cert.X509CertificateHolder;
import picocli.CommandLine;

@CommandLine.Command(
    name = "validate-cert",
    mixinStandardHelpOptions = true,
    description = "Validates a signature of a CMS signed message. This just validates the"
        + " integrity of the message itself. The certificate will not be checked."
)
public class ValidateCert implements Callable<Integer> {

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
            description = "Input File with certificate to validate detached signature."
        )
        File file;
    }

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
            payload = Base64.getEncoder().encode(
                new X509CertificateHolder(Files.readAllBytes(inputPayload.file.toPath())).getEncoded());
        } else if (inputPayload.string != null) {
            payload = Base64.getEncoder().encode(
                new X509CertificateHolder(Base64.getDecoder().decode(input.string)).getEncoded());
        }

        SignedCertificateMessageParser parser;
        if (payload == null) {
            parser = new SignedCertificateMessageParser(cms);
        } else {
            parser = new SignedCertificateMessageParser(cms, payload);
        }

        if (parser.getParserState() != SignedCertificateMessageParser.ParserState.SUCCESS) {
            System.out.println("Failed to validate message: " + parser.getParserState().toString());
        } else {
            if (parser.isSignatureVerified()) {
                System.out.println("Result: Valid");
            } else {
                System.out.println("Result: Invalid CMS");
            }

            System.out.println("Signer Cert: " + parser.getSigningCertificate().getSubject().toString());
            System.out.println("Payload Cert: " + parser.getPayload().getSubject().toString());
        }

        return 0;
    }
}

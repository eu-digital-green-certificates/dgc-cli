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

package eu.europa.ec.dgc.cli.signing;

import eu.europa.ec.dgc.signing.SignedCertificateMessageParser;
import eu.europa.ec.dgc.signing.SignedStringMessageParser;
import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.Callable;
import picocli.CommandLine;

@CommandLine.Command(
    name = "validate-string",
    mixinStandardHelpOptions = true,
    description = "Validates a signature of a CMS signed message containing a String."
)
public class ValidateString implements Callable<Integer> {

    @CommandLine.Option(
        names = {"--input", "-i"},
        description = "Input File with message to validate",
        required = true
    )
    private File inputFile;

    @Override
    public Integer call() throws Exception {

        byte[] bytes = Files.readAllBytes(inputFile.toPath());
        SignedStringMessageParser parser = new SignedStringMessageParser(bytes);

        if (parser.getParserState() != SignedCertificateMessageParser.ParserState.SUCCESS) {
            System.out.println("Failed to validate message: " + parser.getParserState().toString());
        } else {
            if (parser.isSignatureVerified()) {
                System.out.println("Result: Valid");
            } else {
                System.out.println("Result: Invalid CMS");
            }

            System.out.println("Signer Cert: " + parser.getSigningCertificate().getSubject().toString());
            System.out.println("String: " + parser.getPayload());
        }

        return 0;
    }
}

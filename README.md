<h1 align="center">
    EU Digital Green Certificates CLI
</h1>

<p align="center">
    <a href="https://sonarcloud.io/dashboard?id=eu-digital-green-certificates_dgc-cli" title="Quality Gate Status"><img src="https://sonarcloud.io/api/project_badges/measure?project=eu-digital-green-certificates_dgc-cli&metric=alert_status"></a>
    <a href="https://github.com/eu-digital-green-certificates/dgc-cli/actions/workflows/ci-main.yml" title="ci-main.yml"><img src="https://github.com/eu-digital-green-certificates/dgc-cli/actions/workflows/ci-main.yml/badge.svg"></a>
    <a href="/../../commits/" title="Last Commit"><img src="https://img.shields.io/github/last-commit/eu-digital-green-certificates/dgc-cli?style=flat"></a>
    <a href="/../../issues" title="Open Issues"><img src="https://img.shields.io/github/issues/eu-digital-green-certificates/dgc-cli?style=flat"></a>
    <a href="./LICENSE" title="License"><img src="https://img.shields.io/badge/License-Apache%202.0-green.svg?style=flat"></a>
</p>

<p align="center">
  <a href="#about">About</a> •
  <a href="#installation">Installation</a> •
  <a href="#development">Development</a> •
  <a href="#support-and-feedback">Support</a> •
  <a href="#how-to-contribute">Contribute</a> •
  <a href="#contributors">Contributors</a> •
  <a href="#licensing">Licensing</a>
</p>

## About

The DGC-CLI provides a toolchain for manually interacting with DGC Components.
This list gives an overview of the possibilities. For detailed information use the built-in help.

* Signing 
  * Envelope a Certificate in signed CMS message (for DGC Gateway upload)
  * Verify enveloped certificate in signed CMS message
* TrustAnchor
  * Sign a certificate with a TrustAnchor for usage in DGC Gateway

## Installation

You need to have a JRE (minimum version 11) installed on your system. Also the java executable should be available in your PATH variable.

1. Download latest build from [Releases](https://github.com/eu-digital-green-certificates/dgc-cli/releases/tag/latest)
2. Extract ZIP-File to a path of your choice
3. Add the Path to your PATH variable
4. Check your installation
   ```
   dgc --version
   ```

## Development

Whether you cloned or downloaded the 'zipped' sources you will either find the sources in the chosen checkout-directory or get a zip file with the source code, which you can expand to a folder of your choice.

In either case open a terminal pointing to the directory you put the sources in. The local build process is described afterwards depending on the way you choose.

### Maven based build

Building this project is done with maven.

```shell
mvnw install
```

Will download all required dependencies, build the project and stores the artifact in your local repository.

## Support and feedback

The following channels are available for discussions, feedback, and support requests:

| Type                     | Channel                                                |
| ------------------------ | ------------------------------------------------------ |
| **Gateway issues**    | <a href="https://github.com/eu-digital-green-certificates/dgc-gateway/issues" title="Open Issues"><img src="https://img.shields.io/github/issues/eu-digital-green-certificates/dgc-gateway?style=flat"></a>  |
| **CLI issues**    | <a href="/../../issues" title="Open Issues"><img src="https://img.shields.io/github/issues/eu-digital-green-certificates/dgc-cli?style=flat"></a>  |
| **DGC Lib issues**    | <a href="https://github.com/eu-digital-green-certificates/dgc-lib/issues" title="Open Issues"><img src="https://img.shields.io/github/issues/eu-digital-green-certificates/dgc-lib?style=flat"></a>  |
| **Other requests**    | <a href="mailto:opensource@telekom.de" title="Email DGC Team"><img src="https://img.shields.io/badge/email-DGC%20team-green?logo=mail.ru&style=flat-square&logoColor=white"></a>   |

## How to contribute  

Contribution and feedback is encouraged and always welcome. For more information about how to contribute, the project structure, as well as additional contribution information, see our [Contribution Guidelines](./CONTRIBUTING.md). By participating in this project, you agree to abide by its [Code of Conduct](./CODE_OF_CONDUCT.md) at all times.

## Contributors  

Our commitment to open source means that we are enabling -in fact encouraging- all interested parties to contribute and become part of its developer community.

## Licensing

Copyright (C) 2021 T-Systems International GmbH and all other contributors

Licensed under the **Apache License, Version 2.0** (the "License"); you may not use this file except in compliance with the License.

You may obtain a copy of the License at https://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the [LICENSE](./LICENSE) for the specific language governing permissions and limitations under the License.

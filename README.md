# CDBC Bridge


Simple CorDapp for interfacing Barclays CBDC API used on the Barclays CDBC Hackaton 2022


## Structure

It's using default CorDapp Template Kotlin. On top of that there is one module `cbdc-bridge` where
is the bridge used to easy access Barclays APIs.

During the hackaton contracts and states can be created in `contracts` module and flows can be
created in the `workflows` module.

## Getting started

In order to operate with provided bridge functionalities, you should use the provided auth token for your team and set it as a system environment variable. (edited)

To be able to use the bridge with the configured token `val AUTH_TOKEN: String = System.getenv("BARCLAYS_AUTH_TOKEN")`.

In Linux you can set it into you `.profile` file under the home directory like that:

`export BARCLAYS_AUTH_TOKEN="the-actual-token"` and reboot your computer.

In Windows you can go through the menu to Environment Variable and set it from there.

https://docs.oracle.com/en/database/oracle/machine-learning/oml4r/1.5.1/oread/creating-and-modifying-environment-variables-on-windows.html#GUID-DD6F9982-60D5-48F6-8270-A27EC53807D0

## Pre-Event Exercises

Execute the `PreEventTests` to see that you are able to interact with the Barclays' API.
# J2TS
J2TS is a light-weight library which allows for conversion of 
java classes, interfaces, enums and FIs into typescript definitions (.d.ts files)

## Usage
First configure J2TS in via the available fields on the `JTTSConfig` class, and then just run
`TSCodeGen.writeFile()` with any class you want.
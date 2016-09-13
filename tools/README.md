# Script Naming Conventions

Several scripts are used to retrieve RDF graphs of certain shapes.

Script name describes the composition of a RDF chain. The following abbreviation are used:

x{n} = xx...x repeat n times

c = class

i = instance

p = nodes connected by any property

np = nodes connected by numeric property

e.g. ci4cp.sh = Retrieve all triples that form a chain of a length of 6, with the start and end nodes being classes,  intermediate nodes being instances, and connected by any property.

Scripts prefixed by *lst* accept a list of inputs from a file.

`execQ.sh` is used to execute SPARQL queries by these scripts.

`execQ [options...] -G <url> <template> [args...]`

Execute a SPARQL query specified by a template that is filled by arguments.

*options*: cUrl options, e.g. -H 'Accept: text/csv' sets HTTP header. See `curl --help` for more information. Only support options in the form `-p, --parameter val`.

*url*: SPARQL endpoint URL.

*template*: A file of SPARQL template. See examples in `./queries`.

*args*: Optional parameters to fill the template, e.g. $1 in the template is replaced by the first value of *args*.

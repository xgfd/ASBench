self=$0
sh execQ.sh -H 'Accept: text/csv' -G "${1?SPARQL endpoint is required}" ./queries/${self%%.*}.rq ${2?A predicate is required}
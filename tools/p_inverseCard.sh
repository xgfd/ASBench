self=$0
sh execQ.sh -H 'Accept: text/csv' -G 'http://dbpedia.org/sparql' ./queries/${self%%.*}.rq \<${1?A predicate is required}\>